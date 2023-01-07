package com.capstone.accounttracker.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.capstone.accounttracker.exception.DuplicateAccountTypeException;
import com.capstone.accounttracker.exception.ResourceAlreadyExistsException;
import com.capstone.accounttracker.exception.ResourceNotFoundException;
import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.model.Customer;
import com.capstone.accounttracker.service.AccountService;
import com.capstone.accounttracker.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	private CustomerService customerService;

	@MockBean
	private AccountService accountService;

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
	@DisplayName("Test for createCustomer(Customer customer) method of CustomerController class.")
	void testForCreateCustomer() throws Exception {
		when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer1);
		String customerJson = this.convertToJson(customer1);
		
		RequestBuilder request = MockMvcRequestBuilders.post("/customer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(customerJson);
		
		MvcResult mvcResult= mockMvc.perform(request).andReturn();
		
		int status = mvcResult.getResponse().getStatus();
		String returnedCustomerJson = mvcResult.getResponse().getContentAsString();
		assertEquals(201, status);
		assertNotNull(returnedCustomerJson);
		assertEquals(customerJson, returnedCustomerJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for createCustomer(Customer customer) method of CustomerController class.")
	void testForCreateCustomerAlreadyExists() throws Exception {
		Optional<Customer> customer1Optional = Optional.ofNullable(customer1);
		when(customerService.getCustomerByEmail(anyString())).thenReturn(customer1Optional);

		when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer1);
		String customerJson = this.convertToJson(customer1);

		RequestBuilder request = MockMvcRequestBuilders.post("/customer").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(customerJson);

		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();
		assertEquals(409, status);
		assertInstanceOf(ResourceAlreadyExistsException.class, returnedException);
		assertEquals("Customer already exists.", returnedException.getMessage());
	}
	
	// NEGATIVE TEST
		@Test
		@DisplayName("Negative Test for createCustomer(Customer customer) method of CustomerController class.")
		void testForCreateCustomerDuplicateAccountType() throws Exception {
			Customer customer = new Customer(1, "Aquib", "aquib@gmail.com", null);
			List<Account> accountList = new ArrayList<Account>();
			accountList.add(account1);
			accountList.add(account1);
			customer.setAccounts(accountList);

			when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);
			String customerJson = this.convertToJson(customer);

			RequestBuilder request = MockMvcRequestBuilders.post("/customer").accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON).content(customerJson);

			MvcResult mvcResult = mockMvc.perform(request).andReturn();

			int status = mvcResult.getResponse().getStatus();
			Exception returnedException = mvcResult.getResolvedException();
			assertEquals(409, status);
			assertInstanceOf(DuplicateAccountTypeException.class, returnedException);
			assertEquals("A Customer cannot have more than one account of the same type.", returnedException.getMessage());
		}

	@Test
	@DisplayName("Test for getAllCustomers() method of CustomerController class.")
	void testForGetAllCustomers() throws Exception {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customer1);
		customerList.add(customer2);

		when(customerService.getAllCustomers()).thenReturn(customerList);
		String customerListJson = this.convertToJson(customerList);

		RequestBuilder request = MockMvcRequestBuilders.get("/customer").accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String returnedCustomerlistJson = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedCustomerlistJson);
		assertEquals(customerListJson, returnedCustomerlistJson);

	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for getAllCustomers() method of CustomerController class.")
	void testForGetAllCustomersEmptyList() throws Exception {
		List<Customer> customerList = new ArrayList<>();

		when(customerService.getAllCustomers()).thenReturn(customerList);

		RequestBuilder request = MockMvcRequestBuilders.get("/customer").accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("There are no customers in the database.", returnedException.getMessage());

	}

	@Test
	@DisplayName("Test for getCustomerById(long id) method of CustomerController class.")
	void testForGetCustomerById() throws Exception {
		Optional<Customer> customer2Optional = Optional.ofNullable(customer2);
		when(customerService.getCustomerById(anyLong())).thenReturn(customer2Optional);
		String customer2Json = this.convertToJson(customer2Optional.get());

		RequestBuilder request = MockMvcRequestBuilders.get("/customer/{id}", 2).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String returnedCustomerJson = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedCustomerJson);
		assertEquals(customer2Json, returnedCustomerJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for getCustomerById(long id) method of CustomerController class.")
	void testForGetCustomerByIdNotFound() throws Exception {
		Optional<Customer> customer2Optional = Optional.ofNullable(null);
		when(customerService.getCustomerById(anyLong())).thenReturn(customer2Optional);

		RequestBuilder request = MockMvcRequestBuilders.get("/customer/{id}", 2).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Customer with this ID doesn't exist.", returnedException.getMessage());

	}

	@Test
	@DisplayName("Test for getCustomerByEmail(String email) method of CustomerController class.")
	void testForGetCustomerByEmail() throws Exception {
		Optional<Customer> customer1Optional = Optional.ofNullable(customer1);
		when(customerService.getCustomerByEmail("sudhanshu@gmail.com")).thenReturn(customer1Optional);

		String customer1Json = this.convertToJson(customer1Optional.get());

		RequestBuilder request = MockMvcRequestBuilders.get("/customer/email/{email}", "sudhanshu@gmail.com")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String returnedCustomerJson = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedCustomerJson);
		assertEquals(customer1Json, returnedCustomerJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for getCustomerByEmail(String email) method of CustomerController class.")
	void testForGetCustomerByEmailNotFound() throws Exception {
		Optional<Customer> customer2Optional = Optional.ofNullable(null);
		when(customerService.getCustomerByEmail(anyString())).thenReturn(customer2Optional);

		RequestBuilder request = MockMvcRequestBuilders.get("/customer/email/{email}", "sudhanshu@gmail.com")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Customer with this Email doesn't exist.", returnedException.getMessage());

	}

	@Test
	@DisplayName("Test for updateCustomer(long id, Customer customer) method of CustomerController class.")
	void testForUpdateCustomer() throws Exception {
		Optional<Customer> customer1Optional = Optional.ofNullable(customer1);
		when(customerService.getCustomerById(1)).thenReturn(customer1Optional);

		customer1.setEmail("sudhanshu@outlook.com");
		customer1.setName("Sudhanshu Sharma");
		when(customerService.updateCustomer(any(Customer.class))).thenReturn(customer1);

		String updatedCustomerJson = this.convertToJson(customer1);

		RequestBuilder request = MockMvcRequestBuilders.put("/customer/{id}", 1).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(updatedCustomerJson);

		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String returnedCustomerJson = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedCustomerJson);
		assertEquals(updatedCustomerJson, returnedCustomerJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for updateCustomer(long id, Customer customer) method of CustomerController class.")
	void testForUpdateCustomerNotFound() throws Exception {
		Optional<Customer> customer1Optional = Optional.ofNullable(null);
		when(customerService.getCustomerById(1)).thenReturn(customer1Optional);

		customer1.setEmail("sudhanshu@outlook.com");
		customer1.setName("Sudhanshu Sharma");
		when(customerService.updateCustomer(any(Customer.class))).thenReturn(customer1);

		String updatedCustomerJson = this.convertToJson(customer1);

		RequestBuilder request = MockMvcRequestBuilders.put("/customer/{id}", 1).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(updatedCustomerJson);

		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Customer with this ID doesn't exist.", returnedException.getMessage());

	}
	
	// NEGATIVE TEST
		@Test
		@DisplayName("Negative Test for updateCustomer(long id, Customer customer) method of CustomerController class.")
		void testForUpdateCustomerDuplicateAccountType() throws Exception {
			
			
			Customer customer = new Customer(1, "Aquib", "aquib@gmail.com", null);
			List<Account> accountList = new ArrayList<Account>();
			accountList.add(account1);
			accountList.add(account1);
			customer.setAccounts(accountList);
			
			Optional<Customer> customerOptional = Optional.ofNullable(customer);
			when(customerService.getCustomerById(1)).thenReturn(customerOptional);
			
			

			customer.setEmail("sudhanshu@outlook.com");
			customer.setName("Sudhanshu Sharma");
			when(customerService.updateCustomer(any(Customer.class))).thenReturn(customer);

			String updatedCustomerJson = this.convertToJson(customer);

			RequestBuilder request = MockMvcRequestBuilders.put("/customer/{id}", 1).accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON).content(updatedCustomerJson);

			MvcResult mvcResult = mockMvc.perform(request).andReturn();

			int status = mvcResult.getResponse().getStatus();
			Exception returnedException = mvcResult.getResolvedException();

			assertEquals(409, status);
			assertInstanceOf(DuplicateAccountTypeException.class, returnedException);
			assertEquals("A Customer cannot have more than one account of the same type.", returnedException.getMessage());

		}

	@Test
	@DisplayName("Test for deleteCustomerById(long id) method of CustomerController class.")
	void testForDeleteCustomerById() throws Exception {
		Optional<Customer> customer1Optional = Optional.ofNullable(customer1);
		when(customerService.getCustomerById(1)).thenReturn(customer1Optional);
		
		doNothing().when(customerService).deleteCustomerById(0);

		RequestBuilder request = MockMvcRequestBuilders.delete("/customer/{id}", 1).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for deleteCustomerById(long id) method of CustomerController class.")
	void testForDeleteCustomerByIdNotFound() throws Exception {
		Optional<Customer> customer1Optional = Optional.ofNullable(null);
		when(customerService.getCustomerById(1)).thenReturn(customer1Optional);
		
		doNothing().when(customerService).deleteCustomerById(0);

		RequestBuilder request = MockMvcRequestBuilders.delete("/customer/{id}", 1).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();

		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Customer with this ID doesn't exist.", returnedException.getMessage());

	}

	@Test
	@DisplayName("Test for deleteAllCustomers() method of CustomerController class.")
	void testForDeleteAllCustomers() throws Exception {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customer1);
		customerList.add(customer2);

		when(customerService.getAllCustomers()).thenReturn(customerList);
		
		doNothing().when(customerService).deleteAllCustomers();

		RequestBuilder request = MockMvcRequestBuilders.delete("/customer").accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();

		assertEquals(200, status);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for deleteAllCustomers() method of CustomerController class.")
	void testForDeleteAllCustomersEmptyList() throws Exception {
		List<Customer> customerList = new ArrayList<>();

		when(customerService.getAllCustomers()).thenReturn(customerList);
		
		doNothing().when(customerService).deleteAllCustomers();

		RequestBuilder request = MockMvcRequestBuilders.delete("/customer").accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("There are no customers in the database.", returnedException.getMessage());
	}

	private String convertToJson(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}

}
