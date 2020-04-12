package com.leuou.search.controller;

import com.leuou.search.pojo.SearchRequest;
import com.leuou.search.pojo.SearchResult;
import com.leuou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Classname SearchController
 * @Description TODO
 * @Date 2020/3/27 20:42
 * @Created by chenwei
 */
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request){
        SearchResult result= this.searchService.search(request);
        if(result == null|| CollectionUtils.isEmpty(result.getItems())){
            return  ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(result);


    }


}
