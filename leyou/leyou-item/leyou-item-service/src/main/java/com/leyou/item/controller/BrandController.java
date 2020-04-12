package com.leyou.item.controller;

import com.leyou.commom.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据查询条件分页并排序查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy", required = false)String sortBy,
            @RequestParam(value = "desc", required = false)Boolean desc
    ){
        PageResult<Brand> result = this.brandService.queryBrandsByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids")List<Long>cids){
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /*
     * 描述：商品列表新增商品基于分类的品牌查询
     * @Author 陈威
     * @Date 14:24 2020/3/22
     * @Param [cid]
     *
     **/
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>>queryBrandsByCid(@PathVariable("cid")Long cid){
        List<Brand> brands=  this.brandService.queryBrandsByCid(cid);
        if(CollectionUtils.isEmpty(brands)){
            return  ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(brands);

    }
    /**
     * 品牌的修改
     *
     * @param categories
     * @param brand
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(@RequestParam("cids")List<Long>categories,Brand brand){
        this.brandService.updateBrand(categories,brand);
        return ResponseEntity.ok(null);
    }
    /*
     * 描述：删除品牌
     * @Author 陈威
     * @Date 20:33 2020/3/22
     * @Param [bid]
     *
     **/
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid) {
        brandService.deleteBrand(bid);
        return ResponseEntity.ok().build();
    }
    /*
     * 描述：根据商品品牌id，查询商品的品牌名称
     * @Author 陈威
     * @Date 22:50 2020/3/25
     * @Param [id]
     *
     **/
    @GetMapping("{id}")
    public ResponseEntity<Brand>queryBrandById(@PathVariable("id")Long id){
      Brand brand=  this.brandService.queryBrandById(id);
      if(brand==null){
          return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(brand);
    }
}