package com.task1.controllers.DTOs;

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
    private Integer id;
    private String name;
    private Integer quantity;
    private double price;
}
