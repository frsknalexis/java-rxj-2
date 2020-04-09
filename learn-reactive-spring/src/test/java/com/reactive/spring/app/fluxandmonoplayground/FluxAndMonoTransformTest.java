package com.reactive.spring.app.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTest {

	List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");
	
	@Test
	public void transformUsingMap() {
		Flux<String> namesFlux = Flux.fromIterable(names)
							.map((s) -> {
								return s.toUpperCase();
							})
							.log();
		
		StepVerifier.create(namesFlux)
					.expectNext("ADAM", "ANNA", "JACK", "JENNY")
					.verifyComplete();
	}
	
	@Test
	public void transformUsingMapLength() {
		Flux<Integer> namesFlux = Flux.fromIterable(names)
							.map((s) -> {
								return s.length();
							})
							.log();
		
		StepVerifier.create(namesFlux)
					.expectNext(4, 4, 4, 5)
					.verifyComplete();
	}
	
	@Test
	public void transformUsingMapLengthRepeat() {
		Flux<Integer> namesFlux = Flux.fromIterable(names)
						.map((s) -> {
							return s.length();
						})
						.repeat(1)
						.log();
		
		StepVerifier.create(namesFlux)
					.expectNext(4, 4, 4, 5, 4, 4, 4, 5)
					.verifyComplete();
	}
	
	@Test
	public void transformUsingMapFilter() {
		Flux<String> nameFlux = Flux.fromIterable(names)
						.filter((s) -> {
							return s.length() > 4;
						})
						.map((s) -> {
							return s.toUpperCase();
						})
						.log();
		
		StepVerifier.create(nameFlux)
					.expectNext("JENNY")
					.verifyComplete();
	}
	
	@Test
	public void transformUsingFlatMap() {
		Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
								.flatMap((s) -> {
									return Flux.fromIterable(convertToList(s));
								})
								.log(); // db o external service call the returns a flux
		
		StepVerifier.create(stringFlux)
					.expectNextCount(12)
					.verifyComplete();
	}
	
	public List<String> convertToList(String s) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Arrays.asList(s, "newValue");
	}
	
	@Test
	public void transformUsingFlatMapUsingParallel() {
		Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
										.window(2) //Flux<Flux<String>> -> (A, B), (C, D), (E, F)
										.flatMap((s) -> {
											return s.map(this::convertToList).subscribeOn(Schedulers.parallel()); //Flux<List<String>>
										})
										.flatMap((s) -> Flux.fromIterable(s))
										.log(); //Flux<String>
		
		StepVerifier.create(stringFlux)
					.expectNextCount(12)
					.verifyComplete();
	}
	
	@Test
	public void transformUsingFlatMapParallelMaintainOrder() {
		Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
										.window(2) //Flux<Flux<String>> -> (A, B), (C, D), (E, F)
										/*.concatMap((s) -> {
											return s.map(this::convertToList).subscribeOn(Schedulers.parallel()); //Flux<List<String>>
										})*/
										.flatMapSequential((s) -> {
											return s.map(this::convertToList).subscribeOn(Schedulers.parallel()); //Flux<List<String>>
										})
										.flatMap((s) -> Flux.fromIterable(s))
										.log(); //Flux<String>
		
		StepVerifier.create(stringFlux)
					.expectNextCount(12)
					.verifyComplete();
	}
}
