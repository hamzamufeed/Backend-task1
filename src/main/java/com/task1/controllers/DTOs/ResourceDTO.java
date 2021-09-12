package com.task1.controllers.DTOs;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.task1.DB.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO implements DTO {
    @Id
    @NotNull
    private Integer id;

    @NotNull
    private String name;

    @Min(value = 0, message = "quantity should not be less than 0")
    @Max(value = 1000, message = "quantity should not be greater than 1000")
    @NotNull
    private int quantity;

    @NotNull
    private double price;
}
