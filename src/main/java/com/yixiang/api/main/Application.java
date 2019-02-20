package com.yixiang.api.main;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.yixiang.api.interceptor.LoginInterceptor;
import com.yixiang.api.interceptor.ParameterInterceptor;
import com.yixiang.api.interceptor.SignInterceptor;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@SpringBootApplication
@ComponentScan("com.yixiang")
@EnableWebMvc
@EnableAutoConfiguration
@MapperScan("com.yixiang")
@EnableCaching
@EnableTransactionManagement
@EnableSwagger2
public class Application extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.addListeners(new ApplicationStartup());
		app.run(args);
	}
	
	// 声明参数封装拦截器
	@Bean
	public ParameterInterceptor parameterInterceptor() {
		return new ParameterInterceptor();
	}

	// 声明身份验证拦截器
	@Bean
	public LoginInterceptor loginInterceptor() {
		return new LoginInterceptor();
	}

	// 第三方调用验证拦截器
	@Bean
	public SignInterceptor signInterceptor() {
		return new SignInterceptor();
	}

	// 追加自定义拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(parameterInterceptor());
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		// 需要定义一个convert转换消息的对象;
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		// 添加fastJson的配置信息，比如：是否要格式化返回的json数据;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,SerializerFeature.DisableCircularReferenceDetect);
		fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		// 处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		// 在convert中添加配置信息.
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
		// 将convert添加到converters当中.
		converters.add(fastJsonHttpMessageConverter);
		converters.add(new StringHttpMessageConverter());
		converters.add(new ResourceHttpMessageConverter());
	}

}
