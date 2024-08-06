package com.branch.branchapiclientsdk;


import com.branch.branchapiclientsdk.client.BranchApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "branch.client")
@Data
@ComponentScan
public class BranchAPiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public BranchApiClient branchApiClient() {
        return new BranchApiClient(accessKey, secretKey);
    }

}
