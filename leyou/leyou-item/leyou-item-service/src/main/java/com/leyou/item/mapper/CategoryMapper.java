package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {
    @Select("SELECT * from tb_category WHERE id IN (select category_id FROM tb_category_brand where brand_id = #{bid})")
    List<Category> selectbrand(@Param("bid") Long bid);
}
