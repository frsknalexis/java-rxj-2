package com.reactive.spring.app.fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

	@Test
	public void fluxTest() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
						//.concatWith(Flux.error(new RuntimeException("An Exception ocurred")))
						.concatWith(Flux.just("After error"))
						.log();
		
		stringFlux
			.subscribe(System.out::println, (e) -> {
				System.err.println("Exception is " + e );
			}, () -> System.out.println("Completed"));
	}
	
	@Test
	public void fluxTestElementsWithoutError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
										.log();
		
		StepVerifier.create(stringFlux)
					.expectNext("Spring")
					.expectNext("Spring Boot")
					.expectNext("Reactive Spring")
					.verifyComplete();
	}
	
	@Test
	public void fluxTestElementsWithError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
										.concatWith(Flux.error(new RuntimeException("An Exception ocurred")))
										.log();
		
		StepVerifier.create(stringFlux)
						.expectNext("Spring")
						.expectNext("Spring Boot")
						.expectNext("Reactive Spring")
						//.expectError(RuntimeException.class)
						.expectErrorMessage("An Exception ocurred")
						.verify();
	}
	
	@Test
	public void fluxTestElementsCountWithError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("An Exception ocurred")))
				.log();
		
		StepVerifier.create(stringFlux)
					.expectNextCount(3)
					.expectErrorMessage("An Exception ocurred")
					.verify();
	}
	
	@Test
	public void fluxTestElementsWithError1() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("An Exception ocurred")))
				.log();
		
		StepVerifier.create(stringFlux)
					.expectNext("Spring", "Spring Boot", "Reactive Spring")
					.expectErrorMessage("An Exception ocurred")
					.verify();
	}
	
	@Test
	public void monoTest() {
		Mono<String> monoString = Mono.just("Spring");
		
		StepVerifier.create(monoString.log())
					.expectNext("Spring")
					.verifyComplete();
	}
	
	@Test
	public void monoTestError() {
		
		StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
					.expectError(RuntimeException.class)
					.verify();
	}
}
