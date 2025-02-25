package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.imp.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService iOrderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid OrderDTO orderDto,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
//            dùng stream duyệt qua 1 danh sách để ánh sạ đến  nào đó và biến đổi k
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
            }
            OrderResponse orderResponse = iOrderService.createOrder(orderDto);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}")// Thêm biến đường dẫn user_id
    public ResponseEntity<?> getOrders
            (
                    @Valid @PathVariable("user_id") Long user_id
            ) {
        try {
            List<Order> orders = iOrderService.findByUserIds(user_id);
            List<OrderResponse> orderResponses = orders
                    .stream()
                    .map(OrderResponse::fromOrder)
                    .toList();
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //    Lấy chi tiết của 1 order
    @GetMapping("/{id}")// Thêm biến đường dẫn user_id
    public ResponseEntity<?> getOrder
    (
            @Valid @PathVariable("id") Long orderId
    ) {
        try {
            Order existingOrder = iOrderService.getOrder(orderId);
            return ResponseEntity.ok(OrderResponse.fromOrder(existingOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestBody() OrderDTO orderDto
    ) {
        try {
            OrderResponse orderResponse = iOrderService.updateOrder(id, orderDto);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(
            @Valid @PathVariable("id") Long id
    ) {
        //Xoá mềm => cập nhật trường active = false
        iOrderService.deleteOrder(id);
        return ResponseEntity.ok("delete successfully");

    }
}
