package com.task1.services;

import com.task1.DB.*;
import com.task1.controllers.DTOs.OrderDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    AerospikeOrderRepository aerospikeOrderRepository;

    @Autowired
    AerospikeResourceRepository aerospikeResourceRepository;

    @Autowired
    TransformerService transformerService;

    public OrderDTO getOrder(int id){
        Optional<OrderModel> orderModel = aerospikeOrderRepository.findById(id);
        return (orderModel.isPresent()) ? (OrderDTO) transformerService.EntityToDto(orderModel.get(), OrderDTO.class) : null;
    }

    public ResponseEntity<?> supply(OrderDTO orderDTO){
        Optional<ResourceModel> resourceModel = aerospikeResourceRepository.findById(orderDTO.getResourceId());
        if(!resourceModel.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Doesn't Exist");
        else {
            Date date = new Date();
            orderDTO.setDate(date);
            orderDTO.setType("supply");
            orderDTO.setTotalPrice(orderDTO.getQuantity() * resourceModel.get().getPrice());
            resourceModel.get().setQuantity(resourceModel.get().getQuantity() + orderDTO.getQuantity());
            OrderModel orderModel = aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
            aerospikeResourceRepository.save(resourceModel.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderModel);
        }
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    public ResponseEntity<?> purchase(OrderDTO orderDTO) {
        Optional<ResourceModel> resourceModel = aerospikeResourceRepository.findById(orderDTO.getResourceId());
        if(!resourceModel.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Doesn't Exist");
        else if(orderDTO.getQuantity() > resourceModel.get().getQuantity())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insufficient Quantity");
        else {
            Date date = new Date();
            orderDTO.setDate(date);
            orderDTO.setType("purchase");
            orderDTO.setTotalPrice(orderDTO.getQuantity() * resourceModel.get().getPrice());
            resourceModel.get().setQuantity(resourceModel.get().getQuantity() - orderDTO.getQuantity());
            OrderModel orderModel = aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
            aerospikeResourceRepository.save(resourceModel.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderModel);
        }
    }

    public OrderDTO updateOrder(OrderDTO orderDTO) {
        OrderModel updatedOrderModel = aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
        return (OrderDTO) transformerService.EntityToDto((Model) updatedOrderModel, OrderDTO.class);
    }

    public void removeOrderById(Integer id) {
        aerospikeOrderRepository.deleteById(id);
    }

    public final Logger logger = LogManager.getLogger(TransformerService.class);

    public Iterable<OrderModel> getOrderHistory(String requestParams) {
        logger.info(requestParams);
        //return null;
        return aerospikeOrderRepository.findAll();
    }
}
