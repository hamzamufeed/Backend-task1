package com.task1.controllers.DTOs;

import com.task1.DB.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements DTO, Comparable<OrderDTO> {

    @Id
    @NotNull
    private Integer id;

    @NotNull
    private Integer resourceId;

    @NotNull
    @Min(value = 0, message = "quantity should not be less than 0")
    @Max(value = 100, message = "quantity should not be greater than 100")
    private Integer quantity;

    private String type;
    private double totalPrice;
    private Date date;

    @Override
    public int compareTo(OrderDTO o) {
        if(this.totalPrice == o.getTotalPrice())
            return 0;
        else if(this.totalPrice > o.getTotalPrice())
            return -1;
        else
            return 1;
    }
}
