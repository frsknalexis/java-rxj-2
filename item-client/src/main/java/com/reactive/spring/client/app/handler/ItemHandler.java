package com.reactive.spring.client.app.handler;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.spring.client.app.constants.ItemConstants;
import com.reactive.spring.client.app.domain.Item;
import com.reactive.spring.client.app.service.ItemService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ItemHandler {

	@Autowired
	@Qualifier("itemService")
	private ItemService itemService;
	
	public Mono<ServerResponse> getAllItems(ServerRequest request) {
		Flux<Item> itemFlux = itemService.getAllItems();
		return ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(itemFlux, Item.class);
	}
	
	public Mono<ServerResponse> getOneItem(ServerRequest request) {
		String id = request.pathVariable("id");
		return itemService.getOneItem(id)
						.flatMap((item) -> {
							return ServerResponse.ok()
												.contentType(MediaType.APPLICATION_JSON)
												.body(BodyInserters.fromValue(item));
						})
						.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> createItem(ServerRequest request) {
		Mono<Item> monoBody = request.bodyToMono(Item.class);
		return monoBody
					.flatMap((item) -> {
						return itemService.createItem(item);
					})
					.flatMap((i) -> {
						return ServerResponse
								.created(URI.create(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_CLIENT_V1.concat("/".concat(i.getId()))))
								.contentType(MediaType.APPLICATION_JSON)
								.body(BodyInserters.fromValue(i));
					});
	}
	
	public Mono<ServerResponse> updateItem(ServerRequest request) {
		Mono<Item> monoItem = request.bodyToMono(Item.class);
		String id = request.pathVariable("id");
		
		Mono<Item> itemDB = itemService.getOneItem(id);
		return itemDB.zipWith(monoItem)
					.map((tupla) -> {
						Item iDB = tupla.getT1();
						Item iR = tupla.getT2();
						iDB.setDescription(iR.getDescription());
						iDB.setPrecio(iR.getPrecio());
						return iDB;
					})
					.flatMap((item) -> {
						return itemService.updateItem(item, id);
					})
					.flatMap((i) -> {
						return ServerResponse
								.created(URI.create(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_CLIENT_V1.concat("/".concat(i.getId()))))
								.contentType(MediaType.APPLICATION_JSON)
								.body(BodyInserters.fromValue(i));
					})
					.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> deleteItem(ServerRequest request) {
		String id = request.pathVariable("id");
		Mono<Item> monoItem = itemService.getOneItem(id);
		return monoItem
				.flatMap((item) -> {
					return itemService.deleteItem(item.getId())
							.then(ServerResponse.noContent().build());
				})
				.switchIfEmpty(ServerResponse.notFound().build());
	}
}