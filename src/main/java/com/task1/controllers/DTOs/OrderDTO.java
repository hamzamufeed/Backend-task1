package com.task1.controllers.DTOs;

import com.task1.DB.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements DTO {

    @Id
    private Integer id;
    private Integer resourceId;
    private Integer quantity;
    private String type;
    private double totalPrice;
    private Date date;
}
