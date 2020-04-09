package com.reactive.spring.app.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoFilterTest {

	List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");
	
	@Test
	public void filterTest() {
		Flux<String> namesFlux = Flux.fromIterable(names)
									.filter((s) -> {
										return s.startsWith("a");
									})
									.log(); //adam, anna
		
		StepVerifier.create(namesFlux)
					.expectNext("adam", "anna")
					.verifyComplete();
		
	}
	
	@Test
	public void filterTestLength() {
		Flux<String> namesFlux = Flux.fromIterable(names)
						.filter((s) -> {
							return s.length() > 4;
						})
						.log(); // jenny
		
		StepVerifier.create(namesFlux)
					.expectNext("jenny")
					.verifyComplete();
	}
}
