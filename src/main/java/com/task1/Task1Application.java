package com.task1;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Task1Application {

	public static void main(String[] args) {
		SpringApplication.run(Task1Application.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

//	@Bean
//	@PostConstruct
//	public ResourceCache resourceCacheBean() {
//		return ResourceCache.RESOURCE_CACHE.getInstance();
//	}
//
//	@Bean
//	@PostConstruct
//	public OrderCache orderCacheBean() {
//		return OrderCache.ORDER_CACHE.getInstance();
//	}
}

/*
docker run --rm -tid --name aerospike-server -p 3000:3000 -p 3001:3001 -p 3002:3002 -p 3003:3003 aerospike/aerospike-server
 */