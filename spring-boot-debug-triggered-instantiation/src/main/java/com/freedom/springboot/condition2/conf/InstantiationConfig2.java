package com.freedom.springboot.condition2.conf;

import com.freedom.springboot.condition2.bean.BeanC;
import com.freedom.springboot.condition2.bean.BeanD1;
import com.freedom.springboot.condition2.bean.BeanD2;
import com.freedom.springboot.condition2.bean.BeanDInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@Configuration
public class InstantiationConfig2 {
    private static final Logger logger = LoggerFactory.getLogger(InstantiationConfig2.class);


    @Bean
    public BeanC beanC(@Qualifier("beanD2") BeanDInterface d){
        //logger.info("注册 BeanC被调用，d=" + d);
        return new BeanC(d);
    }



    @RefreshScope
    @Primary
    @Bean
    @Lazy
    public BeanD1 beanD1(){
        logger.info("注册 BeanD1 被调用");
        return new BeanD1();
    }

    @RefreshScope
    @Bean
    @Lazy
    public BeanD2 beanD2(){
        logger.info("注册 BeanD2 被调用");
        return new BeanD2();
    }

}
