package com.capstone.accounttracker.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

import com.capstone.accounttracker.exception.DuplicateAccountNumberException;
import com.capstone.accounttracker.exception.InsufficientBalanceException;
import com.capstone.accounttracker.exception.ResourceAlreadyExistsException;
import com.capstone.accounttracker.exception.ResourceNotFoundException;
import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.model.Customer;
import com.capstone.accounttracker.model.TransactionDetails;
import com.capstone.accounttracker.service.AccountService;
import com.capstone.accounttracker.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

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

		account1.setCustomer(customer1);
		account2.setCustomer(customer1);
		account3.setCustomer(customer2);

		accountList1.add(account1);
		accountList1.add(account2);

		accountList2.add(account3);

		customer1.setAccounts(accountList1);
		customer2.setAccounts(accountList2);
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
	@DisplayName("Test for createAccount(Account account) method of AccountController class.")
	void testForCreateAccount() throws Exception {
		when(accountService.saveAccount(any(Account.class))).thenReturn(account1);

		String accountJson = this.convertToJson(account1);

		RequestBuilder request = MockMvcRequestBuilders.post("/account").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(accountJson);

		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String returnedAccountJson = mvcResult.getResponse().getContentAsString();

		assertEquals(201, status);
		assertNotNull(returnedAccountJson);
		assertEquals(accountJson, returnedAccountJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for createAccount(Account account) method of AccountController class.")
	void testForCreateAccountAlreadyExists() throws Exception {
		Optional<Account> accountOptional = Optional.ofNullable(account1);
		when(accountService.getAccountByAccountNumber(1001)).thenReturn(accountOptional);

		when(accountService.saveAccount(any(Account.class))).thenReturn(account1);
		String accountJson = this.convertToJson(account1);

		RequestBuilder request = MockMvcRequestBuilders.post("/account").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(accountJson);

		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(409, status);
		assertInstanceOf(ResourceAlreadyExistsException.class, returnedException);
		assertEquals("Account already exists.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for getAllAccounts() method of AccountController class.")
	void testForGetAllAccounts() throws Exception {
		when(accountService.getAllAccounts()).thenReturn(accountList1);
		String accountListJson = this.convertToJson(accountList1);
		
		RequestBuilder request= MockMvcRequestBuilders.get("/account").contentType(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		
		int status = mvcResult.getResponse().getStatus();
		String returnedAccountListJson=mvcResult.getResponse().getContentAsString();
		
		assertEquals(200, status);
		assertNotNull(returnedAccountListJson);
		assertEquals(accountListJson, returnedAccountListJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for getAllAccounts() method of AccountController class.")
	void testForGetAllAccountsEmptyList() throws Exception {
		List<Account> accounts = new ArrayList<Account>();
		when(accountService.getAllAccounts()).thenReturn(accounts);

		RequestBuilder request = MockMvcRequestBuilders.get("/account").accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("There are no accounts in the database.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for getAccountById(long id) method of AccountController class.")
	void testForGetAccountById() throws Exception {
		Optional<Account> account2Optional = Optional.ofNullable(account2);
		when(accountService.getAccountById(2)).thenReturn(account2Optional);
		String account2Json = this.convertToJson(account2);

		RequestBuilder request = MockMvcRequestBuilders.get("/account/{id}", 2).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String returnedAccountJson = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedAccountJson);
		assertEquals(account2Json, returnedAccountJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for getAccountById(long id) method of AccountController class.")
	void testForGetAccountByIdNotFound() throws Exception {
		Optional<Account> account2Optional = Optional.ofNullable(null);
		when(accountService.getAccountById(2)).thenReturn(account2Optional);

		RequestBuilder request = MockMvcRequestBuilders.get("/account/{id}", 2).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Account with this ID doesn't exist.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for getAccountByAccountNumber(String accountNumber) method of AccountController class.")
	void testForGetAccountByAccountNumber() throws Exception {
		Optional<Account> account1Optional = Optional.ofNullable(account1);
		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);
		String account1Json = this.convertToJson(account1);

		RequestBuilder request = MockMvcRequestBuilders.get("/account/accountNumber/{accountNumber}", 1001)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String returnedAccountJson = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedAccountJson);
		assertEquals(account1Json, returnedAccountJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for getAccountByAccountNumber(String accountNumber) method of AccountController class.")
	void testForGetAccountByAccountNumberNotFound() throws Exception {
		Optional<Account> account1Optional = Optional.ofNullable(null);
		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);

		RequestBuilder request = MockMvcRequestBuilders.get("/account/accountNumber/{accountNumber}", 1001)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Account with this Account Number doesn't exist.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for getAccountByCustomerId(long customerId) method of AccountController class.")
	void testForGetAccountsByCustomerId() throws Exception {
		when(accountService.getAccountsByCustomerId(1)).thenReturn(accountList1);
		String accountListJson = this.convertToJson(accountList1);
		
		RequestBuilder request=MockMvcRequestBuilders.get("/account/customer/{customerId}", 1).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		
		int status = mvcResult.getResponse().getStatus();
		String returnedAccountListJson = mvcResult.getResponse().getContentAsString();
		
		assertEquals(200, status);
		assertNotNull(returnedAccountListJson);
		assertEquals(accountListJson, returnedAccountListJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for getAccountByCustomerId(long customerId) method of AccountController class.")
	void testForGetAccountsByCustomerIdNotFound() throws Exception {
		List<Account> accounts = new ArrayList<Account>();
		when(accountService.getAllAccounts()).thenReturn(accounts);

		RequestBuilder request = MockMvcRequestBuilders.get("/account/customer/{customerId}", 1)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("No accounts exist for this Customer.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for updateAccount(long id, Account account) method of AccountController class.")
	void testForUpdateAccount() throws Exception {
		Optional<Account> account3Optional = Optional.ofNullable(account3);
		when(accountService.getAccountById(3)).thenReturn(account3Optional);

		account3.setAccountNumber(1005);
		account3.setBalance(450000);

		String updatedAccount3Json = this.convertToJson(account3);

		when(accountService.updateAccount(any(Account.class))).thenReturn(account3);

		RequestBuilder request = MockMvcRequestBuilders.put("/account/{id}", 3).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(updatedAccount3Json);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String returnedAccountJson = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedAccountJson);
		assertEquals(updatedAccount3Json, returnedAccountJson);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for updateAccount(long id, Account account) method of AccountController class.")
	void testForUpdateAccountNotFound() throws Exception {
		Optional<Account> account3Optional = Optional.ofNullable(null);
		when(accountService.getAccountById(3)).thenReturn(account3Optional);

		account3.setAccountNumber(1005);
		account3.setBalance(450000);

		String updatedAccount3Json = this.convertToJson(account3);

		when(accountService.updateAccount(any(Account.class))).thenReturn(account3);

		RequestBuilder request = MockMvcRequestBuilders.put("/account/{id}", 3).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(updatedAccount3Json);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Account with this ID doesn't exist.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for deleteAccountById(long id) method of AccountController class.")
	void testForDeleteAccountById() throws Exception {
		Optional<Account> accountOptional = Optional.ofNullable(account1);
		when(accountService.getAccountById(1)).thenReturn(accountOptional);

		doNothing().when(accountService).deleteAccountById(1);

		RequestBuilder request = MockMvcRequestBuilders.delete("/account/{id}", 1).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();

		assertEquals(200, status);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for deleteAccountById(long id) method of AccountController class.")
	void testForDeleteAccountByIdNotFound() throws Exception {
		Optional<Account> accountOptional = Optional.ofNullable(null);
		when(accountService.getAccountById(1)).thenReturn(accountOptional);

		doNothing().when(accountService).deleteAccountById(1);

		RequestBuilder request = MockMvcRequestBuilders.delete("/account/{id}", 1).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("Account with this ID doesn't exist.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for deleteAllAccounts() method of AccountController class.")
	void testForDeleteAllAccounts() throws Exception {
		when(accountService.getAllAccounts()).thenReturn(accountList1);
		
		doNothing().when(accountService).deleteAllAccounts();
		
		RequestBuilder request = MockMvcRequestBuilders.delete("/account").accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(request).andReturn();
		
		int status = mvcResult.getResponse().getStatus();

		assertEquals(200, status);
	}

	// NEGATIVE TEST
	@Test
	@DisplayName("Negative Test for deleteAllAccounts() method of AccountController class.")
	void testForDeleteAllAccountsEmptyList() throws Exception {
		List<Account> accounts = new ArrayList<Account>();
		when(accountService.getAllAccounts()).thenReturn(accounts);

		doNothing().when(accountService).deleteAllAccounts();

		RequestBuilder request = MockMvcRequestBuilders.delete("/account").accept(MediaType.APPLICATION_JSON);

		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception returnedException = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, returnedException);
		assertEquals("There are no accounts in the database.", returnedException.getMessage());
	}

	@Test
	@DisplayName("Test for transferFunds(TransactionDetails transactionDetails) method of AccountController class.")
	void testForTransferFunds() throws Exception {
		Optional<Account> account1Optional = Optional.ofNullable(account1);
		Optional<Account> account2Optional = Optional.ofNullable(account2);

		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);
		when(accountService.getAccountByAccountNumber(1002)).thenReturn(account2Optional);

		doNothing().when(accountService).transferFunds(1001, 1002, 10000);

		TransactionDetails transactionDetails = new TransactionDetails(1001, 1002, 10000);

		String transactionDetailsJson = this.convertToJson(transactionDetails);

		RequestBuilder request = MockMvcRequestBuilders.post("/transfer").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(transactionDetailsJson);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		String returnedMessage = mvcResult.getResponse().getContentAsString();

		assertEquals(200, status);
		assertNotNull(returnedMessage);
		assertEquals("Amount Transferred Successfully!!!", returnedMessage);

	}

	// DUPLICATE ACCOUNT
	@Test
	@DisplayName("Test for transferFunds(TransactionDetails transactionDetails) method of AccountController class when duplicate account numbers are provided.")
	void testForTransferFundsDuplicateAccount() throws Exception {
		Optional<Account> account1Optional = Optional.ofNullable(account1);

		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);
		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);

		doNothing().when(accountService).transferFunds(1001, 1001, 10000);

		TransactionDetails transactionDetails = new TransactionDetails(1001, 1001, 10000);

		String transactionDetailsJson = this.convertToJson(transactionDetails);

		RequestBuilder request = MockMvcRequestBuilders.post("/transfer").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(transactionDetailsJson);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception exception = mvcResult.getResolvedException();

		assertEquals(409, status);
		assertInstanceOf(DuplicateAccountNumberException.class, exception);
		assertEquals("Sender's and Receiver's Account Number cannot be same.", exception.getMessage());
	}

	// SENDER ACCOUNT NUMBER INVALID
	@Test
	@DisplayName("Negative Test for transferFunds(TransactionDetails transactionDetails) method of AccountController class when sender account number is invalid.")
	void testForTransferFundsInvalidSenderAccount() throws Exception {
		Optional<Account> account1Optional = Optional.ofNullable(null);
		Optional<Account> account2Optional = Optional.ofNullable(account2);

		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);
		when(accountService.getAccountByAccountNumber(1002)).thenReturn(account2Optional);

		doNothing().when(accountService).transferFunds(1001, 1002, 10000);

		TransactionDetails transactionDetails = new TransactionDetails(1001, 1002, 10000);

		String transactionDetailsJson = this.convertToJson(transactionDetails);

		RequestBuilder request = MockMvcRequestBuilders.post("/transfer").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(transactionDetailsJson);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception exception = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, exception);
		assertEquals("Provided Sender Account Number is Invalid.", exception.getMessage());

	}

	// RECEIVER ACCOUNT NUMBER INVALID
	@Test
	@DisplayName("Negative Test for transferFunds(TransactionDetails transactionDetails) method of AccountController class when receiver account number is invalid.")
	void testForTransferFundsInvalidReceiverAccount() throws Exception {
		Optional<Account> account1Optional = Optional.ofNullable(account1);
		Optional<Account> account2Optional = Optional.ofNullable(null);

		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);
		when(accountService.getAccountByAccountNumber(1002)).thenReturn(account2Optional);

		doNothing().when(accountService).transferFunds(1001, 1002, 10000);

		TransactionDetails transactionDetails = new TransactionDetails(1001, 1002, 10000);

		String transactionDetailsJson = this.convertToJson(transactionDetails);

		RequestBuilder request = MockMvcRequestBuilders.post("/transfer").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(transactionDetailsJson);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception exception = mvcResult.getResolvedException();

		assertEquals(404, status);
		assertInstanceOf(ResourceNotFoundException.class, exception);
		assertEquals("Provided Receiver Account Number is Invalid.", exception.getMessage());

	}

	// INSUFFICIENT BALANCE
	@Test
	@DisplayName("Test for transferFunds(TransactionDetails transactionDetails) method of AccountController class when sender has insufficient balance.")
	void testForTransferFundsInsufficientBalance() throws Exception {
		Optional<Account> account1Optional = Optional.ofNullable(account1);
		Optional<Account> account2Optional = Optional.ofNullable(account2);

		when(accountService.getAccountByAccountNumber(1001)).thenReturn(account1Optional);
		when(accountService.getAccountByAccountNumber(1002)).thenReturn(account2Optional);

		doNothing().when(accountService).transferFunds(1001, 1002, 150000);

		TransactionDetails transactionDetails = new TransactionDetails(1001, 1002, 150000);

		String transactionDetailsJson = this.convertToJson(transactionDetails);

		RequestBuilder request = MockMvcRequestBuilders.post("/transfer").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(transactionDetailsJson);
		MvcResult mvcResult = mockMvc.perform(request).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Exception exception = mvcResult.getResolvedException();

		assertEquals(400, status); // BAD REQUEST
		assertInstanceOf(InsufficientBalanceException.class, exception);
		assertEquals("Sender Account does not have Sufficient Funds to make this transaction.", exception.getMessage());

	}

	private String convertToJson(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}

}
