package com.task1.services;

import com.task1.DB.AerospikeOrderRepository;
import com.task1.DB.OrderModel;
import com.task1.DB.ResourceModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    AerospikeOrderRepository aerospikeOrderRepository;

    @Autowired
    TransformerService transformerService;

    public Optional<OrderModel> getOrder(int id){
        return aerospikeOrderRepository.findById(id);
    }

    public void supply(OrderModel orderModel){ //,Class DtoClass
        orderModel.setType("supply");
        aerospikeOrderRepository.save(orderModel);
    }

    public void purchase(OrderModel orderModel){
        orderModel.setType("purchase");
        aerospikeOrderRepository.save(orderModel);
    }

}
