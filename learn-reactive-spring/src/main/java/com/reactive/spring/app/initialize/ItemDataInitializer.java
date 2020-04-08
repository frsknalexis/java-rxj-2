package com.reactive.spring.app.initialize;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.reactive.spring.app.document.Item;
import com.reactive.spring.app.repository.ItemRepository;

import reactor.core.publisher.Flux;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

	@Autowired
	@Qualifier("itemRepository")
	private ItemRepository itemRepository;
	
	@Override
	public void run(String... args) throws Exception {
		initialDataSetUp();
	}

	public List<Item> data() {
		return Arrays.asList(new Item(null, "Samsung TV", 399.99),
					new Item(null, "LG TV", 329.99),
					new Item(null, "Apple Watch", 349.99),
					new Item("ABC", "Beats HeadPhones", 149.99));
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
