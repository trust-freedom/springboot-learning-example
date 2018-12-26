package com.freedom.springboot.condition1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugTriggeredInstantiationController {
    private static final Logger logger = LoggerFactory.getLogger(DebugTriggeredInstantiationController.class);

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 请在此方法打断点，并通过applicationContext查看beanFactory中的singletonObjects
     * 找到其中的beanA，并点开其左侧三角查看属性b，此时会触发对beanB1的实例化，打印构造方法中的log
     * @return
     */
    @RequestMapping("/check")
    public String[] check(){
        return applicationContext.getBeanDefinitionNames();
    }

}
