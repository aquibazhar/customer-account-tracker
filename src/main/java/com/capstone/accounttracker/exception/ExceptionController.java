package com.capstone.accounttracker.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionResponse handleResourceNotFoundException(ResourceNotFoundException exception,
			HttpServletRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setErrorMessage(exception.getMessage());
		exceptionResponse.setUrl(request.getRequestURI());
		return exceptionResponse;
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.CONFLICT)
	public ExceptionResponse handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception,
			HttpServletRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setErrorMessage(exception.getMessage());
		exceptionResponse.setUrl(request.getRequestURI());
		return exceptionResponse;
	}
	
	
	@ExceptionHandler(InsufficientBalanceException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleInsufficientBalanceException(InsufficientBalanceException exception, HttpServletRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setErrorMessage(exception.getMessage());
		exceptionResponse.setUrl(request.getRequestURI());
		return exceptionResponse;
	}
	
	@ExceptionHandler(DuplicateAccountNumberException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.CONFLICT)
	public ExceptionResponse handleDuplicateAccountNumberException(DuplicateAccountNumberException exception, HttpServletRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setErrorMessage(exception.getMessage());
		exceptionResponse.setUrl(request.getRequestURI());
		return exceptionResponse;
	}
	
	@ExceptionHandler(DuplicateAccountTypeException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.CONFLICT)
	public ExceptionResponse handleDuplicateAccountTypeException(DuplicateAccountTypeException exception, HttpServletRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setErrorMessage(exception.getMessage());
		exceptionResponse.setUrl(request.getRequestURI());
		return exceptionResponse;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleException(Exception exception, HttpServletRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse();
		exceptionResponse.setErrorMessage(exception.getMessage());
		exceptionResponse.setUrl(request.getRequestURI());
		return exceptionResponse;
	}

}
