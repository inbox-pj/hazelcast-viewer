package com.clover.hazelcastviewer.controller;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Common Hazelcast operations
 */
public abstract class AbstractHazelcastController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Map<String,HazelcastInstance> hazelcastInstances;

    protected Map<String, List<String>> getDistributedObjectNames(final String serviceName) {
        return hazelcastInstances.keySet().stream().collect(Collectors.toMap(key->key,key->hazelcastInstances.get(key).getDistributedObjects().stream()
                .filter(d -> d.getServiceName().equals(serviceName))
                .map(d -> d.getName())
                .collect(Collectors.toList())));
    }

}
