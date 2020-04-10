package com.reactive.spring.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;

import com.reactive.spring.app.document.ItemCapped;

import reactor.core.publisher.Flux;

@Repository("itemCappedRepository")
public interface ItemCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {

	@Tailable
	Flux<ItemCapped> findItemsBy(); 
}
