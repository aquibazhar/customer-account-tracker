package com.capstone.accounttracker.exception;

public class DuplicateAccountNumberException extends Exception{
	
	public DuplicateAccountNumberException() {
		super();
	}
	
	public DuplicateAccountNumberException(String message) {
		super(message);
	}

}
