package com.gasbooking;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stocks")
public class Stocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stock_id;

    @OneToOne
    @JoinColumn(name = "agency_id")
    private gasagency agency;// Matches your "Agency ID" link
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "last_updated")
    private LocalDate last_updated;
    @Column(name = "private_stocks")
    private Integer private_stock; // For Domestic cylinders
   
    @Column(name = "commercial_stocks")
    private Integer commercial_stock; // For Business cylinders
   
    @Column(name = "recorder_level")
    private Integer records;
    @Column(name = "reserved_stock")
    private Integer reserved;
    public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public LocalDate getLast_updated() {
		return last_updated;
	}
	public void setLast_updated(LocalDate last_updated) {
		this.last_updated = last_updated;
	}
	
	public Integer getRecords() {
		return records;
	}
	public void setRecords(Integer records) {
		this.records = records;
	}
	public Integer getReserved() {
		return reserved;
	}
	public gasagency getAgency() {
		return agency;
	}
	public void setAgency(gasagency agency) {
		this.agency = agency;
	}
	public void setReserved(Integer reserved) {
		this.reserved = reserved;
	}
	// Getters and Setters
    public Long getStock_id() { return stock_id; }
    public void setStock_id(Long stock_id) { this.stock_id = stock_id; }



    public Integer getPrivate_stock() { return private_stock; }
    public void setPrivate_stock(Integer private_stock) { this.private_stock = private_stock; }

    public Integer getCommercial_stock() { return commercial_stock; }
    public void setCommercial_stock(Integer commercial_stock) { this.commercial_stock = commercial_stock; }
}