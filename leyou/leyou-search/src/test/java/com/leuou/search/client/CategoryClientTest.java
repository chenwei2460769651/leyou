package com.leuou.search.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @Classname CategoryClientTest
 * @Description TODO
 * @Date 2020/3/26 12:34
 * @Created by chenwei
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testQueryCategories() {
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L));
        names.forEach(System.out::println);
    }


}
