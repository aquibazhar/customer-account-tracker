package com.capstone.accounttracker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "account")
public class Account {
	@Id
	@GeneratedValue
	@Column(name = "accountId")
	private long id;
	@Column(unique =  true)
	private long accountNumber;
	private String accountType;
	private long balance;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customerId")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Customer customer;

	public Account() {
	}

	public Account(long id, long accountNumber, String accountType, long balance, Customer customer) {
		this.id = id;
		this.accountNumber = accountNumber;
		this.accountType = accountType;
		this.balance = balance;
		this.customer = customer;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
