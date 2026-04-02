package com.gasbooking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "gasagency")
public class gasagency {

	@Id
	@Column(name = "agency_id", length = 50)
	private String agencyId;
    private String agency_name;
    private String email;
    private String contact_phone;
   
	private String office_address;
    private String locality;
    private String city;
    private String state;
    private String pincode;
    private String sector;
	private String password;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}
	public String getAgency_name() {
		return agency_name;
	}
	public void setAgency_name(String agency_name) {
		this.agency_name = agency_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	 public String getContact_phone() {
			return contact_phone;
		}
		public void setContact_phone(String contact_phone) {
			this.contact_phone = contact_phone;
		}
	

	public String getOffice_address() {
		return office_address;
	}
	public void setOffice_address(String office_address) {
		this.office_address = office_address;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	


    // IMPORTANT: Right-click -> Source -> Generate Getters and Setters
    // Select All -> Generate
}