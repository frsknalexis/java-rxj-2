package com.reactive.spring.app.handler;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.spring.app.constants.ItemConstants;
import com.reactive.spring.app.document.Item;
import com.reactive.spring.app.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ItemHandler {

	@Autowired
	@Qualifier("itemRepository")
	private ItemRepository itemRepository;
	
	public Mono<ServerResponse> getAllItems(ServerRequest request) {
		Flux<Item> itemsFlux = itemRepository.findAll();
		return ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(itemsFlux, Item.class);
	}
	
	public Mono<ServerResponse> getOneItem(ServerRequest request) {
		String id = request.pathVariable("id");
		return itemRepository.findById(id)
							.flatMap((item) -> {
								return ServerResponse.ok()
													.contentType(MediaType.APPLICATION_JSON)
													.body(BodyInserters.fromValue(item));
							})
							.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> createItem(ServerRequest request) {
		Mono<Item> monoItem = request.bodyToMono(Item.class);
		return monoItem
				.flatMap((i) -> {
					return itemRepository.save(i);
				})
				.flatMap((item) -> {
					return ServerResponse
							.created(URI.create(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/".concat(item.getId()))))
							.contentType(MediaType.APPLICATION_JSON)
							.body(BodyInserters.fromValue(item));
				});
	}
	
	public Mono<ServerResponse> updateItem(ServerRequest request) {
		Mono<Item> monoItem = request.bodyToMono(Item.class);
		String id = request.pathVariable("id");
		Mono<Item> itemDB = itemRepository.findById(id);
		return itemDB.zipWith(monoItem)
							.map((tupla) -> {
								Item iDB = tupla.getT1();
								Item iR = tupla.getT2();
								iDB.setDescription(iR.getDescription());
								iDB.setPrecio(iR.getPrecio());
								return iDB;
							})
							.flatMap((item) -> {
								return itemRepository.save(item);
							})
							.flatMap((i) -> {
								return ServerResponse
										.created(URI.create(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/".concat(i.getId()))))
										.contentType(MediaType.APPLICATION_JSON)
										.body(BodyInserters.fromValue(i));
							})
							.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> deleteItem(ServerRequest request) {
		String id = request.pathVariable("id");
		return itemRepository.findById(id)
							.flatMap((item) -> {
								return itemRepository.delete(item);
							})
							.then(ServerResponse.noContent().build())
							.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> itemException(ServerRequest request) {
		throw new RuntimeException("RuntimeException Occurred");
	}
}