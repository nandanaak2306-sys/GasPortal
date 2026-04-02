package com.gasbooking;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface Gasagencyrepository extends JpaRepository<gasagency, String> {
    // Spring will automatically map this to the 'locality' field in your database
    List<gasagency> findByLocality(String locality);
}