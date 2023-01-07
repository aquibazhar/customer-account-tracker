package com.capstone.accounttracker.service;

import java.util.List;
import java.util.Optional;

import com.capstone.accounttracker.model.Customer;

public interface CustomerService {
	public Customer saveCustomer(Customer customer);

	public List<Customer> getAllCustomers();

	public Optional<Customer> getCustomerById(long id);

	public Optional<Customer> getCustomerByEmail(String email);

	public Customer updateCustomer(Customer customer);

	public void deleteCustomerById(long id);

	public void deleteAllCustomers();
}

