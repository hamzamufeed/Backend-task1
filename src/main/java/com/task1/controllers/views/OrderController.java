package com.task1.controllers.views;

import com.task1.DB.OrderModel;
import com.task1.controllers.DTOs.OrderDTO;
import com.task1.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/order/{id}")
    public OrderDTO getOrder(@PathVariable int id){
        return orderService.getOrder(id);
    }

    @PostMapping("/order/supply")
    public void supplyOrder(@RequestBody OrderDTO orderDTO){
        orderService.supply(orderDTO);
    }

    @PostMapping("/order/purchase")
    public void purchaseOrder(@RequestBody OrderDTO orderDTO){
        orderService.purchase(orderDTO);
    }

    @GetMapping("/order/history")
    @ResponseBody
    public Iterable<OrderModel> getOrderHistory(@RequestParam Map<String,String> allRequestParams){
        return orderService.getOrderHistory(allRequestParams.toString());
    }
}
