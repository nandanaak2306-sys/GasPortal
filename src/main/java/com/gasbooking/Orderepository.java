package com.gasbooking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Orderepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(Long userId);

    @Query("SELECT o FROM Orders o WHERE o.agency_id = :agencyId AND o.delivery_status = :delivery_status")
    List<Orders> findByAgencyIdAndDelivery_status(@Param("agencyId") String agencyId,
                                                  @Param("delivery_status") String delivery_status);
    
    @Query("SELECT o FROM Orders o WHERE o.supplier_id = :supplierId")
    List<Orders> findBySupplierId(@Param("supplierId") Long supplierId);
}