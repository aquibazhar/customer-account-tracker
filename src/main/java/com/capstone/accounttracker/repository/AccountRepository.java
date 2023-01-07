package com.capstone.accounttracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstone.accounttracker.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByAccountNumber(long accountNumber);
	List<Account> findByCustomerId(long id);
}
