package com.sakura.languageschool.api.common;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiSuccessResponse<T> extends ApiResponse{
	private T data;
	
	public ApiSuccessResponse(HttpStatus status, String code, String message, T data) {
		super(status, code, message);
		this.data = data;
	}
}
