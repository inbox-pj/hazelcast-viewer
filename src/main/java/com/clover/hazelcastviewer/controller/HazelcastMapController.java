package com.clover.hazelcastviewer.controller;

import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.MapService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * REST controller for operations on Hazelcast maps
 */
@RestController
@RequestMapping("/servers")
public class HazelcastMapController extends AbstractHazelcastController {

    /**
     * Get a list of the names of all servers
     * @return list of server names
     */
    @RequestMapping(method = GET)
    public List<?> getServers() {
        logger.debug("Getting list of servers");

        return new ArrayList<>(getDistributedObjectNames(MapService.SERVICE_NAME).keySet());
    }

    /**
     * Get the contents of a map on a server
     * @param serverName the name of the server
     * @return the maps on the server
     */
    @RequestMapping(method = GET, value = "/{serverName}/maps")
    public List<?> getMap(@PathVariable("serverName") String serverName) {
        logger.debug("Getting list of maps from server: {}", serverName);

        return getDistributedObjectNames(MapService.SERVICE_NAME).get(serverName);
    }

    /**
     * Get the contents of a map
     * @param serverName the name of the server
     * @param mapName the name of the map
     * @return the contents of the map
     */
    @RequestMapping(method = GET, value = "/{serverName}/maps/{mapName}")
    public Map<?,?> getMapObjects(@PathVariable("serverName") String serverName,
                               @PathVariable("mapName") String mapName) {
        logger.debug("Viewing map: {} on server: {}", mapName, serverName);

        return toMap(hazelcastInstances.get(serverName).getMap(mapName));
    }

    /**
     * Get the contents of a specific entry in the map
     * @param serverName the name of the server
     * @param mapName the name of the map
     * @param mapKey the key of the map entry
     * @return the contents of a specific entry in the map
     */
    @RequestMapping(method = GET, value = "/{serverName}/maps/{mapName}/{mapKey}")
    public Object getMapObject(@PathVariable("serverName") String serverName,
                               @PathVariable("mapName") String mapName,
                               @PathVariable("mapKey") String mapKey) {
        logger.debug("Viewing map object: {}[{}] on server: {}", mapName, mapKey, serverName);

        return hazelcastInstances.get(serverName).getMap(mapName).get(mapKey);
    }

    private <K, V> Map<K, V> toMap(IMap<K, V> hazelcastMap) {
        Map<K, V> map = new HashMap<>();
        for (K key : hazelcastMap.keySet()) {
            map.put(key, hazelcastMap.get(key));
        }
        return map;
    }

}
