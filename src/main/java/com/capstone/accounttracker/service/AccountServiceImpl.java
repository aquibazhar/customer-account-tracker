package com.capstone.accounttracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository repository;

	@Override
	public Account saveAccount(Account account) {
		return repository.save(account);
	}

	@Override
	public List<Account> getAllAccounts() {
		return repository.findAll();
	}

	@Override
	public Optional<Account> getAccountById(long id) {
		return repository.findById(id);
	}

	@Override
	public Optional<Account> getAccountByAccountNumber(long accountNumber) {
		return repository.findByAccountNumber(accountNumber);
	}

	@Override
	public Account updateAccount(Account account) {
		return repository.save(account);
	}

	@Override
	public void deleteAccountById(long id) {
		repository.deleteById(id);
	}

	@Override
	public void deleteAllAccounts() {
		repository.deleteAll();
	}

	@Override
	public List<Account> getAccountsByCustomerId(long id) {
		return repository.findByCustomerId(id);
	}

	@Override
	public void transferFunds(long senderAccountNumber, long receiverAccountNumber, long transferAmount) {
		Account senderAccount = repository.findByAccountNumber(senderAccountNumber).get();
		Account receiverAccount = repository.findByAccountNumber(receiverAccountNumber).get();

		senderAccount.setBalance(senderAccount.getBalance() - transferAmount);
		receiverAccount.setBalance(receiverAccount.getBalance() + transferAmount);

		repository.save(senderAccount);
		repository.save(receiverAccount);
	}

}
