package com.sakura.languageschool.api.exception;

import lombok.Data;

import java.util.List;

import org.springframework.validation.ObjectError;

@Data
public class BeanValidationException extends Exception{
	List<ObjectError> errors;
	public BeanValidationException(List<ObjectError> errors) {
		this.errors = errors;
	}
}
