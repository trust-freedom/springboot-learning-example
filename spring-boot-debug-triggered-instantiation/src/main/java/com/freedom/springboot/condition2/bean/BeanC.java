package com.freedom.springboot.condition2.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanC {
    private static final Logger logger = LoggerFactory.getLogger(BeanC.class);

    private BeanDInterface d;

    public BeanC(BeanDInterface d){
        //logger.info("调用 BeanC 构造方法，d=" + d);
        this.d = d;
    }

}
