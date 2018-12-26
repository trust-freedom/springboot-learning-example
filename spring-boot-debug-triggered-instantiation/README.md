# spring-boot-debug-triggered-instantiation

**由使用IDE工具Debug触发的Spring Bean的实例化**

<br>

## 如何发现的问题

发现如上描述的情况是因为在研究Eureka Client的初始化以及注册时，如果将Eureka Client注解的自动注册置为false，如`@EnableDiscoveryClient(autoRegister = false)` 或 配置文件使用 `spring.cloud.service-registry.auto-registration.enabled=false`取消自动注册后，正常情况下服务启动后，Eureka Client是不会向Eureka Server注册的，但如果在`org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration`的

```java
@Bean
public DiscoveryClient discoveryClient(EurekaInstanceConfig config, EurekaClient client) {
	return new EurekaDiscoveryClient(config, client);
}
```

向容器注册DiscoveryClient的方法打断点，再启动服务，端点触发后直接跳过，看日志会发现Eureka Client向Eureka Server注册了，且注册后的状态是 **STARTING**

<br>

简单分析原因，可能是因为`EurekaClientAutoConfiguration`自动配置类在注册EurekaClient时的逻辑导致的

```java
@Configuration
@ConditionalOnMissingRefreshScope
protected static class EurekaClientConfiguration {

	@Autowired
	private ApplicationContext context;

	@Autowired(required = false)
	private DiscoveryClientOptionalArgs optionalArgs;

	@Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean(value = EurekaClient.class, search = SearchStrategy.CURRENT)
	public EurekaClient eurekaClient(ApplicationInfoManager manager, EurekaClientConfig config) {
		return new CloudEurekaClient(manager, config, this.optionalArgs,this.context);
	}

	@Bean
	@ConditionalOnMissingBean(value = ApplicationInfoManager.class, search = SearchStrategy.CURRENT)
	public ApplicationInfoManager eurekaApplicationInfoManager(
			EurekaInstanceConfig config) {
		InstanceInfo instanceInfo = new InstanceInfoFactory().create(config);
		return new ApplicationInfoManager(config, instanceInfo);
	}
}


@Configuration
@ConditionalOnRefreshScope
protected static class RefreshableEurekaClientConfiguration {
	@Autowired
	private ApplicationContext context;

	@Autowired(required = false)
	private DiscoveryClientOptionalArgs optionalArgs;

	@Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean(value = EurekaClient.class, search = SearchStrategy.CURRENT)
	@org.springframework.cloud.context.config.annotation.RefreshScope
	@Lazy
	public EurekaClient eurekaClient(ApplicationInfoManager manager, EurekaClientConfig config, EurekaInstanceConfig instance) {
		manager.getInfo(); // force initialization
		return new CloudEurekaClient(manager, config, this.optionalArgs,this.context);
	}

	@Bean
	@ConditionalOnMissingBean(value = ApplicationInfoManager.class, search = SearchStrategy.CURRENT)
	@org.springframework.cloud.context.config.annotation.RefreshScope
	@Lazy
	public ApplicationInfoManager eurekaApplicationInfoManager(EurekaInstanceConfig config) {
		InstanceInfo instanceInfo = new InstanceInfoFactory().create(config);
		return new ApplicationInfoManager(config, instanceInfo);
	}
}
```

可以看到`EurekaClientAutoConfiguration`中有两个静态内部类，分别代表满足RefreshScope条件下的`RefreshableEurekaClientConfiguration` 和 不满足RefreshScope条件下的`EurekaClientConfiguration`

由于引入eureka client，肯定引入的spring-cloud-context，故满足RefreshScope条件，所以使用`RefreshableEurekaClientConfiguration`，但其注入的EurekaClient是<font color="red"> **@Lazy + @RefreshScope**</font> 的

- **@Lazy**： 导致在没有被使用前，Bean不会被实例化
- **@RefreshScope**： 导致引用此RefreshScope Bean的对象，会使用`ScopedProxyFactoryBean`这个代理FactoryBean实例化此RefreshScope Bean（具体逻辑待研究）

> 这样可以解释为什么在Spring Cloud Dalston.RELEASE版本使用/refresh断点刷新时，会导致Eureka Client下线
>
> 而在Dalston.SR1版本修复时，只是加上了 eurekaClient.getApplications()，为了能在重新注册的时候，强制初始化eurekaClient
>
> 而在github官方issue中也说了，Dalston.RELEASE在断点调试时不会出现问题

<br>

## 模拟测试

本项目有两种情况的测试，具体在

com.freedom.springboot.condition1

com.freedom.springboot.condition2

这两个例子，尤其是第二个，就是按照 **@Lazy + @RefreshScope** 的思路模拟的

测试方式： 启动微服务，使用每个condition下的Controller暴露的端点访问，并在Controller方法中打断点，查看applicationContext中的情况，并观察log

如condition2，启动时应该还没有答应初始化BeanD2的日志，当断点后，通过applicationContext查看beanFactory中的singletonObjects，找到其中的beanC，并点开其左侧三角查看属性d，此时会触发对beanD2的实例化，打印构造方法中的log

> 说明断点调试改变了程序的状态，使得beanD2被初始化了











