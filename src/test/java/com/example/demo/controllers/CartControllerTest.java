package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void add_to_cart_happy_path() {
        User existingUser = new User();
        existingUser.setUsername("alfianlosari");

        Cart cart = new Cart();
        cart.setUser(existingUser);

        existingUser.setCart(cart);
        when(userRepo.findByUsername("alfianlosari")).thenReturn(existingUser);

        Item item = new Item();
        item.setId(2L);
        item.setPrice(BigDecimal.valueOf(500));
        when(itemRepo.findById(2L)).thenReturn(Optional.of(item));

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("alfianlosari");
        cartRequest.setItemId(2L);
        cartRequest.setQuantity(5);

        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNotNull(c);

        assertEquals(existingUser.getUsername(), c.getUser().getUsername());
        assertEquals(5, c.getItems().size());
        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(5)), c.getTotal());
    }

    @Test
    public void add_to_cart_non_existing_user() {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("alfianlosari");
        cartRequest.setItemId(2L);
        cartRequest.setQuantity(5);

        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNull(c);
    }

    @Test
    public void add_to_cart_non_existing_item() {
        User existingUser = new User();
        existingUser.setUsername("alfianlosari");

        Cart cart = new Cart();
        cart.setUser(existingUser);

        existingUser.setCart(cart);
        when(userRepo.findByUsername("alfianlosari")).thenReturn(existingUser);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("alfianlosari");
        cartRequest.setItemId(2L);
        cartRequest.setQuantity(5);

        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNull(c);
    }

    @Test
    public void remove_from_cart_happy_path() {
        User existingUser = new User();
        existingUser.setUsername("alfianlosari");

        Cart cart = new Cart();
        cart.setUser(existingUser);

        existingUser.setCart(cart);
        when(userRepo.findByUsername("alfianlosari")).thenReturn(existingUser);

        Item item = new Item();
        item.setId(2L);
        item.setPrice(BigDecimal.valueOf(500));
        when(itemRepo.findById(2L)).thenReturn(Optional.of(item));

        cart.addItem(item);
        cart.addItem(item);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("alfianlosari");
        cartRequest.setItemId(2L);
        cartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNotNull(c);

        assertEquals(existingUser.getUsername(), c.getUser().getUsername());
        assertEquals(1, c.getItems().size());
        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(1)), c.getTotal());
    }

    @Test
    public void remove_from_cart_non_existing_user() {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("alfianlosari");

        final ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNull(c);
    }

    @Test
    public void remove_from_cart_non_existing_item() {
        User existingUser = new User();
        existingUser.setUsername("alfianlosari");

        Cart cart = new Cart();
        cart.setUser(existingUser);

        existingUser.setCart(cart);
        when(userRepo.findByUsername("alfianlosari")).thenReturn(existingUser);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("alfianlosari");
        cartRequest.setItemId(2L);
        cartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNull(c);
    }
}
