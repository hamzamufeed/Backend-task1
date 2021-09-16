package com.task1.DB;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

public enum OrderCache {
    ORDER_CACHE;

    @Autowired
    AerospikeOrderRepository aerospikeOrderRepository;

    private HashMap<Integer, OrderModel> orders = new HashMap<>();

    OrderCache() {

    }

    public synchronized OrderCache getInstance() {
        return ORDER_CACHE;
    }

    public synchronized HashMap<Integer, OrderModel> getOrders() {
        return this.orders;
    }

    public synchronized void setOrders(List<OrderModel> all) {
        StreamSupport
                .stream(all.spliterator(), false)
                .forEach(r -> this.orders.put(r.getId(), r));
    }

    public synchronized OrderModel read(int key) {
        return this.orders.get(key);
    }

    public synchronized void write(OrderModel orderModel) {
        this.orders.put(orderModel.getId(), orderModel);
    }

    public synchronized void update(int key, OrderModel orderModel) {
        this.orders.replace(key, orderModel);
    }

    public synchronized void delete(int key) {
        this.orders.remove(key);
    }
}
