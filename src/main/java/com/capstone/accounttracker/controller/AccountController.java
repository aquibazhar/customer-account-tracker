package com.capstone.accounttracker.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.accounttracker.exception.DuplicateAccountNumberException;
import com.capstone.accounttracker.exception.InsufficientBalanceException;
import com.capstone.accounttracker.exception.ResourceAlreadyExistsException;
import com.capstone.accounttracker.exception.ResourceNotFoundException;
import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.model.TransactionDetails;
import com.capstone.accounttracker.service.AccountService;

@RestController
public class AccountController {

	@Autowired
	private AccountService service;

	@PostMapping("/account")
	public ResponseEntity<Account> createAccount(@RequestBody Account account) throws ResourceAlreadyExistsException {
		Optional<Account> accountOptional = service.getAccountByAccountNumber(account.getAccountNumber());
		if (accountOptional.isPresent()) {
			throw new ResourceAlreadyExistsException("Account already exists.");
		}

		Account savedAccount = service.saveAccount(account);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
	}

	@GetMapping("/account")
	public ResponseEntity<List<Account>> getAllAccounts() throws ResourceNotFoundException {
		List<Account> accountList = service.getAllAccounts();
		if (accountList.isEmpty()) {
			throw new ResourceNotFoundException("There are no accounts in the database.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(accountList);
	}

	@GetMapping("/account/{id}")
	public ResponseEntity<Account> getAccountById(@PathVariable long id) throws ResourceNotFoundException {
		Optional<Account> accountOptional = service.getAccountById(id);
		if (accountOptional.isEmpty()) {
			throw new ResourceNotFoundException("Account with this ID doesn't exist.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(accountOptional.get());
	}

	@GetMapping("/account/accountNumber/{accountNumber}")
	public ResponseEntity<Account> getAccountByAccountNumber(@PathVariable long accountNumber)
			throws ResourceNotFoundException {
		Optional<Account> accountOptional = service.getAccountByAccountNumber(accountNumber);
		if (accountOptional.isEmpty()) {
			throw new ResourceNotFoundException("Account with this Account Number doesn't exist.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(accountOptional.get());
	}

	@GetMapping("/account/customer/{customerId}")
	public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable long customerId)
			throws ResourceNotFoundException {
		List<Account> accountList = service.getAccountsByCustomerId(customerId);
		if (accountList.size() == 0) {
			throw new ResourceNotFoundException("No accounts exist for this Customer.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(accountList);
	}

	@PutMapping("/account/{id}")
	public ResponseEntity<Account> updateAccount(@PathVariable long id, @RequestBody Account account)
			throws ResourceNotFoundException {
		Optional<Account> accountOptional = service.getAccountById(id);
		if (accountOptional.isEmpty()) {
			throw new ResourceNotFoundException("Account with this ID doesn't exist.");
		}

		Account updatedAccount = service.updateAccount(account);
		return ResponseEntity.status(HttpStatus.OK).body(updatedAccount);
	}

	@DeleteMapping("/account/{id}")
	public ResponseEntity<Account> deleteAccountById(@PathVariable long id) throws ResourceNotFoundException {
		Optional<Account> accountOptional = service.getAccountById(id);
		if (accountOptional.isEmpty()) {
			throw new ResourceNotFoundException("Account with this ID doesn't exist.");
		}
		service.deleteAccountById(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/account")
	public ResponseEntity<Account> deleteAllAccounts() throws ResourceNotFoundException {
		List<Account> accountList = service.getAllAccounts();
		if (accountList.isEmpty()) {
			throw new ResourceNotFoundException("There are no accounts in the database.");
		}
		service.deleteAllAccounts();
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> transferFunds(@RequestBody TransactionDetails transactionDetails)
			throws DuplicateAccountNumberException, ResourceNotFoundException, InsufficientBalanceException {
		long senderAccountNumber = transactionDetails.getSenderAccountNumber();
		long receiverAccountNumber = transactionDetails.getReceiverAccountNumber();
		long transferAmount = transactionDetails.getAmount();

		// Duplicate Account Number
		if (senderAccountNumber == receiverAccountNumber)
			throw new DuplicateAccountNumberException("Sender's and Receiver's Account Number cannot be same.");

		// Check if both accounts exist
		Optional<Account> senderAccountOptional = service.getAccountByAccountNumber(senderAccountNumber);
		Optional<Account> receiverAccountOptional = service.getAccountByAccountNumber(receiverAccountNumber);

		if (senderAccountOptional.isEmpty())
			throw new ResourceNotFoundException("Provided Sender Account Number is Invalid.");

		if (receiverAccountOptional.isEmpty())
			throw new ResourceNotFoundException("Provided Receiver Account Number is Invalid.");

		Account senderAccount = senderAccountOptional.get();

		// Insufficient Balance
		if (senderAccount.getBalance() < transferAmount)
			throw new InsufficientBalanceException(
					"Sender Account does not have Sufficient Funds to make this transaction.");

		// Transferring the amount
		service.transferFunds(senderAccountNumber, receiverAccountNumber, transferAmount);

		return ResponseEntity.status(HttpStatus.OK).body("Amount Transferred Successfully!!!");
	}
}
