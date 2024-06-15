package by.kettlebell.filestorage.controller;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.web.client.TestRestTemplate;

//import static org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import static org.junit.jupiter.api.Assertions.*;

class SessionControllerTest {

    private RedisProperties.Jedis jedis;
    private TestRestTemplate testRestTemplate;
    private TestRestTemplate testRestTemplateWithAuth;
    private String testUrl = "http://localhost:8080/";

    @BeforeAll
    public void clearRedisData() {
//        testRestTemplate = new TestRestTemplate();
//        testRestTemplateWithAuth = new TestRestTemplate("admin","password",null);
//
//        jedis = new RedisProperties.Jedis();
//
//        jedis.flushAll();
    }
}