package com.example.junzzzz;

import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.form.spring.SpringFormEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.FeignEncoderProperties;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class JunzzzzApplication {

    public static void main(String[] args) {
        SpringApplication.run(JunzzzzApplication.class, args);
    }

    @Autowired
    private TestClient testClient;

    @Bean
    public ApplicationRunner test() {
        return args -> {
            User user = new User();
            user.setName("Can");
            User test = testClient.test(user);
            log.info("{}", test);
        };
    }

    @FeignClient(name = "test", url = "http://localhost:8080", configuration = CoreFeignConfiguration.class)
    public interface TestClient {
        @PostMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        User test(User test);
    }

    class CoreFeignConfiguration {
        @Bean
        Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters, FeignEncoderProperties feignEncoderProperties) {
            return new FormEncoder(new SpringEncoder(new SpringFormEncoder(), messageConverters, feignEncoderProperties));
        }
    }

    @RestController
    @Slf4j
    public static class TestController {
        @PostMapping(value = "/user", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
        public User test(User test) {
            log.info(String.valueOf(test));
            return test;
        }
    }

    @Data
    public static class User {
        private String name;
    }
}

