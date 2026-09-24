package com.project.phone_shop.Service;

import com.project.phone_shop.DTO.Request.CartItemRequest;
import com.project.phone_shop.DTO.Response.CartItemResponse;
import com.project.phone_shop.DTO.Response.CartResponse;
import com.project.phone_shop.Entity.*;
import com.project.phone_shop.Repository.*;
import com.project.phone_shop.exception.AppException;
import com.project.phone_shop.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    InventoryRepository inventoryRepository;

    // Lấy username từ JWT token
    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // Lấy hoặc tạo giỏ hàng cho user hiện tại
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(CartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Kiểm tra tồn kho
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

        // Kiểm tra nếu item đã có trong giỏ, cộng thêm số lượng
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        int newQuantity = request.getQuantity();
        if (cartItem != null) {
            newQuantity = cartItem.getQuantity() + request.getQuantity();
        }

        if (inventory.getQuantity() < newQuantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY);
        }

        if (cartItem != null) {
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(cartItemRepository.save(newItem));
        }

        // Reload cart
        cart = cartRepository.findById(cart.getId()).orElseThrow();
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, CartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Chỉ sửa item thuộc giỏ của user hiện tại
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Kiểm tra tồn kho
        Inventory inventory = inventoryRepository.findByProductId(cartItem.getProduct().getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

        if (inventory.getQuantity() < request.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        cart = cartRepository.findById(cart.getId()).orElseThrow();
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(Long cartItemId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        cartItemRepository.delete(cartItem);
        cart = cartRepository.findById(cart.getId()).orElseThrow();
        return toCartResponse(cart);
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart getCartEntityForUser(User user) {
        return getOrCreateCart(user);
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .unitPrice(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .username(cart.getUser().getUsername())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .totalItems(totalItems)
                .build();
    }
}
