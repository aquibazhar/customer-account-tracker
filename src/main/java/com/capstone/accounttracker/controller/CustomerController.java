package com.capstone.accounttracker.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.capstone.accounttracker.exception.DuplicateAccountTypeException;
import com.capstone.accounttracker.exception.ResourceAlreadyExistsException;
import com.capstone.accounttracker.exception.ResourceNotFoundException;
import com.capstone.accounttracker.model.Account;
import com.capstone.accounttracker.model.Customer;
import com.capstone.accounttracker.service.CustomerService;

@RestController
public class CustomerController {

	@Autowired
	private CustomerService service;

	@PostMapping("/customer")
	public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer)
			throws ResourceAlreadyExistsException, DuplicateAccountTypeException, DuplicateAccountNumberException {
		Optional<Customer> customerOptional = service.getCustomerByEmail(customer.getEmail());
		if (customerOptional.isPresent()) {
			throw new ResourceAlreadyExistsException("Customer already exists.");
		}
		if (customer.getAccounts().size() > 1) {
			Set<String> accountTypes = new HashSet<>();
			for (Account account : customer.getAccounts()) {
				accountTypes.add(account.getAccountType());
			}
			if (accountTypes.size() != customer.getAccounts().size()) {
				throw new DuplicateAccountTypeException(
						"A Customer cannot have more than one account of the same type.");
			}

			Set<Long> accountNumbers = new HashSet<>();
			for (Account account : customer.getAccounts()) {
				accountNumbers.add(account.getAccountNumber());
			}
			if (accountNumbers.size() != customer.getAccounts().size()) {
				throw new DuplicateAccountNumberException("Multiple accounts cannot have same Account Number.");
			}
		}
		Customer savedCustomer = service.saveCustomer(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
	}

	@GetMapping("/customer")
	public ResponseEntity<List<Customer>> getAllCustomers() throws ResourceNotFoundException {
		List<Customer> customerList = service.getAllCustomers();
		if (customerList.isEmpty()) {
			throw new ResourceNotFoundException("There are no customers in the database.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(customerList);
	}

	@GetMapping("/customer/{id}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable long id) throws ResourceNotFoundException {
		Optional<Customer> customerOptional = service.getCustomerById(id);
		if (customerOptional.isEmpty()) {
			throw new ResourceNotFoundException("Customer with this ID doesn't exist.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(customerOptional.get());
	}

	@GetMapping("/customer/email/{email}")
	public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) throws ResourceNotFoundException {
		Optional<Customer> customerOptional = service.getCustomerByEmail(email);
		if (customerOptional.isEmpty()) {
			throw new ResourceNotFoundException("Customer with this Email doesn't exist.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(customerOptional.get());
	}

	@PutMapping("/customer/{id}")
	public ResponseEntity<Customer> updateCustomer(@PathVariable long id, @RequestBody Customer customer)
			throws ResourceNotFoundException, DuplicateAccountTypeException, DuplicateAccountNumberException {
		Optional<Customer> customerOptional = service.getCustomerById(id);
		if (customerOptional.isEmpty()) {
			throw new ResourceNotFoundException("Customer with this ID doesn't exist.");
		}

		if (customer.getAccounts().size() > 1) {
			Set<String> accountTypes = new HashSet<>();
			for (Account account : customer.getAccounts()) {
				accountTypes.add(account.getAccountType());
			}
			if (accountTypes.size() != customer.getAccounts().size()) {
				throw new DuplicateAccountTypeException(
						"A Customer cannot have more than one account of the same type.");
			}
			Set<Long> accountNumbers = new HashSet<>();
			for (Account account : customer.getAccounts()) {
				accountNumbers.add(account.getAccountNumber());
			}
			if (accountNumbers.size() != customer.getAccounts().size()) {
				throw new DuplicateAccountNumberException("Multiple accounts cannot have same Account Number.");
			}
		}

		Customer updatedCustomer = service.updateCustomer(customer);
		return ResponseEntity.status(HttpStatus.OK).body(updatedCustomer);
	}

	@DeleteMapping("/customer/{id}")
	public ResponseEntity<Customer> deleteCustomerById(@PathVariable long id) throws ResourceNotFoundException {
		Optional<Customer> customerOptional = service.getCustomerById(id);
		if (customerOptional.isEmpty()) {
			throw new ResourceNotFoundException("Customer with this ID doesn't exist.");
		}
		service.deleteCustomerById(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/customer")
	public ResponseEntity<Customer> deleteAllCustomers() throws ResourceNotFoundException {
		List<Customer> customerList = service.getAllCustomers();
		if (customerList.isEmpty()) {
			throw new ResourceNotFoundException("There are no customers in the database.");
		}
		service.deleteAllCustomers();
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
