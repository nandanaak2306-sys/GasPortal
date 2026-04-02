package com.gasbooking;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/agency")
public class Gasagencycontroller {

    @Autowired
    private Gasagencyrepository agencyRepo;
    @Autowired
    private Userrepository userRepo; 
    @Autowired 
    private Supplierrepository supplierRepo;
    @Autowired
    private Stockrepository stockRepo;
    @Autowired
    private Orderepository ordersRepo;
    /**
     * Handles the registration form submission.
     * Uses @ModelAttribute to map form fields to the gasagency object.
     */
    @PostMapping("/register")
    public String registerAgency(@ModelAttribute gasagency agency, Model model) {
        
        // Validate Agency ID (e.g., AG followed by 4 digits like AG1234)
        if (agency.getAgencyId() == null || !agency.getAgencyId().matches("^AG\\d{4}$")) {
            model.addAttribute("error", "Error: Invalid Agency ID.");
            return "gasregistration";
        }
        
        // Validate phone number (exactly 10 digits)
        if (agency.getContact_phone() != null && !agency.getContact_phone().matches("\\d{10}")) {
            model.addAttribute("error", "Error: Invalid Phone Number.");
            return "gasregistration"; // Returns the user to the registration form
        }

        // Validate 6-digit Pincode starting with 56
        if (agency.getPincode() == null || !agency.getPincode().matches("^56\\d{4}$")) {
            model.addAttribute("error", "Error: Invalid Pincode.");
            return "gasregistration";
        }
        
        // 1. Check if the manual agency_id already exists to prevent a 500 error
        if (agencyRepo.existsById(agency.getAgencyId())) {
            // Add an error message to display on the HTML page
            model.addAttribute("error", "Error: Agency ID '" + agency.getAgencyId() + "' is already taken.");
            return  "gasregistration"; // Returns the user to the registration form
        }

        // 2. Save the new agency to the database
        try {
            agencyRepo.save(agency);
            model.addAttribute("successMsg", "Registration completed successfully!");
            return "gasregistration"; // Redirect to a success page
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("error", "Error: Email or Agency details already exist. Please try different details or log in.");
            return "gasregistration";
        } catch (Exception e) {
            // Catch-all for other database issues like missing required fields
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "gasregistration";
        }
    }
 // 
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "gasregistration"; // This looks for gasregistration.html
    }
    @PostMapping("/login")
    public String agencylogin(@RequestParam String agencyId, @RequestParam String password, HttpSession session, Model model) {
    	gasagency agency=agencyRepo.findById(agencyId).orElse(null);
    	if(agency!=null&&agency.getPassword().equals(password)) {
    		session.setAttribute("loggedInAgency", agency);
    		return "redirect:/agency/admin/dashboard";
    	}
    	else {
    		model.addAttribute("error", "Invalid agency id or password");
    		return "agency_login";
    	}
    }
    @GetMapping("/login")
    public String showLoginForm() {
    	return "agency_login";
    }
  
    @PostMapping("/approve_user")
    
    public String approveUser(@RequestParam long user_id,
                              @RequestParam String connection_id)
    {
                            
		
        userRepo.findById(user_id).ifPresent(user -> {
            user.setConnection_id(connection_id);
            user.setStatus("Approved");
            userRepo.save(user);
        });

        return "redirect:/agency/admin/dashboard";
    }
    
   
    	// Use the new method to fetch pending users for the logged-in agency
    @GetMapping("/admin/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        gasagency agency = (gasagency) session.getAttribute("loggedInAgency");
        if (agency == null) {
            return "redirect:/agency/login";
        }
        
        model.addAttribute("agency", agency);
        
        Stocks stock = stockRepo.findByAgency(agency).orElse(new Stocks());
        model.addAttribute("stock", stock);
        
        // 2. Synchronize totals for the UI (only for THIS agency's stock)
        int available = (stock.getPrivate_stock() != null ? stock.getPrivate_stock() : 0) + 
                    (stock.getCommercial_stock() != null ? stock.getCommercial_stock() : 0);
        int reserved = (stock.getReserved() != null ? stock.getReserved() : 0);
        int grandTotal = available + reserved;
        stock.setQuantity(grandTotal);
        
        // 3. Create a single-element list for the HTML table which expects a collection
        List<Stocks> inventory = java.util.Arrays.asList(stock);

       
        List<user> users = userRepo.findByStatusAndSelectedAgency_AgencyId("Pending", agency.getAgencyId());
        
        
        model.addAttribute("users", users);
        List<Supplier> suppliers = supplierRepo.findByAgency_AgencyId(agency.getAgencyId());
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("stocksList", inventory);  
        List<Orders> ordersList = ordersRepo.findByAgencyIdAndDelivery_status(agency.getAgencyId(), "Pending");
        	    
        model.addAttribute("ordersList", ordersList);
        Map<Long, user> usersMap = new HashMap<>();
        for (Orders order : ordersList) {
            userRepo.findById(order.getUserId()).ifPresent(u -> usersMap.put(u.getUser_id(), u));
        }
        model.addAttribute("usersMap", usersMap);

        return "agency_dashboard";
    }
    @PostMapping("/add-supplier")
    public String addSupplier(@ModelAttribute Supplier supplier, HttpSession session) {
        gasagency agency = (gasagency) session.getAttribute("loggedInAgency");
        
        if (supplier.getPhone_no() != null && !supplier.getPhone_no().matches("\\d{10}")) {
            return "redirect:/agency/admin/dashboard?error=invalid_phone";
        }
        
        if (agency != null) {
            supplier.setAgency(agency);
            supplier.setPassword(agency.getAgencyId()); // Password is Agency ID
            supplier.setStatus("Active"); // Default status
            
            supplierRepo.save(supplier);
        }
        return "redirect:/agency/admin/dashboard?success=true";
    }
    @PostMapping("/toggle-supplier-status")
    @ResponseBody
    public String toggleSupplierStatus(@RequestParam("supplier_id") int supplier_id) {
        Supplier supplier = supplierRepo.findById(supplier_id).orElse(null);
        
        if (supplier != null) {
            // Simple logic: if Active, make Inactive. If Inactive, make Active.
            if ("Active".equalsIgnoreCase(supplier.getStatus())) {
                supplier.setStatus("Inactive");
                
            } else {
                supplier.setStatus("Active");
               
            }
            supplierRepo.save(supplier);
            return "SUCCESS";
        }
        
        return "ERROR";
    }
    @PostMapping("/assign-supplier")
    public String assignSupplier(@RequestParam("bookingId") Long bookingId, 
                                 @RequestParam("supplier_id") Long supplier_id) {
        Orders order = ordersRepo.findById(bookingId).orElse(null);
        if (order != null) {
            order.setSupplier_id(supplier_id);
            ordersRepo.save(order);
        }
        return "redirect:/agency/admin/dashboard?view=bookings";
    }
             
            
           
    @PostMapping("/update-stock")
    public String updateStock(@RequestParam(value = "private_stock", required = false, defaultValue = "0") Integer newPrivate,
                              @RequestParam(value = "commercial_stock", required = false, defaultValue = "0") Integer newCommercial,
                              HttpSession session) {
        
        gasagency loggedIn = (gasagency) session.getAttribute("loggedInAgency");
        if (loggedIn == null) return "redirect:/agency/login";
        
        // 1. Fetch the EXISTING stock record from the DB to get the REAL Reserved value
        Stocks currentStock = stockRepo.findByAgency(loggedIn).orElse(new Stocks());

        // 2. Add the new values to the existing Private and Commercial values
        int existingPrivate = currentStock.getPrivate_stock() != null ? currentStock.getPrivate_stock() : 0;
        int existingCommercial = currentStock.getCommercial_stock() != null ? currentStock.getCommercial_stock() : 0;
        
        int safeNewPrivate = newPrivate != null ? newPrivate : 0;
        int safeNewCommercial = newCommercial != null ? newCommercial : 0;
        
        int updatedPrivate = existingPrivate + safeNewPrivate;
        int updatedCommercial = existingCommercial + safeNewCommercial;

        currentStock.setPrivate_stock(updatedPrivate);
        currentStock.setCommercial_stock(updatedCommercial);

        // 3. Keep the Reserved exactly as it was in the DB
        int existingReserved = (currentStock.getReserved() != null) ? currentStock.getReserved() : 0;
        
        // 4. Recalculate Total: (New Private + New Commercial + UNCHANGED Reserved)
        currentStock.setQuantity(updatedPrivate + updatedCommercial + existingReserved);
        currentStock.setLast_updated(LocalDate.now());
        
        // 5. Explicitly set the agency so the database foreign key isn't null for new records
        currentStock.setAgency(loggedIn);

        stockRepo.save(currentStock);
        
        return "redirect:/agency/admin/dashboard?view=stock";
    }
        // Redirect back to dashboard and stay on the stock tab


    @PostMapping("/update-delivery")
    public String updateDelivery(@RequestParam("bookingId") Long bookingId, 
                                 @RequestParam("status") String status) {
        
        Orders order = ordersRepo.findById(bookingId).orElse(null);

        if (order != null && "Delivered".equalsIgnoreCase(status)) {
            order.setDelivery_status("Delivered");
            ordersRepo.save(order);

            // Fetch the stock for this agency
            gasagency agency = agencyRepo.findById(order.getAgency_id()).orElse(null);
            Stocks stock = stockRepo.findByAgency(agency).orElse(null);

            if (stock != null) {
                
                int orderQty = (order.getQuantity() != null) ? order.getQuantity() : 1;
                
                // Subtract from reserved
                int currentReserved = (stock.getReserved() != null) ? stock.getReserved() : 0;
                stock.setReserved(Math.max(0, currentReserved - orderQty));

                // Recalculate total quantity: (Private + Commercial + Reserved)
                stock.setQuantity(stock.getPrivate_stock() + stock.getCommercial_stock() + stock.getReserved());
                
                stockRepo.save(stock);
            }
        }
        return "redirect:/agency/admin/dashboard?view=bookings";
    }
    @PostMapping("/stock/set-threshold")
    public String setThreshold(@RequestParam("level") Integer newLevel, HttpSession session) {
        
    	gasagency loggedIn = (gasagency) session.getAttribute("loggedInAgency");
        if (loggedIn == null) return "redirect:/agency/login";
    	
        
    	Stocks s = stockRepo.findByAgency(loggedIn).orElse(new Stocks());
        
        // Updates the recorder_level column (mapped as 'records' in Java)
        s.setRecords(newLevel); 
        s.setAgency(loggedIn); // Ensure the foreign key is set if it's a new empty stock!
        
        stockRepo.save(s);
        
        
        return "redirect:/agency/admin/dashboard?view=stock";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/agency/login";
    }
}

    
    

	