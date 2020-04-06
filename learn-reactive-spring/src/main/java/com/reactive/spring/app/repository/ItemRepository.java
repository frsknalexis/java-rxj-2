package com.reactive.spring.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.reactive.spring.app.document.Item;

@Repository("itemRepository")
public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

}
