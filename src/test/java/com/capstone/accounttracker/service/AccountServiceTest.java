package com.capstone.accounttracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.model.Customer;
import com.capstone.accounttracker.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@InjectMocks
	private AccountServiceImpl service;

	@Mock
	private AccountRepository repository;

	Account account1;
	Account account2;
	Account account3;

	Customer customer1;
	Customer customer2;

	List<Account> accountList1 = new ArrayList<Account>();
	List<Account> accountList2 = new ArrayList<Account>();;

	@BeforeEach
	void setUp() throws Exception {
		account1 = new Account(1, 1001, "SAVINGS", 100000, null);
		account2 = new Account(2, 1002, "CURRENT", 200000, null);
		account3 = new Account(3, 1003, "SALARY", 300000, null);
		customer1 = new Customer(1, "Sudhanshu", "sudhanshu@gmail.com", null);
		customer2 = new Customer(2, "Ayush", "ayush@gmail.com", null);

		accountList1.add(account1);
		accountList1.add(account2);

		customer1.setAccounts(accountList1);

		account1.setCustomer(customer1);
		account2.setCustomer(customer1);

		accountList2.add(account3);

		customer2.setAccounts(accountList2);

		account3.setCustomer(customer2);
	}

	@AfterEach
	void tearDown() throws Exception {
		account1 = null;
		account2 = null;
		account3 = null;
		customer1 = null;
		customer2 = null;
		accountList1 = new ArrayList<Account>();
		accountList2 = new ArrayList<Account>();
	}

	@Test
	@DisplayName("Test for saveAccount(Account account) method of AccountService class.")
	void testForSaveAccount() {
		when(repository.save(account1)).thenReturn(account1);
		Account returnedAccount = service.saveAccount(account1);
		assertEquals(account1, returnedAccount);
		verify(repository, times(1)).save(account1);
	}

	@Test
	@DisplayName("Test for getAllAccounts() method of AccountService class.")
	void testForGetAllAccounts() {
		List<Account> accountList = new ArrayList<>();
		accountList.add(account1);
		accountList.add(account2);
		accountList.add(account3);

		when(repository.findAll()).thenReturn(accountList);

		List<Account> returnedAccounts = service.getAllAccounts();

		assertEquals(accountList.size(), returnedAccounts.size());

		verify(repository, times(1)).findAll();
	}

	@Test
	@DisplayName("Test for getAccountById(long id) method of AccountService class.")
	void testForGetAccountById() {
		Optional<Account> account1Optional = Optional.ofNullable(account1);
		when(repository.findById((long) 1)).thenReturn(account1Optional);
		Optional<Account> returnedAccount1Optional = service.getAccountById(1);

		assertEquals(account1Optional.get(), returnedAccount1Optional.get());

		verify(repository, times(1)).findById((long) 1);
	}

	@Test
	@DisplayName("Test for getAccountByAccountNumber(String accountNumber) method of AccountService class.")
	void testForGetAccountByAccountNumber() {
		Optional<Account> account2Optional = Optional.ofNullable(account2);

		when(repository.findByAccountNumber((long) 1002)).thenReturn(account2Optional);

		Optional<Account> returnedAccount2Optional = service.getAccountByAccountNumber(1002);

		assertEquals(account2Optional.get(), returnedAccount2Optional.get());

		verify(repository, times(1)).findByAccountNumber(1002);
	}

	@Test
	@DisplayName("Test for getAccountsByCustomerId(long id) method of AccountService class.")
	void testForGetAccountsByCustomerId() {
		List<Account> accountList = new ArrayList<>();
		accountList.add(account1);
		accountList.add(account2);

		when(repository.findByCustomerId(1)).thenReturn(accountList);
		
		List<Account> returnedAccounts = service.getAccountsByCustomerId(1);
		
		assertEquals(2, returnedAccounts.size());
		
		verify(repository, times(1)).findByCustomerId(1);
	}

	@Test
	@DisplayName("Test for updateAccount(Account account) method of AccountService class.")
	void testForUpdateAccount() {
		Account newAccount = new Account(3, 1005, "SALARY", 50000, customer2);
		when(repository.save(newAccount)).thenReturn(newAccount);
		
		Account returnedAccount = service.updateAccount(newAccount);
		
		assertEquals(newAccount, returnedAccount);
		assertEquals(1005, returnedAccount.getAccountNumber());
		assertEquals(50000, returnedAccount.getBalance());
		verify(repository, times(1)).save(newAccount);
	}

	@Test
	@DisplayName("Test for deleteAccountById(long id) method of AccountService class.")
	void testForDeleteAccountById() {
		service.deleteAccountById(1);
		verify(repository, times(1)).deleteById((long) 1);
	}

	@Test
	@DisplayName("Test for deleteAllAccounts() method of AccountService class.")
	void testForDeleteAllAccounts() {
		service.deleteAllAccounts();
		verify(repository, times(1)).deleteAll();
	}

	@Test
	@DisplayName("Test for transferFunds() method of AccountService class.")
	void testForTransferFunds() {
		Optional<Account> account1Optional = Optional.ofNullable(account1);
		when(repository.findByAccountNumber(1001)).thenReturn(account1Optional);
		
		Optional<Account> account2Optional = Optional.ofNullable(account2);
		when(repository.findByAccountNumber(1002)).thenReturn(account2Optional);
		
		service.transferFunds(1001, 1002, 10000);
		
		assertEquals(account1.getBalance(), 90000);
		
		assertEquals(account2.getBalance(), 210000);
		
		verify(repository, times(2)).findByAccountNumber(ArgumentMatchers.anyLong());
		
		verify(repository, times(2)).save(ArgumentMatchers.any(Account.class));
	}

}
