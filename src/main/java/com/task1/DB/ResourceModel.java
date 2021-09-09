package com.task1.DB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceModel implements Model {
    @Id
    private Integer id;
    private String name;
    private Integer quantity;
    private double price;
}
