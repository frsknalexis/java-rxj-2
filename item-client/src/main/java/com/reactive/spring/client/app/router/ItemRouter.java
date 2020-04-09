package com.reactive.spring.client.app.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.spring.client.app.constants.ItemConstants;
import com.reactive.spring.client.app.handler.ItemHandler;

@Configuration
public class ItemRouter {

	@Bean
	public RouterFunction<ServerResponse> itemRoutes(ItemHandler handler) {
		return RouterFunctions.route(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_CLIENT_V1), handler::getAllItems)
					.andRoute(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_CLIENT_V1 + "/{id}"), handler::getOneItem)
					.andRoute(RequestPredicates.POST(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_CLIENT_V1), handler::createItem)
					.andRoute(RequestPredicates.PUT(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_CLIENT_V1 + "/{id}"), handler::updateItem)
					.andRoute(RequestPredicates.DELETE(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_CLIENT_V1 + "/{id}"), handler::deleteItem);
	}
}
