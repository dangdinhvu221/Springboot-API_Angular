package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.imp.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        // tìm xem user_id có tồn tại hay không?
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDTO.getUserId()));
        // convert oderDTO => Order
        // Dùng thư viện Model Mapper
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ.
        // Tạo một luồng ánh xạ riêng để kiểm soát việc ánh xạ.
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId)); // Bỏ qua ID nếu cần

        Order order = new Order();
        modelMapper.map(orderDTO, order);

        order.setUser(user);
        order.setOrderDate(new Date()); // Lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);

        // Kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today!");
        }

        order.setActive(true);
        order.setShippingDate(shippingDate);

        // Lưu order và đảm bảo không bị null
        order = orderRepository.save(order);

        // In log kiểm tra dữ liệu
        System.out.println("Saved Order: " + order);

        // Đảm bảo modelMapper không bị lỗi khi mapping
//        modelMapper.typeMap(Order.class, OrderResponse.class).addMappings(mapper -> {
//            mapper.map(src -> src.getUser().getId(), OrderResponse::setUser);
//            mapper.map(Order::getId, OrderResponse::setId);
//            mapper.map(Order::getFullName, OrderResponse::setFullName);
//            mapper.map(Order::getPhoneNumber, OrderResponse::setPhoneNumber);
//            mapper.map(Order::getOrderDate, OrderResponse::setOrderDate);
//            mapper.map(Order::getTotalMoney, OrderResponse::setTotalMoney);
//        });

        OrderResponse response = OrderResponse.mapToOrderResponse(order);
        System.out.println("Mapped OrderResponse: " + response);
        return response;

    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() ->
                new DateTimeException("Cannot find order with id: " + orderId));
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot find order with id: " + id));

        User existingUser = userRepository.findById(order.getUser().getId()).orElseThrow(() ->
                new DataNotFoundException("Cannot find user with id: " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường đơn hàng từ OrderDTO
        modelMapper.map(orderDTO, order);
        order.setUser(existingUser);
        orderRepository.save(order);
        OrderResponse response = OrderResponse.mapToOrderResponse(order);
        System.out.println("Mapped OrderResponse: " + response);
        return response;
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        // không được xáo cứng, chỉ xoá mềm
        if(order != null){
            order.setActive(false); // update trạng thái đơn hàng
            orderRepository.save(order);
        }
    }

    public List<Order> findByUserIds(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
