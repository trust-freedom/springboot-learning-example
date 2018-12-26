package com.freedom.springboot.condition1.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanB1 implements BeanBInterface{
    private static final Logger logger = LoggerFactory.getLogger(BeanA.class);

    public BeanB1(){
        logger.info("调用 BeanD1 构造方法");
    }

}
