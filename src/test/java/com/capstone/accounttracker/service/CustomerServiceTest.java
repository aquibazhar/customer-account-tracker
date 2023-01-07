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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.model.Customer;
import com.capstone.accounttracker.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@InjectMocks
	private CustomerServiceImpl service;

	@Mock
	private CustomerRepository repository;

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
	@DisplayName("Test for saveCustomer(Customer customer) method of CustomerService class.")
	void testForSaveCustomer() {
		when(repository.save(customer1)).thenReturn(customer1);
		Customer returnedCustomer = service.saveCustomer(customer1);
		assertEquals(customer1, returnedCustomer);
		verify(repository, times(1)).save(customer1);
	}

	@Test
	@DisplayName("Test for getAllCustomers() method of CustomerService class.")
	void testForGetAllCustomers() {

		List<Customer> customerList = new ArrayList<>();
		customerList.add(customer1);
		customerList.add(customer2);

		when(repository.findAll()).thenReturn(customerList);

		List<Customer> returnedCustomers = service.getAllCustomers();

		assertEquals(customerList.size(), returnedCustomers.size());

		verify(repository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Test for getCustomerById(long id) method of CustomerService class.")
	void testGetCustomerById() {
		Optional<Customer> customer1Optional = Optional.ofNullable(customer1);
		when(repository.findById((long) 1)).thenReturn(customer1Optional);
		Optional<Customer> returnedCustomer1Optional = service.getCustomerById(1);
		
		assertEquals(customer1Optional.get(), returnedCustomer1Optional.get());
		
		verify(repository, times(1)).findById((long) 1);
	}
	
	@Test
	@DisplayName("Test for getCustomerByEmail(String email) method of CustomerService class.")
	void testGetCustomerByEmail() {
		Optional<Customer> customer2Optional = Optional.ofNullable(customer2);
		
		when(repository.findByEmail("ayush@gmail.com")).thenReturn(customer2Optional);
		
		Optional<Customer> returnedCustomer2Optional = service.getCustomerByEmail("ayush@gmail.com");
		
		assertEquals(customer2Optional.get(), returnedCustomer2Optional.get());
		
		verify(repository, times(1)).findByEmail("ayush@gmail.com");
	}
	
	@Test
	@DisplayName("Test for updateCustomer(Customer customer) method of CustomerService class.")
	void testForUpdateCustomer() {
		Customer newCustomer = new Customer(1, "Sudhanshu Sharma", "sudhanshu@outlook.com", accountList1);
		when(repository.save(newCustomer)).thenReturn(newCustomer);
		Customer returnedCustomer = service.updateCustomer(newCustomer);
		assertEquals(newCustomer, returnedCustomer);
		assertEquals(customer1.getId(), returnedCustomer.getId());
		assertEquals("Sudhanshu Sharma", returnedCustomer.getName());
		verify(repository, times(1)).save(newCustomer);
	}
	
	@Test
	@DisplayName("Test for deletCustomerById(long id) method of CustomerService class.")
	void testForDeletCustomerById() {
		service.deleteCustomerById(2);
		verify(repository, times(1)).deleteById((long) 2);
	}
	
	@Test
	@DisplayName("Test for deleteAllCustomers() method of CustomerService class.")
	void testForDeleteAllCustomers() {
		service.deleteAllCustomers();
		verify(repository, times(1)).deleteAll();
	}
}
