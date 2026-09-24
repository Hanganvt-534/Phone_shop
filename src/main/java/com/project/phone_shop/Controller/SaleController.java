package com.project.phone_shop.Controller;

// Chức năng bán hàng được xử lý tại OrderController (/orders)
// CartController (/cart) xử lý giỏ hàng
// Controller này được giữ lại để mở rộng sau

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaleController {
    // Xem OrderController cho các API bán hàng:
    // POST /orders/checkout   - Thanh toán giỏ hàng
    // POST /orders/direct     - Mua thẳng
    // GET  /orders/my         - Xem đơn hàng của tôi
    // GET  /orders/{id}       - Chi tiết đơn hàng
    // GET  /orders            - Tất cả đơn hàng (ADMIN)
    // PUT  /orders/{id}/status - Cập nhật trạng thái (ADMIN)
}
