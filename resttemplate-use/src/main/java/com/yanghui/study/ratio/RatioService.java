package com.yanghui.study.ratio;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yanghui
 * @date 2019-4-28
 */
@org.springframework.stereotype.Service
public class RatioService {

    @Autowired
    private Mapper mapper;

    @EnableRatio
    public String flow(JSONObject param){
        return mapper.flow(param);
    }

    public String income(){
        return mapper.income();
    }
}
