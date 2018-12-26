package com.freedom.springboot.condition1.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanA {
    private static final Logger logger = LoggerFactory.getLogger(BeanA.class);

    private BeanBInterface b;

    public BeanA(BeanBInterface b){
        //logger.info("调用 BeanC 构造方法，b=" + b);
        this.b = b;
    }

}
