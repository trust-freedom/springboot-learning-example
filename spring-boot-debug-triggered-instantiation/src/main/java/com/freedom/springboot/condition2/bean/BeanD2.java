package com.freedom.springboot.condition2.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanD2 implements BeanDInterface {
    private static final Logger logger = LoggerFactory.getLogger(BeanC.class);

    public BeanD2(){
        logger.info("调用 BeanD2 构造方法");
    }

}
