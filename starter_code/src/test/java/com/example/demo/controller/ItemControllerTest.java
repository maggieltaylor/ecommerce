package com.example.demo.controller;

import com.example.demo.controller.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    @Mock private ItemRepository itemRepository;

    private ItemController controller;

    @Before
    public void init() {
        controller = new ItemController(itemRepository);
    }

    @Test
    public void getItemsReturnsListOfItems() {
        var expectedItems = Collections.singletonList(Item.builder().name("item").build());
        when(itemRepository.findAll()).thenReturn(expectedItems);

        var actualItems = controller.getItems().getBody();

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void getItemByIdReturnsItem() {
        var id = 3L;
        var expectedItem = Item.builder().name("item").build();
        when(itemRepository.findById(id)).thenReturn(Optional.of(expectedItem));

        var actualItem = controller.getItemById(id).getBody();

        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void getItemsByNameReturnsNotFoundStatusWhenItemsAreNull() {
        when(itemRepository.findByName(any())).thenReturn(null);

        var statusCode = controller.getItemsByName("name").getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void getItemsByNameReturnsNotFoundStatusWhenItemsAreEmpty() {
        when(itemRepository.findByName(any())).thenReturn(Collections.emptyList());

        var statusCode = controller.getItemsByName("name").getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void getItemsByNameReturnsListOfItems() {
        var name = "maggie taylor";
        var expectedItems = Collections.singletonList(Item.builder().name("item").build());
        when(itemRepository.findByName(name)).thenReturn(expectedItems);

        var actualItems = controller.getItemsByName(name).getBody();

        assertEquals(expectedItems, actualItems);
    }
}
