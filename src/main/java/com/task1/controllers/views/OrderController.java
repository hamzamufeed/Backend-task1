package com.task1.controllers.views;

import com.task1.DB.OrderModel;
import com.task1.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/order/{id}")
    public Optional<OrderModel> getOrder(@PathVariable int id){
        return orderService.getOrder(id);
    }

    @PostMapping("/order/supply")
    public void supplyOrder(@RequestBody OrderModel orderModel){
        orderService.supply(orderModel);
    }

    @PostMapping("/order")
    public void purchaseOrder(@RequestBody OrderModel orderModel){
        orderService.purchase(orderModel);
    }
}
