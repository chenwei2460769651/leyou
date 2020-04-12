package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Classname SpecifcationController
 * @Description TODO
 * @Date 2020/3/21 17:16
 * @Created by chenwei
 */
@RequestMapping("spec")
public interface SpecifcationApi {

    /*
     * 描述：根据条件查询参数组
     * @Author 陈威
     * @Date 21:03 2020/3/21
     * @Param [gid]
     *
     **/
    @GetMapping("params")
    public List<SpecParam> queryParamsByGid(
            @RequestParam(value = "gid", required = false) Long gid,//参数组id
            @RequestParam(value = "cid", required = false) Long cid,//商品分类id
            @RequestParam(value = "genric", required = false) Boolean genric,//是否是sku通用属性
            @RequestParam(value = "searching", required = false) Boolean searching//是否用于搜索过滤
    );

    /*
     * 描述：根据分类id查询参数组
     * @Author 陈威
     * @Date 17:22 2020/3/21
     * @Param [cid]
     *
     **/
    @GetMapping("groups/{cid}")
    public List<SpecGroup> queryGroupByCid(@PathVariable("cid") long cid);

    /*
     * 描述：通过查询到规格组在查询组下的参数
     * @Author 陈威
     * @Date 14:01 2020/4/2
     * @Param [cid]
     *
     **/
    @GetMapping("{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid") Long cid);

}


