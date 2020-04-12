package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * @Classname GoodsHtmlService
 * @Description TODO
 * @Date 2020/4/3 14:37
 * @Created by chenwei
 */
@Service
public class GoodsHtmlService {
    @Autowired
    private TemplateEngine engine;
    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId) {
        //初始化运行上下文
        Context context = new Context();
        PrintWriter printWriter = null;
        try {
            //设置数据模型
            context.setVariables(this.goodsService.loadData(spuId));
            //io流  把html文件生成到服务器本地
            File file = new File("C:\\nginx\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            this.engine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {  //释放这个io流
            if(printWriter!=null){
                printWriter.close();
            }
        }
    }

    public void deleteHtml(Long id) {
        File file = new File("C:\\\\nginx\\\\nginx-1.14.0\\\\html\\\\item\\\\\" + id + \".html");
        file.deleteOnExit();
    }
}
