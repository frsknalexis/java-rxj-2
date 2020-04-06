package com.reactive.spring.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest
public class FluxAndMonoControllerTest {

	@Autowired
	private WebTestClient webTestClient;
	
	@Test
	public void fluxApproach1() {
		Flux<Integer> integerFlux = webTestClient.get()
											.uri("/flux")
											.accept(MediaType.APPLICATION_JSON)
											.exchange()
											.expectStatus().isOk()
											.returnResult(Integer.class)
											.getResponseBody();
		
		StepVerifier.create(integerFlux)
					.expectSubscription()
					.expectNext(1)
					.expectNext(2)
					.expectNext(3)
					.expectNext(4)
					.verifyComplete();
					
	}
	
	@Test
	public void fluxApproach2() {
		webTestClient.get()
					.uri("/flux")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBodyList(Integer.class)
					.hasSize(4);
	}
	
	@Test
	public void fluxApproach3() {
		
		List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);
		
		EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get()
																	.uri("/flux")
																	.accept(MediaType.APPLICATION_JSON)
																	.exchange()
																	.expectStatus().isOk()
																	.expectBodyList(Integer.class)
																	.returnResult();
		assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
	}
	
	@Test
	public void fluxApproach4() {
		
		List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);
		
		webTestClient.get()
					.uri("/flux")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectBodyList(Integer.class)
					.consumeWith((response) -> {
						assertEquals(expectedIntegerList, response.getResponseBody());
					});
	}
	
	@Test
	public void fluxStream() {
		Flux<Long> longStreamFlux = webTestClient.get()
												.uri("/fluxstream")
												.accept(MediaType.APPLICATION_STREAM_JSON)
												.exchange()
												.expectStatus().isOk()
												.returnResult(Long.class)
												.getResponseBody();
		
		StepVerifier.create(longStreamFlux)
					.expectSubscription()
					.expectNext(0l, 1l, 2l)
					.thenCancel()
					.verify();
	}
	
	@Test
	public void mono() {
		
		Integer expectedInteger = new Integer(1);
		
		webTestClient.get()
					.uri("/mono")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectBody(Integer.class)
					.consumeWith((response) -> {
						assertEquals(expectedInteger, response.getResponseBody());
					});
	}
}
