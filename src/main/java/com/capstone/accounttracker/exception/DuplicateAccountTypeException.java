package com.capstone.accounttracker.exception;

public class DuplicateAccountTypeException extends Exception{
	public DuplicateAccountTypeException() {
		super();
	}
	
	public DuplicateAccountTypeException(String message) {
		super(message);
	}
}
