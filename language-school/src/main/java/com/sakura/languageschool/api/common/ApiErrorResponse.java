package com.sakura.languageschool.api.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ApiErrorResponse extends ApiResponse{
	
	private List<String> errors;

	public ApiErrorResponse(HttpStatus status, String code, String message) {
		super(status, code, message);
		this.errors = Collections.emptyList();
	}
	
	public ApiErrorResponse(HttpStatus status, String errorCode, String message, String error) {
        super(status,errorCode,message);

        errors = Arrays.asList(error);
    }

	public List<String> getErrors() {
		return errors;
	}

}
