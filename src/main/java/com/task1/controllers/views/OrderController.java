package com.task1.controllers.views;

import com.task1.controllers.DTOs.OrderDTO;
import com.task1.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/order/")
    public List<OrderDTO> getAllOrder(){
        return orderService.getAllOrders();
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrder(@PathVariable int id){
        OrderDTO orderDTO = orderService.getOrder(id);
        return (orderDTO == null) ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order Not Found") :
                ResponseEntity.status(HttpStatus.OK).body(orderDTO);
    }

    @PostMapping("/order/supply")
    public ResponseEntity<?> supplyOrder(@RequestBody OrderDTO orderDTO){
        if(orderService.getOrder(orderDTO.getId()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order Already Exists");
        else if(orderDTO.getQuantity() < 0 || orderDTO.getQuantity() > 100)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity should be 0 - 100");
        else
            return orderService.supply(orderDTO);
    }

    @PostMapping("/order/purchase")
    public ResponseEntity<?> purchaseOrder(@RequestBody OrderDTO orderDTO){
        if(orderService.getOrder(orderDTO.getId()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order Already Exists");
        else if(orderDTO.getQuantity() < 0 || orderDTO.getQuantity() > 100)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity should be 0 - 100");
        else
            return orderService.purchase(orderDTO);
    }

    @PutMapping("/order/{id}")
    public OrderDTO updateOrder(@RequestBody OrderDTO orderDTO, @PathVariable("id") Integer id) {
        return orderService.updateOrder(orderDTO);
    }

    @DeleteMapping("/order/{id}")
    public void deleteOrder(@PathVariable("id") Integer id) {
        orderService.removeOrderById(id);
    }

//    @GetMapping("/order/history")
//    @ResponseBody
//    public List<OrderDTO> getOrderHistory(@RequestParam Map<String,String> allRequestParams){
//        return orderService.getOrderHistory(allRequestParams.toString());
//    }

    @GetMapping("/order/history")
    public List<OrderDTO> getOrderHistory(
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) String orderType,
            @RequestParam() boolean sorted) {
        System.out.println(count+" "+orderType+" "+sorted);
        return orderService.getOrderHistory(count, orderType, sorted);
    }
}