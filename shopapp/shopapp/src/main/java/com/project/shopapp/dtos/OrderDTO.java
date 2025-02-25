package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder // tạo ra đối tượng builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1, message = "User's ID must be > 0")
    private Long userId;
    @JsonProperty("full_name") // không cần đặt trùng với db vẫn được vì đây là tên đặt hàng có thể thay đổi
    private String fullName;
    private String email;// giống fullName

    @JsonProperty("phone_number")// giống fullName
    @NotBlank(message = "phone number is required")
    @Size(min = 5 , message = "Phone number must be at least 5 characters")
    private String phoneNumber;

    private String address;
    private String note;
    @JsonProperty("total_money")
    @Min(value = 0, message="Total money must be >= 0")
    private Float totalMoney;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("shipping_date")
    private LocalDate shippingDate;
    @JsonProperty("payment_method")
    private String paymentMethod;
}
