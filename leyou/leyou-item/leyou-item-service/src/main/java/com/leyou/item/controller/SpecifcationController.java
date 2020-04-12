package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecifcationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname SpecifcationController
 * @Description TODO
 * @Date 2020/3/21 17:16
 * @Created by chenwei
 */
@Controller
@RequestMapping("spec")
public class SpecifcationController {

    @Autowired
    private SpecifcationService specifcationService;


    /*
     * 描述：根据分类id查询参数组
     * @Author 陈威
     * @Date 17:22 2020/3/21
     * @Param [cid]
     *
     **/
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") long cid){

        List<SpecGroup> groups= this.specifcationService.queryGroupByCid(cid);
        if(CollectionUtils.isEmpty(groups)){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /*
     * 描述：新增规格组
     * @Author 陈威
     * @Date 20:27 2020/3/21
     * @Param [specGroup]
     *
     **/
    @PostMapping("group")
    public  ResponseEntity<Void> saveSpecGroup (@RequestBody SpecGroup specGroup){
        this.specifcationService.saveSpecGroup(specGroup);
        return ResponseEntity.ok().build();
    }

    /*
     * 描述：删除规格组
     * @Author 陈威
     * @Date 20:36 2020/3/21
     * @Param [id]
     *
     **/
    @DeleteMapping("group/{id}")
    public  ResponseEntity<Void> deleteSpecGroup (@PathVariable ("id")Long id){
        this.specifcationService.deleteSpecGroup(id);
        return ResponseEntity.ok().build();
    }

    /*
     * 描述：修改规格组
     * @Author 陈威
     * @Date 20:41 2020/3/21
     * @Param [specGroup]
     *
     **/
    @PutMapping("group")
    public  ResponseEntity<Void> updateSpectGroup(@RequestBody SpecGroup specGroup){
        this.specifcationService.updateSpectGroup(specGroup);
        return ResponseEntity.ok().build();
    }

    /*
     * 描述：查询参数组
     * @Author 陈威
     * @Date 21:03 2020/3/21
     * @Param [gid]
     *
     **/
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamsByGid(
            @RequestParam(value = "gid" ,required = false)Long gid,//参数组id
            @RequestParam(value = "cid" ,required = false)Long cid,//商品分类id
            @RequestParam(value = "genric" ,required = false)Boolean genric,//是否是sku通用属性
            @RequestParam(value = "searching" ,required = false)Boolean searching//是否用于搜索过滤


    ){
        List<SpecParam> params= this.specifcationService.queryParamByGid(gid,cid,genric,searching);
        if(CollectionUtils.isEmpty(params)){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /*
     * 描述：新增规格参数组
     * @Author 陈威
     * @Date 21:11 2020/3/21
     * @Param [specParam]
     *
     **/
    @PostMapping("param")
    public ResponseEntity<Void> saveParam (@RequestBody SpecParam specParam){
        this.specifcationService.saveParam(specParam);
        return  ResponseEntity.ok().build();
    }
    /*
     * 描述：更新规格参数组
     * @Author 陈威
     * @Date 21:15 2020/3/21
     * @Param [specParam]
     *
     **/
    @PutMapping("param")
    private  ResponseEntity<Void> updateparam(@RequestBody SpecParam specParam){
        this.specifcationService.updateparam(specParam);
        return  ResponseEntity.ok().build();
    }
    /*
     * 描述：删除规格参数组
     * @Author 陈威
     * @Date 21:20 2020/3/21
     * @Param [id]
     *
     **/
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id") Long id) {
        this.specifcationService.deleteSpecParam(id);
        return ResponseEntity.ok().build();

    }
    /*
     * 描述：通过查询到规格组在查询组下的参数
     * @Author 陈威
     * @Date 14:01 2020/4/2
     * @Param [cid]
     *
     **/
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid") Long cid){
        List<SpecGroup> list = this.specifcationService.queryGroupsWithParam(cid);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    }




