package com.gasbooking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface Stockrepository extends JpaRepository<Stocks, Long> {
    // Crucial: Finds the specific stock row for a given agency
	Optional<Stocks> findByAgency(gasagency agency);
	
}