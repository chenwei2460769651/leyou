package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname SpecifcationService
 * @Description TODO
 * @Date 2020/3/21 17:15
 * @Created by chenwei
 */
@Service
public class SpecifcationService {
    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    /*
     * 描述：根据分类id查询参数组
     * @Author 陈威
     * @Date 17:22 2020/3/21
     * @Param [cid]
     *
     **/
    public List<SpecGroup> queryGroupByCid(long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return this.groupMapper.select(record);
    }

    /*
     * 描述：新增规格组
     * @Author 陈威
     * @Date 20:27 2020/3/21
     * @Param [specGroup]
     *
     **/
    public void saveSpecGroup(SpecGroup specGroup) {
        this.groupMapper.insert(specGroup);
    }

    /*
     * 描述：删除规格组
     * @Author 陈威
     * @Date 20:36 2020/3/21
     * @Param [id]
     *
     **/
    public void deleteSpecGroup(Long id) {
        this.groupMapper.deleteByPrimaryKey(id);
    }

    /*
     * 描述：修改规格组
     * @Author 陈威
     * @Date 20:41 2020/3/21
     * @Param [specGroup]
     *
     **/
    public void updateSpectGroup(SpecGroup specGroup) {
        this.groupMapper.updateByPrimaryKey(specGroup);
    }

    /*
     * 描述：查询规格参数组
     * @Author 陈威
     * @Date 21:03 2020/3/21
     * @Param [gid]
     *
     **/
    public List<SpecParam> queryParamByGid(Long gid, Long cid, Boolean genric, Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setGeneric(genric);
        param.setSearching(searching);
        return this.paramMapper.select(param);
    }

    /*
     * 描述：新增规格参数组
     * @Author 陈威
     * @Date 21:11 2020/3/21
     * @Param [specParam]
     *
     **/
    public void saveParam(SpecParam specParam) {
        this.paramMapper.insert(specParam);
    }

    /*
     * 描述：更新规格参数组
     * @Author 陈威
     * @Date 21:15 2020/3/21
     * @Param [specParam]
     *
     **/
    public void updateparam(SpecParam specParam) {
        this.paramMapper.updateByPrimaryKey(specParam);
    }

    /*
     * 描述：删除规格参数组
     * @Author 陈威
     * @Date 21:20 2020/3/21
     * @Param [id]
     *
     **/
    public void deleteSpecParam(Long id) {
        this.paramMapper.deleteByPrimaryKey(id);
    }

    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> groups=this.queryGroupByCid(cid);
        groups.forEach(group ->{
            List<SpecParam> params = this.queryParamByGid(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }
}
