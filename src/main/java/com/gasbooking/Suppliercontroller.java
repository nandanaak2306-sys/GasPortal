package com.gasbooking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller // This ensures text is returned directly to the browser
@RequestMapping("/supplier")
public class Suppliercontroller {

    @Autowired
    private Supplierrepository supplierRepo;
    @Autowired
    private Gasagencyrepository agencyRepo;
    @Autowired
    private Orderepository ordersRepo;
    @Autowired
    private Userrepository userRepo;
    @Autowired
    private Stockrepository stockRepo;


    @PostMapping("/register")
    public String registerSupplier(@ModelAttribute Supplier s) {
        try
        {
            if (s.getPhone_no() != null && !s.getPhone_no().matches("\\d{10}")) {
                return "Error: Phone number must be exactly 10 digits.";
            }

        	if (supplierRepo.existsByEmail(s.getEmail())) {
        	    return "Email already exists!";
        	}
        	if (!agencyRepo.existsById(s.getAgency_id())) {
                return "Error: Company does not exist. Please enter a valid Company Registration ID.";
        	}
            // 2. Set the default status to pending
            s.setStatus("pending");

            // 3. Save the new supplier to the database
            supplierRepo.save(s);

            // 4. Return success message
            return "Registration successful! Your status is: " + s.getStatus();

        } catch (Exception e) {
            // Catch database errors like Foreign Key issues with agency_id
            return "Error during registration: " + e.getMessage();
        }
    }
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Supplier supplier = supplierRepo.findByEmailAndPassword(email, password);
        if (supplier != null) {

            if (!"Active".equalsIgnoreCase(supplier.getStatus())) {
                model.addAttribute("error", "Your account is not active.");
                return "supplier_login";
            }

            session.setAttribute("supplier", supplier);
            if (supplier.isFirstLogin()) {
                return "redirect:/supplier/reset-password";
            }

            return "redirect:/supplier/dashboard";
        }
        model.addAttribute("error", "Invalid login");
        return "supplier_login";
    }
    @GetMapping("/login")
    public String showLoginPage() {
    	
    	
        return "supplier_login";
    }
    @GetMapping("/reset-password")
    public String showResetPage() {
        return "supplier_reset";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword,
                                HttpSession session) {

        Supplier supplier = (Supplier) session.getAttribute("supplier");

        
        if (supplier == null) {
            return "redirect:/supplier/login";
        }

        supplier.setPassword(newPassword);
        supplier.setFirstLogin(false);

        supplierRepo.save(supplier);

        return "redirect:/supplier/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Supplier supplier = (Supplier) session.getAttribute("supplier");

        if (supplier == null) {
            return "redirect:/supplier/login";
        }

        List<Orders> orders = ordersRepo.findBySupplierId(
                (long) supplier.getSupplier_id()
        );

        // 🔥 Map users
        Map<Long, user> userMap = new HashMap<>();

        for (Orders o : orders) {
            userRepo.findById(o.getUserId())
                .ifPresent(u -> userMap.put(o.getBooking_id(), u));
        }

        model.addAttribute("orders", orders);
        model.addAttribute("userMap", userMap);

        return "supplier_dashboard";
    }
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("booking_id") Long booking_id, 
                               @RequestParam("status") String status,
                               @RequestParam(value = "quantity", required = false) Integer quantity) {

        Orders order = ordersRepo.findById(booking_id).orElse(null);

        if (order != null) {

            order.setDelivery_status(status);

            if (quantity != null) {
                order.setQuantity(quantity);
            }

            ordersRepo.save(order);

            if ("Delivered".equalsIgnoreCase(status)) {

                gasagency agency = agencyRepo.findById(order.getAgency_id()).orElse(null);
                Stocks stock = stockRepo.findByAgency(agency).orElse(null);

                if (stock != null) {

                    int orderQty = (order.getQuantity() != null) ? order.getQuantity() : 1;

                    int currentReserved = (stock.getReserved() != null) ? stock.getReserved() : 0;

                    // 🔻 reduce reserved
                    stock.setReserved(Math.max(0, currentReserved - orderQty));

                    // 🔁 recalculate total
                    stock.setQuantity(
                            stock.getPrivate_stock() +
                            stock.getCommercial_stock() +
                            stock.getReserved()
                    );

                    stockRepo.save(stock);
                }
            }
        }

        return "redirect:/supplier/dashboard";
    }
}