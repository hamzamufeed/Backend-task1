package com.task1.DB;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;


public enum ResourceCache {
    RESOURCE_CACHE;

    @Autowired
    AerospikeResourceRepository aerospikeResourceRepository;

    private HashMap<Integer, ResourceModel> resources = new HashMap<>();

    ResourceCache() {
    }

    public synchronized ResourceCache getInstance() {
        return RESOURCE_CACHE;
    }

    public synchronized HashMap<Integer, ResourceModel> getResources() {
        return this.resources;
    }

    public synchronized void setResources(List<ResourceModel> all) {
        StreamSupport
                .stream(all.spliterator(), false)
                .forEach(r -> this.resources.put(r.getId(), r));
    }

    public synchronized ResourceModel read(int key) {
        return this.resources.get(key);
    }

    public synchronized void write(ResourceModel resourceModel) {
        this.resources.put(resourceModel.getId(), resourceModel);
    }

    public synchronized void update(int key, ResourceModel resourceModel) {
        this.resources.replace(key, resourceModel);
    }

    public synchronized void patch(int key, String patch){
        //this.resources.replace(key, patch);
    }

    public synchronized void delete(int key) {
        this.resources.remove(key);
    }
}
