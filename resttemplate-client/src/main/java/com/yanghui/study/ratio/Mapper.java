package com.yanghui.study.ratio;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * @author yanghui
 * @date 2019-4-28
 */
@Component
public class Mapper {

    public String flow(JSONObject param){
        return param.toString();
    }

    public String income() {
        return "income";
    }
}
