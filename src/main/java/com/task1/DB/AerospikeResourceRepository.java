package com.task1.DB;

import org.springframework.data.repository.CrudRepository;

public interface AerospikeResourceRepository extends CrudRepository<ResourceModel, Integer> {
}
