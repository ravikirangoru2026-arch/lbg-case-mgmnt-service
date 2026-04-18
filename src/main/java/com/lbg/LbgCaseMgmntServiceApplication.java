package com.lbg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LbgCaseMgmntServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LbgCaseMgmntServiceApplication.class, args);
	}

}
