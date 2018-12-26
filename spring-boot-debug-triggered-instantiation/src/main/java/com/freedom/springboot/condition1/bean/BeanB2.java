package com.freedom.springboot.condition1.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanB2 implements BeanBInterface{
    private static final Logger logger = LoggerFactory.getLogger(BeanA.class);

    public BeanB2(){
        logger.info("调用 BeanD2 构造方法");
    }

}
