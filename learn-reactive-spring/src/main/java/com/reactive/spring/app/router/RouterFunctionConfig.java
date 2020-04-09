package com.reactive.spring.app.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.spring.app.handler.SampleHandlerFunction;

@Configuration
public class RouterFunctionConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(SampleHandlerFunction handler) {
		return RouterFunctions.route(RequestPredicates.GET("/api/functional/flux"), handler::flux)
				.andRoute(RequestPredicates.GET("/api/functional/mono"), handler::mono);
	}
}
