package com.leuou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leuou.search.client.BrandClient;
import com.leuou.search.client.CategoryClient;
import com.leuou.search.client.GoodsClient;
import com.leuou.search.client.SpecificationClient;
import com.leuou.search.pojo.Goods;
import com.leuou.search.pojo.SearchRequest;
import com.leuou.search.pojo.SearchResult;
import com.leuou.search.repository.GoodsReponsitory;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname SearchService
 * @Description TODO
 * @Date 2020/3/26 11:13
 * @Created by chenwei
 */
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsReponsitory goodsReponsitory;
    //json工具
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public SearchResult search(SearchRequest request) {
        if (StringUtils.isBlank(request.getKey())) {
            return null;
        }
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //查询条件
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery=  buildBoolQueryBUilder(request);
        //添加查询条件  匹配查询  operator(Operator.AND)  多个查询条件
        queryBuilder.withQuery(basicQuery);
        //添加分页,分页页码从0开始
        queryBuilder.withPageable(PageRequest.of(request.getPage() - 1, request.getSize()));
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            //如果不为空则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
        //添加分类和品牌的聚合
        String categoryAggname = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggname).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //执行查询获取结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsReponsitory.search(queryBuilder.build());
        //获取分类聚合结果集并解析
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggname));
        //获取品牌聚合结果集并解析
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));
        //判断是否只是一个分类，只有一个分类时才做规格参数聚合
        List<Map<String, Object>> specs = null;
        if (categories.size() == 1) {
            specs = getParamAggResult((Long)categories.get(0).get("id"), basicQuery);
        }

        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(), categories, brands, specs);
    }
    /*
     * 描述：第13天：构建布尔查询  规格参数过滤
     * @Author 陈威
     * @Date 16:23 2020/4/1
     * @Param [request]
     *
     **/
    private BoolQueryBuilder buildBoolQueryBUilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //给布尔查询添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        //添加过滤条件

        //获取用户选择的过滤信息
        Map<String, Object> filter = request.getFilter();
        //循环选择的规格参数信息
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            //获得
            String key = entry.getKey();
            if (StringUtils.equals("品牌", key)){
                key= "brandId";
            }else  if(StringUtils.equals("分类",key)){
                key="cid3";
            }else {
                key="specs."+key+".keyword" ;
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }

        return boolQueryBuilder;


    }

    /*
     * 描述：根据查询条件来聚合规格参数
     * @Author 陈威
     * @Date 17:47 2020/3/31
     * @Param [id, basicQuery]
     *
     **/
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //自定义查询对象构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParamsByGid(null, cid, null, true);
        //添加规格参数的聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        //添加聚合结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        //执行聚合查询,获取聚合结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsReponsitory.search(queryBuilder.build());
        //解析聚合结果集
        List<Map<String, Object>> specs = new ArrayList<>();
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            //初始化一个map {key：规格参数名，options：规格参数值}
            Map<String, Object> map = new HashMap<>();

            //初始化一个options集合来收集桶中的key
            List<String> options = new ArrayList<>();
            //获取聚合
            StringTerms terms = (StringTerms)entry.getValue();
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("k", entry.getKey());
            map.put("options", options);
            specs.add(map);
        }
        return specs;
    }


    /*
     * 描述：解析品牌的聚合结果集
     * @Author 陈威
     * @Date 10:47 2020/3/30
     * @Param [aggregation]
     *
     **/
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //获取聚合里面的桶 在通过桶获得brandId去查询brand的内容 返回数组  解析聚合结果集中的桶，把桶的集合转化成id的集合
        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue()); //根据ids查询品牌
        }).collect(Collectors.toList());
    }

    /*
     * 描述：解析分类的聚合结果集
     * @Author 陈威
     * @Date 10:47 2020/3/30
     * @Param [aggregation]
     *
     **/
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //获取桶的集合，遍历转化成List<map()>集合
        return terms.getBuckets().stream().map(bucket -> {
            //初始化一个map
            Map<String, Object> map = new HashMap<>();
            //获取桶中的分类id，聚合里的key值
            Long id = bucket.getKeyAsNumber().longValue();
            //根据分类id查询分类名称
            List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
    }


    public Goods buildGoods(Spu spu) throws IOException {
        //根据分类id查询分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //根据品牌id查询平拍名称
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //根据spuid查询所有的sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        //初始化一个价格集合，收集所有价格的信息
        List<Long> prices = new ArrayList<>();
        //收集sku必要的字段信息
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("price", sku.getPrice());
            map.put("title", sku.getTitle());
            //获取sku中的图片，数据库的图片可能是多张，多张以","进行分割,所以也以","切割返回图片数据，下标获取第一张图片
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            //把该map放进list中
            skuMapList.add(map);
        });
        //根据spu中的cid3查询出所有的规格参数
        List<SpecParam> params = this.specificationClient.queryParamsByGid(null, spu.getCid3(), null, true);

        //根据spuid获取spu——detail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());
        //把通用的规格参数值进行反序列化
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //把特殊的规格参数值进行反序列化
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        Map<String, Object> specs = new HashMap<>();
        params.forEach(param -> {
            //判断规格参数的类型是否是通用的规格参数
            if (param.getGeneric()) {
                //如果是通用类型的参数从genericSpecMap里获取规格参数值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //判断是否是数值类型参数 true或false  如果是就返回一个区间 设置规格参数的集合
                if (param.getNumeric()) {
                    value = chooseSegment(value, param);
                }
                specs.put(param.getName(), value);
            } else {
                //如果是特殊类型的规格参数从specialSpecMap里获取规格参数值
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(), value);
            }
        });
        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段需要分类名称及品牌名称
        goods.setAll(spu.getTitle() + "" + StringUtils.join(names, "") + "" + brand.getName());
        //获取spu下的所有sku的价格
        goods.setPrice(prices);
        //获取spu下的所有sku并序列化成josn字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有的查询的规格参数{name,value}
        goods.setSpecs(specs);


        return goods;


    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    public void save(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsReponsitory.save(goods);

    }

    public void delete(Long id) {
        this.goodsReponsitory.deleteById(id);
    }
}
