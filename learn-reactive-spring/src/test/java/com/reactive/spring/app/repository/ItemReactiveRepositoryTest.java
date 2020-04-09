package com.reactive.spring.app.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;

import com.reactive.spring.app.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@DirtiesContext
public class ItemReactiveRepositoryTest {

	@Autowired
	@Qualifier("itemRepository")
	private ItemRepository itemRepository;
	
	List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 149.99));
	
	@BeforeEach
	public void setUp() {
		itemRepository.deleteAll()
					.thenMany(Flux.fromIterable(itemList))
					.flatMap((item) -> itemRepository.save(item))
					.doOnNext((item) -> {
						System.out.println("Inserted item is: " + item);
					})
					.blockLast();
					
	}
	
	@Test
	public void getAllItems() {
		StepVerifier.create(itemRepository.findAll())
					.expectSubscription()
					.expectNextCount(5)
					.verifyComplete();
	}
	
	@Test
	public void getItemById() {
		StepVerifier.create(itemRepository.findById("ABC"))
					.expectSubscription()
					.expectNextMatches((item) -> item.getDescription().equals("Bose Headphones"))
					.verifyComplete();
	}
	
	@Test
	public void getItemByDescription() {
		StepVerifier.create(itemRepository.findByDescription("Bose Headphones").log("getItemByDescription: "))
					.expectSubscription()
					.expectNextCount(1)
					.verifyComplete();
	}
	
	@Test
	public void createItem() {
		Item item = new Item(null, "Google Home Mini", 30.00);
		Mono<Item> monoItem = itemRepository.save(item);
		
		StepVerifier.create(monoItem.log("Save Item: "))
					.expectSubscription()
					.expectNextMatches((i) -> {
						return i.getId() != null && i.getDescription().equals("Google Home Mini");
					})
					.verifyComplete();
	}
	
	@Test
	public void updateItem() {
		
		double newPrice = 520.00;
		
		Mono<Item> monoItem = itemRepository.findByDescription("LG TV");
		
		Mono<Item> updatedItem = monoItem.map((item) -> {
												item.setPrecio(newPrice);
												return item;
											})
											.flatMap((item) -> {
												return itemRepository.save(item);
											});
		
		StepVerifier.create(updatedItem.log("Update Item: "))
					.expectSubscription()
					.expectNextMatches((item) -> item.getPrecio() == 520)
					.verifyComplete();
	}
	
	@Test
	public void deleteItemById() {
		Mono<Void> itemDeleted = itemRepository.findById("ABC")
												.map((item) -> item.getId()) // transform from one type to another type
												.flatMap((id) -> {
													return itemRepository.deleteById(id);
												});
		
		StepVerifier.create(itemDeleted.log())
					.expectSubscription()
					.verifyComplete();
		
		StepVerifier.create(itemRepository.findAll().log("The new List of Item: "))
					.expectSubscription()
					.expectNextCount(4)
					.verifyComplete();
	}
	
	@Test
	public void deleteItem() {
		Mono<Void> itemDeleted = itemRepository.findByDescription("LG TV")
											.flatMap((item) -> {
												return itemRepository.delete(item);
											});
		
		StepVerifier.create(itemDeleted.log())
					.expectSubscription()
					.verifyComplete();
		
		StepVerifier.create(itemRepository.findAll().log("The new List of Items: "))
					.expectSubscription()
					.expectNextCount(4)
					.verifyComplete();
	}
}
