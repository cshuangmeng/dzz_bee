package com.yixiang.api.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.yixiang.api.util.PropertiesUtil;

@Component()
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
	
	Logger log=LoggerFactory.getLogger(getClass());

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			if(null==Redis.use()){
				//设置accessToken存储方式
				RedisPlugin rp=new RedisPlugin("mainRedis", PropertiesUtil.getProperty("redis.host")
						, Integer.valueOf(PropertiesUtil.getProperty("redis.port")), PropertiesUtil.getProperty("redis.pwd"));
				rp.start();
				log.info("Application inited!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
