package com.freedom.springboot.condition1.config;

import com.freedom.springboot.condition1.bean.BeanA;
import com.freedom.springboot.condition1.bean.BeanB1;
import com.freedom.springboot.condition1.bean.BeanB2;
import com.freedom.springboot.condition1.bean.BeanBInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

//@Configuration
public class InstantiationConfig {
    private static final Logger logger = LoggerFactory.getLogger(InstantiationConfig.class);


    @Bean
    public BeanA beanA(BeanBInterface b){
        //logger.info("注册 BeanA被调用，b=" + b);
        return new BeanA(b);
    }



    @ConditionalOnMissingBean(value = BeanBInterface.class)
    @RefreshScope
    @Bean
    @Lazy
    public BeanB1 beanB1(){
        logger.info("注册 BeanD1 被调用");
        return new BeanB1();
    }

    @ConditionalOnMissingBean(value = BeanBInterface.class)
    @RefreshScope
    @Bean
    @Lazy
    public BeanB2 beanB2(){
        logger.info("注册 BeanD2 被调用");
        return new BeanB2();
    }


}
