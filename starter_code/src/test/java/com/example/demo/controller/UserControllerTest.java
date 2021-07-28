package com.example.demo.controller;

import com.example.demo.controller.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserController controller;

    @Before
    public void init() {
        controller = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
        when(cartRepository.save(any())).thenReturn(new Cart());
    }

    @Test
    public void findByIdReturnsAUserWithTheId() {
        var id = 1L;
        var expectedUser = User.builder().id(id).username("username").password("password").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        var actualUser = controller.findById(id).getBody();

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void findByUsernameReturnsUserWithTheUserName() {
        var username = "maggie";
        var expectedUser = User.builder().username(username).password("password").build();
        when(userRepository.findByUsername(username)).thenReturn(expectedUser);

        var actualUser = controller.findByUserName(username).getBody();

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void findByUsernameReturnNotFoundStatusWhenUserIsNotFound() {
        var username = "maggie";
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        var statusCode = controller.findByUserName(username).getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void createUserReturnsBadRequestStatusWhenPasswordIsLTFiveCharacters() {
        var userRequest = CreateUserRequest.builder().username("maggie").password("123").confirmPassword("123").build();

        var statusCode = controller.createUser(userRequest).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, statusCode);
    }

    @Test
    public void createUserReturnsBadRequestStatusWhenPasswordsDontMatch() {
        var userRequest = CreateUserRequest.builder().username("maggie").password("12345").confirmPassword("123456").build();

        var statusCode = controller.createUser(userRequest).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, statusCode);
    }

    @Test
    public void createUserReturnsUserWithEncryptedPassword() {
        var password = "1234567890";
        var encryptedPassword = "aodfniewnvownow";
        var userRequest = CreateUserRequest.builder().username("maggie").password(password).confirmPassword(password).build();
        when(bCryptPasswordEncoder.encode(password)).thenReturn(encryptedPassword);

        var user = controller.createUser(userRequest).getBody();

        assertEquals(userRequest.getUsername(), user.getUsername());
        assertEquals(encryptedPassword, user.getPassword());
    }
}
