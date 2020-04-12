package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {



    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    //商品列表根据分类id查询品牌
    @Select("select * from tb_brand a inner join tb_category_brand b on a.id=b.brand_id where b.category_id=#{cid}")
    List<Brand> selectBrandsByCid(Long cid);

    //品牌修改
    @Update("UPDATE tb_category_brand SET category_id = #{cid} where brand_id = #{bid}" )
    void updateCategoryBrand(@Param("cid") Long categoryId, @Param("bid") Long id);

    //删除品牌
    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryBrandByBid(@Param("bid") Long bid);
}
