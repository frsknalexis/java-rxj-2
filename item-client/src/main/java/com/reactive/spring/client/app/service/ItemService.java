package com.reactive.spring.client.app.service;

import com.reactive.spring.client.app.domain.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {

	Flux<Item> getAllItems();
	
	Mono<Item> getOneItem(String id);
	
	Mono<Item> createItem(Item item);
	
	Mono<Item> updateItem(Item item, String id);
	
	Mono<Void> deleteItem(String id);
}
