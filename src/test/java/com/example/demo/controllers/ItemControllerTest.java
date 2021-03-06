package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_all_existing_items() {
        Item item1 = new Item();
        item1.setName("ps4");
        Item item2 = new Item();
        item2.setName("ps5");
        when(itemRepository.findAll()).thenReturn(List.of(item1,item2));

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("ps4", items.get(0).getName());
        assertEquals("ps5", items.get(1).getName());
    }

    @Test
    public void get_all_no_items() {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    public void get_existing_item_by_id() {
        Item item = new Item();
        item.setId(2L);
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(2L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item i = response.getBody();
        assertNotNull(i);
        assertEquals(Optional.of(2L).get(), i.getId());
    }

    @Test
    public void get_non_existing_item_by_id() {
        final ResponseEntity<Item> response = itemController.getItemById(2L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Item i = response.getBody();
        assertNull(i);
    }

    @Test
    public void get_existing_items_by_name() {
        Item item1 = new Item();
        item1.setName("ps4");
        Item item2 = new Item();
        item2.setName("ps5");
        when(itemRepository.findByName("playstation")).thenReturn(List.of(item1,item2));

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("playstation");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("ps4", items.get(0).getName());
        assertEquals("ps5", items.get(1).getName());
    }

    @Test
    public void get_non_existing_items_by_name() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("playstation");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNull(items);
    }

}
