package com.yanghui.study.ratio;

import java.lang.annotation.*;

/**
 * @author yanghui
 * @date 2019-4-26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableRatio {

    /**
     * 比例字段后缀
     */
    String suffix() default "HRate";

    /**
     * group by 字段
     */
    String group() default "subsystemCode";
}


