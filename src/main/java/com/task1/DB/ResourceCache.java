package com.task1.DB;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.stream.StreamSupport;

public enum ResourceCache {
    CACHE;

    private HashMap<Integer, ResourceModel> resources = new HashMap<>();

    @Autowired
    AerospikeResourceRepository aerospikeResourceRepository;

    ResourceCache() {
        Iterable<ResourceModel> all = aerospikeResourceRepository.findAll();
        StreamSupport
                .stream(all.spliterator(), false)
                .forEach(r -> this.resources.put(r.getId(), r));
    }

    private synchronized HashMap<Integer, ResourceModel> getInstance() {
        return this.resources;
    }

    private synchronized ResourceModel read(int key) {
        return this.resources.get(key);
    }

    private synchronized void write(ResourceModel resourceModel) {
        this.resources.put(resourceModel.getId(), resourceModel);
        this.aerospikeResourceRepository.save(resourceModel);
    }

    private synchronized void update(int key, ResourceModel resourceModel) {
        this.resources.put(key, resourceModel);
        this.aerospikeResourceRepository.save(resourceModel);
    }
}
