package com.freedom.springboot.condition2.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanD1 implements BeanDInterface {
    private static final Logger logger = LoggerFactory.getLogger(BeanC.class);

    public BeanD1(){
        logger.info("调用 BeanD1 构造方法");
    }

}
