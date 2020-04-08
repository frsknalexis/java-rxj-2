package com.reactive.spring.app.controller.v1;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.reactive.spring.app.constants.ItemConstants;
import com.reactive.spring.app.document.Item;
import com.reactive.spring.app.repository.ItemRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemRestController {

	@Autowired
	@Qualifier("itemRepository")
	private ItemRepository itemRepository;
	
	@GetMapping(value = ItemConstants.ITEM_END_POINT_V1)
	public Mono<ResponseEntity<Flux<Item>>> getAllItems() {
		Flux<Item> fluxItems = itemRepository.findAll();
		return Mono.just(ResponseEntity.ok()
										.contentType(MediaType.APPLICATION_JSON)
										.body(fluxItems));
	}
	
	@GetMapping(value = ItemConstants.ITEM_END_POINT_V1 + "/{id}")
	public Mono<ResponseEntity<Item>> getOneItem(@PathVariable(value = "id") String id) {
		Mono<Item> monoItem = itemRepository.findById(id);
		return monoItem.map((item) -> {
			return ResponseEntity.ok()
								.contentType(MediaType.APPLICATION_JSON)
								.body(item);
				})
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping(value = ItemConstants.ITEM_END_POINT_V1)
	public Mono<ResponseEntity<Item>> createItem(@RequestBody Item item) {
		Mono<Item> monoItem = Mono.just(item);
		return monoItem.flatMap((itemRequest) -> {
								return itemRepository.save(itemRequest);
						})
						.map((i) -> ResponseEntity
											.created(URI.create(ItemConstants.ITEM_END_POINT_V1.concat("/".concat(i.getId()))))
											.contentType(MediaType.APPLICATION_JSON)
											.body(i));
	}
	
	@DeleteMapping(value = ItemConstants.ITEM_END_POINT_V1 + "/{id}")
	public Mono<ResponseEntity<Void>> deleteItem(@PathVariable(value = "id") String id) {
		Mono<Item> monoItem = itemRepository.findById(id);
		return monoItem
				.flatMap((item) -> {
					return itemRepository.delete(item)
							.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
				})
				.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));	
	}
	
	@PutMapping(value = ItemConstants.ITEM_END_POINT_V1 + "/{id}")
	public Mono<ResponseEntity<Item>> updateItem(@PathVariable(value = "id") String id, @RequestBody Item item) {
		Mono<Item> itemDB = itemRepository.findById(id);
		return itemDB
				.zipWith(Mono.just(item))
				.map((tupla) -> {
					Item iDB = tupla.getT1();
					Item iR = tupla.getT2();
					iDB.setDescription(iR.getDescription());
					iDB.setPrecio(iR.getPrecio());
					return iDB;
				})
				.flatMap((i) -> {
					i.setDescription(item.getDescription());
					i.setPrecio(item.getPrecio());
					return itemRepository.save(i);
				})
				.map((itemResponse) -> {
					return ResponseEntity.created(URI.create(ItemConstants.ITEM_END_POINT_V1.concat("/".concat(itemResponse.getId()))))
							.contentType(MediaType.APPLICATION_JSON)
							.body(itemResponse);
				})
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
