package com.gasbooking;

import jakarta.persistence.*;

@Entity
@Table(name = "supplier") 
public class Supplier {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 11)
    private int supplier_id;
	@ManyToOne
	@JoinColumn(name = "agency_id")
	private gasagency agency;
    
    private String password;
    private String supplier_name;
    private String phone_no;
    private boolean firstLogin = true;
    
    private String email;

    
    @Column(columnDefinition = "varchar(20) default 'pending'")
    private String status = "pending";


   

	public int getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(int supplier_id) {
		this.supplier_id = supplier_id;
	}
	

	public void setAgency(gasagency agency) {
	    this.agency = agency;
	}

	public gasagency getAgency() {
	    return agency;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}



	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAgency_id() {
	    return agency != null ? agency.getAgencyId() : null;
	}
	public boolean isFirstLogin() {
	    return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
	    this.firstLogin = firstLogin;
	}
	 

    // IMPORTANT: Right-click -> Source -> Generate Getters and Setters for all fields
}