package com.orderservice.controller;
import com.orderservice.model.Order;
import com.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    public OrderController(OrderService orderService, RestTemplate restTemplate) {
        this.orderService = orderService;
        this.restTemplate = restTemplate;
    }
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Order addOrder(@RequestBody Order order) {
        return orderService.addOrder(order);
    }
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id,@RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/products")
    public Object getProductsFromProductService() {
        return restTemplate.getForObject(
                "http://product-service/products",
                Object.class
        );
    }
}