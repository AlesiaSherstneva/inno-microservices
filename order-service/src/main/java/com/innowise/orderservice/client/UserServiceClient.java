package com.innowise.orderservice.client;

import com.innowise.orderservice.config.ServiceFeignConfig;
import com.innowise.orderservice.model.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        configuration = ServiceFeignConfig.class
)
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    CustomerDto getUserById(@PathVariable("id") Long id);
}