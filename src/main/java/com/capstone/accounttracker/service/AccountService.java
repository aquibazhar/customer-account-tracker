package com.capstone.accounttracker.service;

import java.util.List;
import java.util.Optional;

import com.capstone.accounttracker.model.Account;

public interface AccountService {
	public Account saveAccount(Account account);

	public List<Account> getAllAccounts();

	public Optional<Account> getAccountById(long id);
	
	public List<Account> getAccountsByCustomerId(long id);

	public Optional<Account> getAccountByAccountNumber(long accountNumber);

	public Account updateAccount(Account account);

	public void deleteAccountById(long id);

	public void deleteAllAccounts();
	
	public void transferFunds(long senderAccountNumber, long receiverAccountNumber, long transferAmount);
}
