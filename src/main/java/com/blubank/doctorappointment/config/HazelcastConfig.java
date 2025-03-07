package com.blubank.doctorappointment.config;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class HazelcastConfig {

    @Bean
    public Config hazelcast() {
        MapConfig eventStoreMap = new MapConfig("spring-boot-admin-event-store")
                .setInMemoryFormat(InMemoryFormat.OBJECT)
                .setBackupCount(1)
                .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU)
                        .setMaxSizePolicy(MaxSizePolicy.PER_NODE))
                .setMergePolicyConfig(new MergePolicyConfig(PutIfAbsentMergePolicy.class.getName(), 100));

        MapConfig sentNotificationsMap = new MapConfig("spring-boot-admin-application-store")
                .setInMemoryFormat(InMemoryFormat.OBJECT)
                .setBackupCount(1)
                .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU)
                        .setMaxSizePolicy(MaxSizePolicy.PER_NODE))
                .setMergePolicyConfig(new MergePolicyConfig(PutIfAbsentMergePolicy.class.getName(), 100));

        FlakeIdGeneratorConfig flakeIdGeneratorConfig = new FlakeIdGeneratorConfig("series_seq");

        Config config = new Config();
        config.addMapConfig(eventStoreMap);
        config.addMapConfig(sentNotificationsMap);
        config.addFlakeIdGeneratorConfig(flakeIdGeneratorConfig);
        config.setProperty("hazelcast.jmx", "true");

        config.getNetworkConfig()
                .getJoin()
                .getMulticastConfig()
                .setEnabled(false);
        TcpIpConfig tcpIpConfig = config.getNetworkConfig()
                .getJoin()
                .getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        tcpIpConfig.setMembers(Collections.singletonList("127.0.0.1"));
        return config;
    }

    @Bean
    public FlakeIdGenerator flakeIdGenerator(HazelcastInstance hazelcast) {
        return hazelcast.getFlakeIdGenerator("series_seq");
    }
}