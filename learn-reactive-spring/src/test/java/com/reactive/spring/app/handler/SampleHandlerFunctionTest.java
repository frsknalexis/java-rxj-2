package com.reactive.spring.app.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@AutoConfigureWebTestClient
@SpringBootTest
public class SampleHandlerFunctionTest {

	@Autowired
	private WebTestClient webTestClient;
	
	@Test
	public void fluxApproach1() {
		Flux<Integer> fluxInteger = webTestClient.get()
								.uri("/api/functional/flux")
								.accept(MediaType.APPLICATION_JSON)
								.exchange()
								.expectStatus().isOk()
								.expectHeader().contentType(MediaType.APPLICATION_JSON)
								.returnResult(Integer.class)
								.getResponseBody();
		
		StepVerifier.create(fluxInteger)
					.expectSubscription()
					.expectNext(1, 2, 3, 4)
					.verifyComplete();
	}
	
	@Test
	public void monoTest() {

		Integer expectedValueInteger = new Integer(1);
		
		webTestClient.get()
					.uri("/api/functional/mono")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBody(Integer.class)
					.consumeWith((response) -> {
						assertEquals(expectedValueInteger, response.getResponseBody());
					});
	}
}
