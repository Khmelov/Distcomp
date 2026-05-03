package com.sergey.orsik;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"InTopic", "OutTopic"})
class OrsikApplicationTests {

    @Test
    void contextLoads() {
    }
}
