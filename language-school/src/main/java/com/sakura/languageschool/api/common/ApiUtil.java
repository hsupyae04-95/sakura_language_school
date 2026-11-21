package com.sakura.languageschool.api.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiUtil {
	 public <T> ResponseEntity<ApiSuccessResponse<T>> buildSuccessResponse(HttpStatus httpStatus, String code, String message, T body) {
		 ApiSuccessResponse<T> response = new ApiSuccessResponse<T>(httpStatus, SuccessCode.SUCCESS.toString(), message, body);
		 
		 return new ResponseEntity<ApiSuccessResponse<T>>(response, new HttpHeaders(), response.getStatus());
	 }
}
