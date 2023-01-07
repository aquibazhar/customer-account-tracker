package com.capstone.accounttracker.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.model.Customer;

@DataJpaTest
class AccountRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	Account account1;

	Customer customer1;

	@BeforeEach
	void setUp() throws Exception {
		account1 = new Account(1, 1001, "SAVINGS", 100000, null);
		customer1 = new Customer(1, "Sudhanshu", "sudhanshu@gmail.com", null);
	}

	@AfterEach
	void tearDown() throws Exception {
		account1 = null;
		customer1 = null;
	}

	@Test
	void testForSaveAccount() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		accountRepository.save(account1);
		List<Account> accounts = accountRepository.findAll();

		assertNotNull(accounts);
		assertEquals(1, accounts.size());

	}

	@Test
	void testForFindAllAccounts() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		accountRepository.save(account1);
		List<Account> accounts = accountRepository.findAll();

		assertNotNull(accounts);
		assertEquals(1, accounts.size());
	}

	@Test
	void testForFindById() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		Account newAccount = accountRepository.save(account1);
		Optional<Account> accountOptional = accountRepository.findById(newAccount.getId());

		Account returnedAccount = accountOptional.get();

		assertNotNull(returnedAccount);
		assertEquals("SAVINGS", returnedAccount.getAccountType());
		assertEquals(1001, returnedAccount.getAccountNumber());
	}

	@Test
	@DisplayName("Test for findByAccountNumber(long accountNumber) custom method of AccountRepository class.")
	void testForFindByAccountNumber() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		Account newAccount = accountRepository.save(account1);
		Optional<Account> accountOptional = accountRepository.findByAccountNumber(newAccount.getAccountNumber());

		Account returnedAccount = accountOptional.get();

		assertNotNull(returnedAccount);
		assertEquals("SAVINGS", returnedAccount.getAccountType());
		assertEquals(1001, returnedAccount.getAccountNumber());
	}

	@Test
	@DisplayName("Test for findByCustomerId(long customerId) custom method of AccountRepository class.")
	void testForFindByCustomerId() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		Account newAccount = accountRepository.save(account1);
		List<Account> accounts = accountRepository.findByCustomerId(newAccount.getCustomer().getId());

		assertNotNull(accounts);
		assertEquals(1, accounts.size());
	}

	@Test
	void testForUpdateCustomer() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		Account newAccount = accountRepository.save(account1);
		Optional<Account> accountOptional = accountRepository.findById(newAccount.getId());

		Account updatedAccount = accountOptional.get();

		updatedAccount.setAccountNumber(1005);
		updatedAccount.setAccountType("SALARY");

		Account returnedAccount = accountRepository.save(updatedAccount);

		assertNotNull(returnedAccount);
		assertEquals("SALARY", returnedAccount.getAccountType());
		assertEquals(1005, returnedAccount.getAccountNumber());
	}
	
	@Test
	void testForDeleteById() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		Account returnedAccount =  accountRepository.save(account1);
		accountRepository.deleteById(returnedAccount.getId());
		
		List<Account> accounts = accountRepository.findAll();

		assertNotNull(accounts);
		assertEquals(0, accounts.size());
	}
	
	@Test
	void testForDeleteAllAccounts() {
		Customer newCustomer = customerRepository.save(customer1);
		account1.setCustomer(newCustomer);

		accountRepository.save(account1);
		accountRepository.deleteAll();
		
		List<Account> accounts = accountRepository.findAll();

		assertNotNull(accounts);
		assertEquals(0, accounts.size());
	}
}
