package com.reactive.spring.client.app.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5317247600312542081L;

	private String id;
	
	private String description;
	
	private Double precio;
}
