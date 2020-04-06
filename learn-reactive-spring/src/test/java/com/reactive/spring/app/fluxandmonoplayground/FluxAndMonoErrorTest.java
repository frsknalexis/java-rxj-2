package com.reactive.spring.app.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

public class FluxAndMonoErrorTest {

	@Test
	public void fluxErrorHandling() {
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									.concatWith(Flux.just("D"))
									.onErrorResume((e) -> { //This block will be executed
										System.out.println("Exception is: " + e);
										return Flux.just("default", "default1");
									});
		
		StepVerifier.create(stringFlux.log())
					.expectSubscription()
					.expectNext("A", "B", "C")
					//.expectError(RuntimeException.class)
					//.verify();
					.expectNext("default", "default1")
					.verifyComplete();
	}
	
	@Test
	public void fluxErrorHandlingOnErrorReturn() {
		Flux<String> stringFlux = Flux.just("A", "B", "C")
										.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
										.concatWith(Flux.just("D"))
										.onErrorReturn("default");
		
		StepVerifier.create(stringFlux.log())
					.expectSubscription()
					.expectNext("A", "B", "C")
					.expectNext("default")
					.verifyComplete();
	}
	
	@Test
	public void fluxErrorHandlingOnErrorMap() {
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									.concatWith(Flux.just("D"))
									.onErrorMap((e) -> new CustomException(e));
				
		StepVerifier.create(stringFlux.log())
					.expectSubscription()
					.expectNext("A", "B", "C")
					.expectError(CustomException.class)
					.verify();
					
	}
	
	@Test
	public void fluxErrorHandlingOnErrorMapWithRetry() {
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									.concatWith(Flux.just("D"))
									.onErrorMap((e) -> new CustomException(e))
									.retry(2);
		
		StepVerifier.create(stringFlux.log())
					.expectSubscription()
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
					.expectError(CustomException.class)
					.verify();
			
	}
	
	@Test
	public void fluxErrorHandlingOnErrorMapWithRetryBackOff() {
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									.concatWith(Flux.just("D"))
									.onErrorMap((e) -> new CustomException(e))
									.retryWhen(Retry.backoff(2, Duration.ofSeconds(5)));
		
		StepVerifier.create(stringFlux.log())
					.expectSubscription()
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
					.expectNext("A", "B", "C")
					.expectError(CustomException.class)
					.verify();
	}
}
