package com.capstone.accounttracker.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	Account account1;

	Customer customer1;

	List<Account> accountList1 = new ArrayList<Account>();

	@BeforeEach
	void setUp() throws Exception {
		account1 = new Account(1, 1001, "SAVINGS", 100000, null);
		customer1 = new Customer(1, "Sudhanshu", "sudhanshu@gmail.com", null);

		accountList1.add(account1);

		customer1.setAccounts(accountList1);

		account1.setCustomer(customer1);
	}

	@AfterEach
	void tearDown() throws Exception {
		account1 = null;
		customer1 = null;
		accountList1 = null;
	}

	@Test
	void testForSaveCustomer() {
		customerRepository.save(customer1);
		List<Customer> customers = customerRepository.findAll();

		assertNotNull(customers);
		assertEquals(1, customers.size());

	}

	@Test
	void testForFindAllCustomers() {
		customerRepository.save(customer1);
		List<Customer> customers = customerRepository.findAll();

		assertNotNull(customers);
		assertEquals(1, customers.size());

	}

	@Test
	void testForFindById() {
		Customer newCustomer = customerRepository.save(customer1);
		Optional<Customer> customerOptional = customerRepository.findById(newCustomer.getId());

		Customer returnedCustomer = customerOptional.get();

		assertNotNull(returnedCustomer);
		assertEquals("Sudhanshu", returnedCustomer.getName());
		assertEquals(1, returnedCustomer.getAccounts().size());

	}

	@Test
	@DisplayName("Test for findByEmail(String email) custom method of CustomerRepository class.")
	void testForFindByEmail() {
		customerRepository.save(customer1);
		Optional<Customer> customerOptional = customerRepository.findByEmail("sudhanshu@gmail.com");

		Customer returnedCustomer = customerOptional.get();

		assertNotNull(returnedCustomer);
		assertEquals("Sudhanshu", returnedCustomer.getName());
		assertEquals(1, returnedCustomer.getAccounts().size());

	}

	@Test
	void testForUpdateCustomer() {
		Customer customer = customerRepository.save(customer1);
		Optional<Customer> customerOptional = customerRepository.findById(customer.getId());

		Customer updatedCustomer = customerOptional.get();
		updatedCustomer.setName("Sharma");
		updatedCustomer.setEmail("sharma@outlook.com");

		Customer returnedCustomer = customerRepository.save(updatedCustomer);

		assertNotNull(returnedCustomer);
		assertEquals(customer.getId(), returnedCustomer.getId());
		assertEquals("Sharma", returnedCustomer.getName());
		assertEquals("sharma@outlook.com", returnedCustomer.getEmail());

	}

	@Test
	void testForDeleteById() {
		Customer newCustomer = customerRepository.save(customer1);

		customerRepository.deleteById(newCustomer.getId());

		List<Customer> customers = customerRepository.findAll();

		assertEquals(0, customers.size());

	}

	@Test
	void testForDeleteAllCustomers() {
		customerRepository.save(customer1);

		customerRepository.deleteAll();

		List<Customer> customers = customerRepository.findAll();

		assertEquals(0, customers.size());

	}
}
