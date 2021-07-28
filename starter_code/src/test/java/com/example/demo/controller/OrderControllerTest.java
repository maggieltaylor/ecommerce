package com.example.demo.controller;

import com.example.demo.controller.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @Mock UserRepository userRepository;
    @Mock OrderRepository orderRepository;

    private OrderController controller;

    @Before
    public void init() {
        controller = new OrderController(userRepository, orderRepository);
    }

    @Test
    public void submitReturnsASavedOrderWithTheUsersCartItem() {
        var username = "maggie";
        var expectedItems = Collections.singletonList(Item.builder()
                .name("cup")
                .description("red cup")
                .build());
        var userCart = Cart.builder().items(expectedItems).total(BigDecimal.ONE).build();
        var user = User.builder().username(username).cart(userCart).build();
        var userOrder = UserOrder.builder().items(expectedItems).build();

        when(userRepository.findByUsername(username)).thenReturn(user);
        lenient().when(orderRepository.save(userOrder)).thenReturn(null);

        var order = controller.submit(username).getBody();

        assertEquals(expectedItems, order.getItems());
    }

    @Test
    public void submitReturnsNotFoundStatusWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        var statusCode = controller.submit("username").getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void getOrdersForUserReturnsListOfUserOrders() {
        var username = "maggie";
        var user = User.builder().username(username).build();
        var expectedUserOrders = Collections.singletonList(UserOrder.builder().user(user).build());
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(expectedUserOrders);

        var actualUserOrders = controller.getOrdersForUser(username).getBody();

        assertEquals(expectedUserOrders, actualUserOrders);
    }

    @Test
    public void getOrdersForUserReturnsNotFoundStatusWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        var statusCode = controller.getOrdersForUser("username").getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }
}
