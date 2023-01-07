package com.capstone.accounttracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.accounttracker.model.Customer;
import com.capstone.accounttracker.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository repository;

	@Override
	public Customer saveCustomer(Customer customer) {
		return repository.save(customer);
	}

	@Override
	public List<Customer> getAllCustomers() {
		return repository.findAll();
	}

	@Override
	public Optional<Customer> getCustomerById(long id) {
		return repository.findById(id);
	}

	@Override
	public Optional<Customer> getCustomerByEmail(String email) {
		return repository.findByEmail(email);
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		return repository.save(customer);
	}

	@Override
	public void deleteCustomerById(long id) {
		repository.deleteById(id);
	}

	@Override
	public void deleteAllCustomers() {
		repository.deleteAll();
	}

}
