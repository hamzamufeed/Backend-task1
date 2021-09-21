package com.task1.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task1.DB.*;
import com.task1.controllers.DTOs.OrderDTO;
import com.task1.controllers.DTOs.OrderHistoryDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    @PostConstruct
    public void init(){
        OrderCache.getInstance().setOrders(
                StreamSupport
                        .stream(aerospikeOrderRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList())
        );
    }

    public List<OrderDTO> getAllOrders(){
        ArrayList<OrderModel> allOrders = new ArrayList<>(OrderCache.getInstance().getOrders().values());
        return allOrders.stream()
                .map( i -> (OrderDTO) transformerService.EntityToDto(i, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public OrderDTO getOrder(int id){
//        Optional<OrderModel> orderModel = aerospikeOrderRepository.findById(id);
//        return (orderModel.isPresent()) ? (OrderDTO) transformerService.EntityToDto(orderModel.get(), OrderDTO.class) : null;
        OrderModel order = OrderCache.getInstance().read(id);
        return (order != null) ? (OrderDTO) transformerService.EntityToDto(order, OrderDTO.class) : null;
    }

    public ResponseEntity<?> supply(OrderDTO orderDTO){
//        Optional<ResourceModel> resourceModel = aerospikeResourceRepository.findById(orderDTO.getResourceId());
        ResourceModel resourceModel = ResourceCache.getInstance().read(orderDTO.getResourceId());
        if(resourceModel == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource Doesn't Exist");
        else {
            Date date = new Date();
            orderDTO.setDate(date);
            orderDTO.setType("supply");
            orderDTO.setTotalPrice(orderDTO.getQuantity() * resourceModel.getPrice());
            resourceModel.setQuantity(resourceModel.getQuantity() + orderDTO.getQuantity());
            OrderModel orderModel = (OrderModel) transformerService.DtoToEntity(orderDTO, OrderModel.class);
            aerospikeOrderRepository.save(orderModel);
            OrderCache.getInstance().write(orderModel);
            aerospikeResourceRepository.save(resourceModel);
            ResourceCache.getInstance().update(resourceModel.getId(), resourceModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderModel);
        }
    }

    public ResponseEntity<?> purchase(OrderDTO orderDTO) {
//        Optional<ResourceModel> resourceModel = aerospikeResourceRepository.findById(orderDTO.getResourceId());
        ResourceModel resourceModel = ResourceCache.getInstance().read(orderDTO.getResourceId());
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
            OrderModel orderModel = (OrderModel) transformerService.DtoToEntity(orderDTO, OrderModel.class);
            aerospikeOrderRepository.save(orderModel);
            OrderCache.getInstance().write(orderModel);
            aerospikeResourceRepository.save(resourceModel);
            ResourceCache.getInstance().update(resourceModel.getId(), resourceModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderModel);
        }
    }

    public OrderDTO updateOrder(OrderDTO orderDTO) {
        OrderModel updatedOrderModel = aerospikeOrderRepository.save((OrderModel) transformerService.DtoToEntity( orderDTO, OrderModel.class));
        OrderCache.getInstance().update(updatedOrderModel.getId(), updatedOrderModel);
        return (OrderDTO) transformerService.EntityToDto(updatedOrderModel, OrderDTO.class);
    }

    public void removeOrderById(Integer id) {
        aerospikeOrderRepository.deleteById(id);
        OrderCache.getInstance().delete(id);
    }

    public final Logger logger = LogManager.getLogger(TransformerService.class);

    public List<OrderDTO> getOrderHistory(Map<String,String> params) {
        ObjectMapper mapper = new ObjectMapper();
        OrderHistoryDTO orderHistoryDTO = mapper.convertValue(params, OrderHistoryDTO.class);
        logger.info("Count: "+orderHistoryDTO.getCount()+" - Order Type: "+orderHistoryDTO.getOrderType()+
                " - Sorted: "+orderHistoryDTO.isSorted());
        Iterable<OrderModel> allOrders = aerospikeOrderRepository.findAll();
        Stream<OrderDTO> ordersStream = StreamSupport
                .stream(allOrders.spliterator(), false)
                .map(i -> (OrderDTO) transformerService.EntityToDto(i, OrderDTO.class));

        Stream<OrderDTO> filteredStream = filterOrders(ordersStream, orderHistoryDTO.getOrderType());
        Stream<OrderDTO> limitedOrders = countOrders(filteredStream, orderHistoryDTO.getCount());
        Stream<OrderDTO> sortedOrders = sortOrders(limitedOrders, orderHistoryDTO.isSorted());
        return sortedOrders.collect(Collectors.toList());
    }

    public static Stream<OrderDTO> filterOrders(Stream<OrderDTO> history, String orderType){
        return (orderType == null) ? history : history.filter(i -> i.getType().equals(orderType));
    }

    public static Stream<OrderDTO> countOrders(Stream<OrderDTO> history, Integer count){
        return (count == null) ? history : history.limit(count);
    }

    public static Stream<OrderDTO> sortOrders(Stream<OrderDTO> history, boolean sorted) {
        return (sorted) ? history.sorted(Comparator.comparing(OrderDTO::getTotalPrice)) :
                history.sorted(Comparator.comparing(OrderDTO::getDate));
    }
}
