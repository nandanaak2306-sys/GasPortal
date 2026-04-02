package com.gasbooking;

import jakarta.persistence.*;

@Entity
@Table(name = "`user`") // Using backticks for the reserved keyword 'user'
public class user {
	// Add this inside your user class
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
 // Add this inside your user class
  
 // 
    @ManyToOne
    @JoinColumn(name = "agency_id", referencedColumnName = "agency_id")
    private gasagency selectedAgency; // This represents the actual Agency object

    // Getters and Setters
    public gasagency getSelectedAgency() {
		return selectedAgency;
	}

    
    public void setSelectedAgency(gasagency selectedAgency) { this.selectedAgency = selectedAgency; }
   
    private String aadhar_no;
    
    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }
   
	private String connection_id;
    
   
   public Long getUser_id() {
	return user_id;
}
   public void setUser_id(Long user_id) {
	this.user_id = user_id;
   }
   public String getFull_name() {
		return full_name;
   }
   public void setFull_name(String full_name) {
	this.full_name = full_name;
   }
   public String getEmail() {
	return email;
   }
   public void setEmail(String email) {
	this.email = email;
   }
   public String getPassword() {
	return password;
   }
   public void setPassword(String password) {
	this.password = password;
   }
   public String getAddress() {
	return address;
   }
   public void setAddress(String address) {
	this.address = address;
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
   
   public String getStatus() {
	return status;
}
   public void setStatus(String status) {
	this.status = status;
   }

   private String status;
	private String full_name;
    private String email;
    private String password;
    private String address;
    private String locality;
    private String city;
    private String state;
    private String pincode;

	


  
    // Right-click -> Source -> Generate Getters and Setters




	public String getConnection_id() {
		return connection_id;
	}
	public void setConnection_id(String connection_id) {
		this.connection_id = connection_id;
	}

		// TODO Auto-generated method st
	}
	
		
	

