package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submit_cart_happy_path() {
        User existingUser = new User();
        existingUser.setUsername("alfianlosari");

        Cart cart = new Cart();
        cart.setUser(existingUser);

        existingUser.setCart(cart);
        when(userRepo.findByUsername("alfianlosari")).thenReturn(existingUser);

        Item item = new Item();
        item.setId(2L);
        item.setPrice(BigDecimal.valueOf(500));

        cart.addItem(item);
        cart.addItem(item);

        final ResponseEntity<UserOrder> response = orderController.submit("alfianlosari");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder o = response.getBody();
        assertNotNull(o);

        assertEquals(existingUser.getUsername(), o.getUser().getUsername());
        assertEquals(2, o.getItems().size());
        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(2)), o.getTotal());
    }

    @Test
    public void submit_cart_non_existing_user() {
        final ResponseEntity<UserOrder> response = orderController.submit("alfianlosari");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        UserOrder o = response.getBody();
        assertNull(o);
    }

    @Test
    public void get_orders_for_user_happy_path() {
        User existingUser = new User();
        existingUser.setUsername("alfianlosari");
        when(userRepo.findByUsername("alfianlosari")).thenReturn(existingUser);

        UserOrder userOrder1 = new UserOrder();
        userOrder1.setUser(existingUser);
        userOrder1.setId(1L);
        userOrder1.setTotal(BigDecimal.valueOf(500));

        UserOrder userOrder2 = new UserOrder();
        userOrder2.setUser(existingUser);
        userOrder2.setId(2L);
        userOrder2.setTotal(BigDecimal.valueOf(1000));

        when(orderRepo.findByUser(existingUser)).thenReturn(Arrays.asList(userOrder1, userOrder2));

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("alfianlosari");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);

        assertEquals(2, orders.size());
        assertEquals(Optional.of(1L).get(), orders.get(0).getId());
        assertEquals(Optional.of(2L).get(), orders.get(1).getId());

        assertEquals(BigDecimal.valueOf(500), orders.get(0).getTotal());
        assertEquals(BigDecimal.valueOf(1000), orders.get(1).getTotal());
    }

    @Test
    public void get_orders_for_non_existing_user() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("alfianlosari");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }


}
