package com.sakura.languageschool.api.exception;

public class ResourceNotFoundException extends RuntimeException{
	String message;
	public ResourceNotFoundException(String message) {
		super(message);
		this.message = message;
	}
}

