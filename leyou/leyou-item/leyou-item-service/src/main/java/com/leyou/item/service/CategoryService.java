package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }
    /*
     * 描述：商品列表 查询多个id的方法类  分类的三级类目
     * @Author 陈威
     * @Date 19:41 2020/3/22
     * @Param [ids]
     *
     **/
    public List<String>queryNameByIds(List<Long> ids){
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
          return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
    /*
     * 描述：品牌管理修改品牌信息的品牌信息回显
     * @Author 陈威
     * @Date 19:37 2020/3/22
     * @Param [bid]
     *
     **/
    public List<Category> selectbrand(Long bid) {
        return categoryMapper.selectbrand(bid);
    }
    /**
     * 第十三天：根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryMapper.selectByPrimaryKey(id);
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}