package com.reactive.spring.app.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactive.spring.app.constants.ItemConstants;
import com.reactive.spring.app.document.ItemCapped;
import com.reactive.spring.app.repository.ItemCappedRepository;

import reactor.core.publisher.Flux;

@RestController
public class ItemStreamRestController {

	@Autowired
	@Qualifier("itemCappedRepository")
	private ItemCappedRepository itemCappedRepository;
	
	@GetMapping(value = ItemConstants.ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<ItemCapped> getItemsStream() {
		return itemCappedRepository.findItemsBy();
	}
}
