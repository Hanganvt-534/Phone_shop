package com.project.phone_shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1009, "Invalid email address", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(1009, "Email is required", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1005, "User not found", HttpStatus.NOT_FOUND), PRODUCT_EXISTS(1010, "Product already exists", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1011, "Product not found", HttpStatus.NOT_FOUND),
    INVENTORY_NOT_FOUND(1012, "Inventory not found", HttpStatus.NOT_FOUND),
    INVENTORY_ALREADY_EXISTS(1013, "Inventory already exists", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_QUANTITY(1014, "Insufficient quantity in stock", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(1015, "Cart item not found", HttpStatus.NOT_FOUND),
    CART_EMPTY(1016, "Cart is empty", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1017, "Order not found", HttpStatus.NOT_FOUND);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
