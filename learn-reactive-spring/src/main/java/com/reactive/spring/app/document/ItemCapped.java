package com.reactive.spring.app.document;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCapped implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4555329293977588771L;

	@Id
	private String id;
	
	private String description;
	
	private Double precio;
}
