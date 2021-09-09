package com.task1.services;

import com.task1.DB.*;
import com.task1.controllers.DTOs.OrderDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    AerospikeOrderRepository aerospikeOrderRepository;

    @Autowired
    TransformerService transformerService;

    public OrderDTO getOrder(int id){
        return (OrderDTO) transformerService.EntityToDto((Model) aerospikeOrderRepository.findById(id).get(), OrderDTO.class);    }

    public void supply(OrderDTO orderDTO){
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        orderDTO.setDate(date);
        orderDTO.setType("supply");
        aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
    }

    public void purchase(OrderDTO orderDTO){
        Date date = new Date();
        orderDTO.setDate(date);
        orderDTO.setType("purchase");
        aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
    }

    public Iterable<OrderModel> getOrderHistory(String requestParams) {
        return aerospikeOrderRepository.findAll();
    }
}
