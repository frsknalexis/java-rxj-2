package com.reactive.spring.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.reactive.spring.app.document.Item;

import reactor.core.publisher.Mono;

@Repository("itemRepository")
public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

	Mono<Item> findByDescription(String description);
	
}
