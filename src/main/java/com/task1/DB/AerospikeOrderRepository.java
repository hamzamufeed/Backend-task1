package com.task1.DB;

import org.springframework.data.repository.CrudRepository;

public interface AerospikeOrderRepository extends CrudRepository<OrderModel, Integer> {
}
