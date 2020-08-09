package com.yanghui.study.ratio;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanghui
 * @date 2019-4-28
 */
@RestController
@RequestMapping("test")
public class RatioController {

    @Autowired
    private RatioService service;

    @RequestMapping("flow")
    public String flow(@RequestBody JSONObject param){
        return service.flow(param);
    }

    @RequestMapping("income")
    public String income(@RequestBody JSONObject param){
        return service.income();
    }
}
