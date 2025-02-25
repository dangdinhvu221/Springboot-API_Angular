package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order detail's ID must be > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's ID must be > 0")
    private Long productId;

    @Min(value = 0, message="Price money must be >= 0")
    private Float price;

    @JsonProperty("number_of_orders")
    @Min(value = 0, message="Number of order's money must be >= 0")
    private Integer numberOfOrders;

    @JsonProperty("total_money")
    @Min(value = 0, message="Total price money must be >= 0")
    private Float totalMoney;
    private String color;
}
