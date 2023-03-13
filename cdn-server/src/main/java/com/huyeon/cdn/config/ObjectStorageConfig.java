package com.huyeon.cdn.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.http.client.Options;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ObjectStorageConfig {
    @Value("${oci.configPath}")
    private String CONFIG_FILE_PATH;

    @Bean
    public ObjectStorage objectStorage() throws IOException {
        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(CONFIG_FILE_PATH);
        AbstractAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);

        Options.shouldAutoCloseResponseInputStream(false);

        return ObjectStorageClient.builder()
                .region(Region.AP_CHUNCHEON_1)
                .build(provider);
    }
}
