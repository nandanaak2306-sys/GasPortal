package com.gasbooking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Userrepository extends JpaRepository<user, Long> {
	user findByEmail(String email);
    // This allows you to save and find users without writing SQL!
	List<user> findByStatus(String status);
	// Filter users by their status and the ID of their linked agency object
	// Corrected: Uses _Agency_id to match the 'agency_id' field in gasagency.java
	List<user> findByStatusAndSelectedAgency_AgencyId(String status, String agency_id);
	
}