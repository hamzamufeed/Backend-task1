package com.task1.controllers.DTOs;

import lombok.NonNull;


public class OrderHistoryDTO {
    private Integer count;
    private String orderType;

    @NonNull
    private boolean sorted = false;

    public OrderHistoryDTO() {
    }

    public OrderHistoryDTO(Integer count, String orderType, @NonNull boolean sorted) {
        this.count = (count == null) ? Integer.MAX_VALUE : count;
        this.orderType = orderType;
        this.sorted = sorted;
    }

    public Integer getCount() {
        return count;
    }

    public String getOrderType() {
        return orderType;
    }

    public boolean isSorted() {
        return sorted;
    }
}
