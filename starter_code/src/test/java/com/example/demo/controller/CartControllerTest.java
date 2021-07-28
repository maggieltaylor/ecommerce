package com.example.demo.controller;

import com.example.demo.controller.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ItemRepository itemRepository;

    private CartController controller;

    @Before
    public void init() {
        controller = new CartController(userRepository, cartRepository, itemRepository);
    }

    @Test
    public void addToCartReturnsCartWithThreeItemsWhenTwoAreAdded() {
        var username = "maggie";
        var itemId = 1L;
        var existingItems = new ArrayList<Item>();
        existingItems.add(Item.builder().name("first item").price(BigDecimal.ONE).build());
        var cart = Cart.builder().items(existingItems).build();
        var user = User.builder().cart(cart).build();
        var item = Item.builder().name("other item").price(BigDecimal.TEN).build();
        var cartRequest = ModifyCartRequest.builder()
                .username(username)
                .itemId(itemId)
                .quantity(2)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartRepository.save(any())).thenReturn(null);

        var updatedCart = controller.addToCart(cartRequest).getBody();

        assertEquals(3, updatedCart.getItems().size());
    }

    @Test
    public void addToCartReturnsNotFoundWhenUserNotFound() {
        var username = "maggie";
        when(userRepository.findByUsername(username)).thenReturn(null);

        var statusCode = controller.addToCart(ModifyCartRequest.builder().username(username).build()).getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void addToCartReturnsNotFoundWhenItemNotFound() {
        var username = "maggie";
        when(userRepository.findByUsername(username)).thenReturn(User.builder().username(username).build());
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        var statusCode = controller.addToCart(ModifyCartRequest.builder().username(username).itemId(1L).build()).getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void removeFromCartReturnsCartWithZeroItemsWhenOneWasRemoved() {
        var username = "maggie";
        var itemId = 1L;
        var existingItems = new ArrayList<Item>();
        existingItems.add(Item.builder().name("first item").price(BigDecimal.ONE).build());
        var cart = Cart.builder().items(existingItems).build();
        var user = User.builder().cart(cart).build();
        var item = Item.builder().name("item to delete").price(BigDecimal.TEN).build();
        var cartRequest = ModifyCartRequest.builder()
                .username(username)
                .itemId(itemId)
                .quantity(1)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartRepository.save(any())).thenReturn(null);

        var updatedCart = controller.removeFromCart(cartRequest).getBody();

        assertEquals(0, updatedCart.getItems().size());
    }

    @Test
    public void removeFromCartReturnsNotFoundWhenUserNotFound() {
        var username = "maggie";
        when(userRepository.findByUsername(username)).thenReturn(null);

        var statusCode = controller.removeFromCart(ModifyCartRequest.builder().username(username).build()).getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void removeFromCartReturnsNotFoundWhenItemNotFound() {
        var username = "maggie";
        when(userRepository.findByUsername(username)).thenReturn(User.builder().username(username).build());
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        var statusCode = controller.removeFromCart(ModifyCartRequest.builder().username(username).itemId(1L).build()).getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }
}
