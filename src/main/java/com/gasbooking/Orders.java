package com.gasbooking;

import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long booking_id;
   
    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Payment payment;

    // getter
    public Payment getPayment() { return payment; }
   

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	private Long userId;
    private String agency_id;

    private Long supplier_id;
    public Integer quantity;
       
   
	private LocalDate booking_date;

    private String status;

    private String connection_id;
    private String cylinder_type;
    private String delivery_status;
    // Getters and Setters

    public String getCylinder_type() {
		return cylinder_type;
	}

	public void setCylinder_type(String cylinder_type) {
		this.cylinder_type = cylinder_type;
	}

	public String getDelivery_status() {
		return delivery_status;
	}

	public void setDelivery_status(String delivery_status) {
		this.delivery_status = delivery_status;
	}

	public Long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

   

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public Long getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Long supplier_id) {
        this.supplier_id = supplier_id;
    }

    public LocalDate getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(LocalDate booking_date) {
        this.booking_date = booking_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConnection_id() {
        return connection_id;
    }

    public void setConnection_id(String connection_id) {
        this.connection_id = connection_id;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}