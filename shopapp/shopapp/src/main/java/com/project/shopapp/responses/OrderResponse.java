package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.Product;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Builder
public class OrderResponse{

    private Long id;

    @JsonProperty("user_id")
    private Long user;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
    private String note;
    @JsonProperty("order_date")
    private Date orderDate;
    private String status;

    @JsonProperty("total_money")
    private Float totalMoney;

    public static  OrderResponse fromOrder(Order order){
        return OrderResponse
                .builder()
                .id(order.getId())
                .user(order.getUser().getId())
                .fullName(order.getFullName())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .build();
    }

    public static OrderResponse mapToOrderResponse(Order order) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Order.class, OrderResponse.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getId(), OrderResponse::setUser);
            mapper.map(Order::getId, OrderResponse::setId);
            mapper.map(Order::getFullName, OrderResponse::setFullName);
            mapper.map(Order::getPhoneNumber, OrderResponse::setPhoneNumber);
            mapper.map(Order::getOrderDate, OrderResponse::setOrderDate);
            mapper.map(Order::getTotalMoney, OrderResponse::setTotalMoney);
        });
        return modelMapper.map(order, OrderResponse.class);
    }

}
