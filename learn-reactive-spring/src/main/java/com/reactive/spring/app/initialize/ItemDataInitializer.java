package com.reactive.spring.app.initialize;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.reactive.spring.app.document.Item;
import com.reactive.spring.app.document.ItemCapped;
import com.reactive.spring.app.repository.ItemCappedRepository;
import com.reactive.spring.app.repository.ItemRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

	@Autowired
	@Qualifier("itemRepository")
	private ItemRepository itemRepository;
	
	@Autowired
	@Qualifier("itemCappedRepository")
	private ItemCappedRepository itemCappedRepository;
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Override
	public void run(String... args) throws Exception {
		initialDataSetUp();
		createCappedCollection();
		dataSetUpForCappedCollection();
	}

	private void createCappedCollection() {
		mongoOperations.dropCollection(ItemCapped.class);
		mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
	}
	
	public List<Item> data() {
		return Arrays.asList(new Item(null, "Samsung TV", 399.99),
					new Item(null, "LG TV", 329.99),
					new Item(null, "Apple Watch", 349.99),
					new Item("ABC", "Beats HeadPhones", 149.99));
	}
 	
	public void dataSetUpForCappedCollection() {
		Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
												.map((i) -> new ItemCapped(null, "Random Item " + i, (100.00 + i)));
		
		itemCappedRepository
					.insert(itemCappedFlux)
					.subscribe((itemCapped) -> {
						log.info("Inserted ItemCapped is " + itemCapped);
					});
					
	}
	
	private void initialDataSetUp() {
		itemRepository.deleteAll()
					.thenMany(Flux.fromIterable(data()))
					.flatMap((item) -> {
						return itemRepository.save(item);
					})
					.thenMany(itemRepository.findAll())
					.subscribe((item) -> System.out.println("Item Inserted from CommandLineRunner: " + item));
	}
}
