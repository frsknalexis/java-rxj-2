package com.reactive.spring.client.app.service.impl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactive.spring.client.app.constants.ItemConstants;
import com.reactive.spring.client.app.domain.Item;
import com.reactive.spring.client.app.service.ItemService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("itemService")
public class ItemServiceImpl implements ItemService {
	

	@Autowired
	private WebClient webClient;
	
	@Override
	public Flux<Item> getAllItems() {
		Flux<Item> fluxItems = webClient.get()
										.uri(ItemConstants.API_ITEMS_FUNCTIONAL_END_POINT)
										.accept(MediaType.APPLICATION_JSON)
										.exchange()
										.flatMapMany((response) -> {
											Flux<Item> items = response.bodyToFlux(Item.class);
											return items;
										});
		return fluxItems;
	}

	@Override
	public Mono<Item> getOneItem(String id) {
		Mono<Item> monoItem = webClient.get()
									.uri(ItemConstants.API_ITEMS_FUNCTIONAL_END_POINT.concat("/{id}"), Collections.singletonMap("id", id))
									.accept(MediaType.APPLICATION_JSON)
									.exchange()
									.flatMap((response) -> { 
										Mono<Item> item = response.bodyToMono(Item.class);
										return item;
									});
		return monoItem;
	}

	@Override
	public Mono<Item> createItem(Item item) {
		Mono<Item> monoItem = webClient.post()
									.uri(ItemConstants.API_ITEMS_FUNCTIONAL_END_POINT)
									.contentType(MediaType.APPLICATION_JSON)
									.accept(MediaType.APPLICATION_JSON)
									.body(BodyInserters.fromValue(item))
									.exchange()
									.flatMap((response) ->  {
										Mono<Item> itemResponse = response.bodyToMono(Item.class);
										return itemResponse;
									});
		return monoItem;
	}

	@Override
	public Mono<Item> updateItem(Item item, String id) {
		Mono<Item> monoItem = webClient.put()
							.uri(ItemConstants.API_ITEMS_FUNCTIONAL_END_POINT.concat("/{id}"), Collections.singletonMap("id", id))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.body(BodyInserters.fromValue(item))
							.exchange()
							.flatMap((response) -> {
								Mono<Item> itemResponse = response.bodyToMono(Item.class);
								return itemResponse;
							});
		return monoItem;
	}

	@Override
	public Mono<Void> deleteItem(String id) {
		Mono<Void> monoReturn = webClient.delete()
							.uri(ItemConstants.API_ITEMS_FUNCTIONAL_END_POINT.concat("/{id}"), Collections.singletonMap("id", id))
							.exchange()
							.then();
		return monoReturn;
	}
}