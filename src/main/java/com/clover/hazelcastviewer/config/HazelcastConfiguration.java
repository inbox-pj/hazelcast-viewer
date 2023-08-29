package com.clover.hazelcastviewer.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration of HazelcastInstance
 */
@Configuration
@EnableAutoConfiguration(exclude={HazelcastAutoConfiguration.class})
public class HazelcastConfiguration {

    @Value("${hazelcast.client.instance.name}")
    private String hazelcastClientInstance;

    @Value("${hazelcast.server.addresses}")
    private String hazelcastAddresses;


    @Value("${hazelcast.server.group}")
    private String group;

    @Value("${hazelcast.server.pass}")
    private String password;

    /**
     * Programatically create a Hazelcast instance with client config
     * @return a Hazelcast instance
     */
    @Bean
    public Map<String,HazelcastInstance> hazelcastInstance() {
        List<String> hazelcastAddressList = hazelcastAddresses != null
                ? Arrays.asList(hazelcastAddresses.split(","))
                : Collections.<String>emptyList();

        List<String> hazelcastGroupList = group != null
                ? Arrays.asList(group.split(","))
                : Collections.<String>emptyList();

        List<String> hazelcastPasswordList = password != null
                ? Arrays.asList(password.split(","))
                : Collections.<String>emptyList();

        List<String> hazelcastInstanceNames = hazelcastClientInstance != null
                ? Arrays.asList(hazelcastClientInstance.split(","))
                : Collections.<String>emptyList();

        HashMap<String, HazelcastInstance> hazelcastInstanceMap = new HashMap<>();
        for (int i = 0; i < hazelcastAddressList.size(); ++i) {
            String hazelcastAddress = hazelcastAddressList.get(i);
            String hazelcastGroup = hazelcastGroupList.size() > i ? hazelcastGroupList.get(i) : "";
            String hazelcastPassword = hazelcastPasswordList.size() > i ? hazelcastPasswordList.get(i) : "";
            String hazelcastInstanceName = hazelcastInstanceNames.get(i);
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setInstanceName(hazelcastInstanceName);
            if (hazelcastPassword.equals("")) {
                clientConfig.setGroupConfig(new GroupConfig(hazelcastGroup));
            }
            else {
                clientConfig.setGroupConfig(new GroupConfig(hazelcastGroup, hazelcastPassword));
            }
            ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();

            networkConfig.addAddress(hazelcastAddress);

            networkConfig.setSmartRouting(true);
            networkConfig.setRedoOperation(true);

            // Instantiate a Hazelcast client with the configuration
            hazelcastInstanceMap.put(hazelcastInstanceName, HazelcastClient.newHazelcastClient(clientConfig));
        }

        return hazelcastInstanceMap;

    }

}
