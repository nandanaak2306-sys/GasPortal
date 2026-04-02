package com.gasbooking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Supplierrepository extends JpaRepository<Supplier, Integer> {

	<S extends Supplier> S save(S s);

	boolean existsByEmail(String email);

	List<Supplier> findByAgency_AgencyId(String agencyId);
	Supplier findByEmailAndPassword(String email, String password);

	
}