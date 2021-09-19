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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    AerospikeOrderRepository aerospikeOrderRepository;

    @Autowired
    AerospikeResourceRepository aerospikeResourceRepository;

    @Autowired
    TransformerService transformerService;

    OrderCache orderCache;
    ResourceCache resourceCache;

    @PostConstruct
    public void init(){
        orderCache.ORDER_CACHE.setOrders(
                StreamSupport
                        .stream(aerospikeOrderRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
    }

    public List<OrderDTO> getAllOrders(){
        ArrayList<OrderModel> all = new ArrayList<>(orderCache.getOrders().values());
        return StreamSupport
                .stream(all.spliterator(), false)
                .map( i -> (OrderDTO) transformerService.EntityToDto((Model) i, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public OrderDTO getOrder(int id){
//        Optional<OrderModel> orderModel = aerospikeOrderRepository.findById(id);
//        return (orderModel.isPresent()) ? (OrderDTO) transformerService.EntityToDto(orderModel.get(), OrderDTO.class) : null;
        OrderModel order = orderCache.read(id);
        return (order != null) ? (OrderDTO) transformerService.EntityToDto((Model) order, OrderDTO.class) : null;
    }

    public ResponseEntity<?> supply(OrderDTO orderDTO){
//        Optional<ResourceModel> resourceModel = aerospikeResourceRepository.findById(orderDTO.getResourceId());
        ResourceModel resourceModel = resourceCache.read(orderDTO.getResourceId());
        if(resourceModel == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Doesn't Exist");
        else {
            Date date = new Date();
            orderDTO.setDate(date);
            orderDTO.setType("supply");
            orderDTO.setTotalPrice(orderDTO.getQuantity() * resourceModel.getPrice());
            resourceModel.setQuantity(resourceModel.getQuantity() + orderDTO.getQuantity());
            OrderModel orderModel = aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
            orderCache.write(orderModel);
            aerospikeResourceRepository.save(resourceModel);
            resourceCache.update(resourceModel.getId(), resourceModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderModel);
        }
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    public ResponseEntity<?> purchase(OrderDTO orderDTO) {
//        Optional<ResourceModel> resourceModel = aerospikeResourceRepository.findById(orderDTO.getResourceId());
        ResourceModel resourceModel = resourceCache.read(orderDTO.getResourceId());
        if(resourceModel == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Doesn't Exist");
        else if(orderDTO.getQuantity() > resourceModel.getQuantity())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insufficient Quantity");
        else {
            Date date = new Date();
            orderDTO.setDate(date);
            orderDTO.setType("purchase");
            orderDTO.setTotalPrice(orderDTO.getQuantity() * resourceModel.getPrice());
            resourceModel.setQuantity(resourceModel.getQuantity() - orderDTO.getQuantity());
            OrderModel orderModel = aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
            orderCache.write(orderModel);
            aerospikeResourceRepository.save(resourceModel);
            resourceCache.update(resourceModel.getId(), resourceModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderModel);
        }
    }

    public OrderDTO updateOrder(OrderDTO orderDTO) {
        OrderModel updatedOrderModel = aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity((DTO) orderDTO, OrderModel.class));
        orderCache.update(updatedOrderModel.getId(), updatedOrderModel);
        return (OrderDTO) transformerService.EntityToDto((Model) updatedOrderModel, OrderDTO.class);
    }

    public void removeOrderById(Integer id) {
        aerospikeOrderRepository.deleteById(id);
    }

    public final Logger logger = LogManager.getLogger(TransformerService.class);

    public List<OrderDTO> getOrderHistory(Integer count, String orderType, boolean sorted) {
        logger.info("Count: "+count+" - Order Type: "+orderType+" - Sorted: "+sorted);
        Iterable<OrderModel> all = aerospikeOrderRepository.findAll();
        List<OrderDTO> history;
        if(count == null && orderType == null) {
            history = StreamSupport
                    .stream(all.spliterator(), false)
                    .map(i -> (OrderDTO) transformerService.EntityToDto(i, OrderDTO.class))
                    .collect(Collectors.toList());
        }
        else if(count != null && orderType == null) {
            history = StreamSupport
                    .stream(all.spliterator(), false)
                    .map(i -> (OrderDTO) transformerService.EntityToDto(i, OrderDTO.class))
                    .limit(count)
                    .collect(Collectors.toList());
        }
        else if(count == null && orderType != null) {
            history = StreamSupport
                    .stream(all.spliterator(), false)
                    .map(i -> (OrderDTO) transformerService.EntityToDto(i, OrderDTO.class))
                    .filter(i -> i.getType().equals(orderType))
                    .collect(Collectors.toList());
        }
        else {
            history = StreamSupport
                    .stream(all.spliterator(), false)
                    .map(i -> (OrderDTO) transformerService.EntityToDto(i, OrderDTO.class))
                    .filter(i -> i.getType().equals(orderType))
                    .limit(count)
                    .collect(Collectors.toList());
        }
        if(sorted)
            history = StreamSupport
                    .stream(history.spliterator(), false)
                    .sorted()
                    .collect(Collectors.toList());
        else
            history = StreamSupport
                    .stream(history.spliterator(), false)
                    .sorted(Comparator.comparing(OrderDTO::getDate))
                    .collect(Collectors.toList());
        return history;
    }
}
