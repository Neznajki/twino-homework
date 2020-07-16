package com.twino.homework;

import com.twino.homework.web.ApiController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TwinoHomeworkApplicationTests {
    @Autowired
    private ApiController apiController;

    @Test
    void contextLoads() {
        assertThat(apiController).isNotNull();
    }

}
