package com.zju.section;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class SectionApplicationTests {

	@Test
	void contextLoads() {
		// 简单的上下文加载测试
	}

}