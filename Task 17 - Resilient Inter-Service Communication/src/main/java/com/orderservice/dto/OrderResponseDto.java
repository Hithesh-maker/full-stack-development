package com.orderservice.dto;

public class OrderResponseDto {

    private Long orderId;
    private Long productId;
    private String customerName;
    private Integer orderQuantity;
    private Double totalPrice;
    private String orderStatus;
    private String serviceStatus;
    private String message;
    private ProductDto productDetails;

    public OrderResponseDto() {
    }

    public OrderResponseDto(Long orderId, Long productId, String customerName, Integer orderQuantity,
                            Double totalPrice, String orderStatus, String serviceStatus,
                            String message, ProductDto productDetails) {
        this.orderId = orderId;
        this.productId = productId;
        this.customerName = customerName;
        this.orderQuantity = orderQuantity;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.serviceStatus = serviceStatus;
        this.message = message;
        this.productDetails = productDetails;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProductDto getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ProductDto productDetails) {
        this.productDetails = productDetails;
    }
}
