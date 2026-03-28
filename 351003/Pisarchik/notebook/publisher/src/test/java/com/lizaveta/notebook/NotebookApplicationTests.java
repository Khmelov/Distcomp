package com.lizaveta.notebook;

import com.lizaveta.notebook.config.PostgresTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotebookApplicationTests extends PostgresTestContainerConfig {

    @Test
    void contextLoads() {
    }
}
