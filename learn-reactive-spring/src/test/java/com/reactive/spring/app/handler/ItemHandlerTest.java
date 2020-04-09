package com.reactive.spring.app.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.reactive.spring.app.constants.ItemConstants;
import com.reactive.spring.app.document.Item;
import com.reactive.spring.app.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class ItemHandlerTest {

	@Autowired
	private WebTestClient webTestClient;
	
	@Autowired
	@Qualifier("itemRepository")
	private ItemRepository itemRepository;
	
	public List<Item> data() {
		return Arrays.asList(new Item(null, "Samsung TV", 399.99),
				new Item(null, "LG TV", 329.99),
				new Item(null, "Apple Watch", 349.99),
				new Item("ABC", "Beats HeadPhones", 149.99));
	}
	
	@BeforeEach
	public void setUp() {
		itemRepository.deleteAll()
					.thenMany(Flux.fromIterable(data()))
					.flatMap((item) -> {
						return itemRepository.save(item);
					})
					.doOnNext((item) -> {
						System.out.println("Inserted Item is: " + item);
					})
					.blockLast();
	}
	
	@Test
	public void getAllItems() {
		webTestClient.get()
					.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBodyList(Item.class)
					.hasSize(4);
	}
	
	@Test
	public void getAllItemsApproach2() {
		webTestClient.get()
					.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBodyList(Item.class)
					.consumeWith((response) -> {
						List<Item> items = response.getResponseBody();
						items.forEach((item) -> {
							assertTrue(item.getId() != null);
						});
					});
	}
	
	@Test
	public void getAllItemsApproach3() {
		Flux<Item> itemsFlux = webTestClient.get()
									.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
									.accept(MediaType.APPLICATION_JSON)
									.exchange()
									.expectStatus().isOk()
									.expectHeader().contentType(MediaType.APPLICATION_JSON)
									.returnResult(Item.class)
									.getResponseBody();
		
		StepVerifier.create(itemsFlux.log("values from setUp: "))
					.expectSubscription()
					.expectNextCount(4)
					.verifyComplete();
	}
	
	@Test
	public void getOneItem() {
		webTestClient.get()
					.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), Collections.singletonMap("id", "ABC"))
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBody(Item.class)
					.consumeWith((response) -> {
						Item item = response.getResponseBody();
						assertEquals(149.99, item.getPrecio());
					});
	}
	
	@Test
	public void getOneItemNotFound() {
		webTestClient.get()
					.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), Collections.singletonMap("id", "DEF"))
					.exchange()
					.expectStatus().isNotFound();
	}
	
	@Test
	public void createItem() {
		
		Item item = new Item(null, "Iphone X", 999.99);
		
		webTestClient.post()
					.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(item))
					.exchange()
					.expectStatus().isCreated()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBody(Item.class)
					.consumeWith((response) -> {
						Item itemResponse = response.getResponseBody();
						assertTrue(itemResponse.getId() != null);
						assertEquals(item.getDescription(), itemResponse.getDescription());
					});
	}
	
	@Test
	public void updateItem() {
		
		double newPrice =129.99;
        Item item = new Item(null,"Beats HeadPhones", newPrice);
		
		webTestClient.put()
					.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), Collections.singletonMap("id", "ABC"))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(item))
					.exchange()
					.expectStatus().isCreated()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBody(Item.class)
					.consumeWith((response) -> {
						Item itemResponse = response.getResponseBody();
						assertEquals(newPrice, itemResponse.getPrecio());
					});
	}
	
	@Test
	public void deleteItem() {
		webTestClient.delete()
					.uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), Collections.singletonMap("id", "ABC"))
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isNoContent()
					.expectBody(Void.class);
	}
	
	@Test
	public void runtimeException() {
		webTestClient.get()
					.uri("/func/runtimeException")
					.exchange()
					.expectStatus().is5xxServerError()
					.expectBody(String.class)
					.consumeWith((response) -> {
						String stringResponse = response.getResponseBody();
						assertEquals("RuntimeException Occurred", stringResponse);
					});
	}
}
