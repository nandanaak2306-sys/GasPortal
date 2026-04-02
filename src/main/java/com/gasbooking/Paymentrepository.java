package com.gasbooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
@Repository

public interface Paymentrepository extends JpaRepository<Payment, Long>{
	
	
	List<Payment> findByOrders_UserId(Long userId);

	Payment findByOrders(Orders orders);
}
