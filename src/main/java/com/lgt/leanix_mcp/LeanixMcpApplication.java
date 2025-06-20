package com.lgt.leanix_mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.web.bind.annotation.RestController;

import com.lgt.leanix_mcp.service.LeanIXService;
import com.lgt.leanix_mcp.config.LeanIXClientConfig;

@SpringBootApplication
@RestController
@EnableConfigurationProperties(LeanIXClientConfig.class)
public class LeanixMcpApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeanixMcpApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider registerTool(LeanIXService leanIXService) {
		return MethodToolCallbackProvider.builder().toolObjects(leanIXService)
				.build();
	}

}
