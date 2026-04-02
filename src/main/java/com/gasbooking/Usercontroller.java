package com.gasbooking;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class Usercontroller {

   
    @Autowired
    private Userrepository userRepo; 

    @Autowired
    private Gasagencyrepository agencyRepo;
    @Autowired
    private Orderepository ordersRepo;
    @Autowired
    private Paymentrepository paymentRepo;
    @Autowired
    private Supplierrepository supplierRepo;
    @Autowired
    private Stockrepository stockRepo;
   
    @PostMapping("/register")
    public String registerUser(@ModelAttribute user user, Model model) {
        
        // Validate 12-digit Aadhaar Number (should not start with 0 or 1)
        if (user.getAadhar_no() == null || !user.getAadhar_no().matches("^[2-9][0-9]{11}$")) {
            return "redirect:/user/register?error=invalid_aadhar";
        }
        
        // Validate 6-digit Pincode starting with 56
        if (user.getPincode() == null || !user.getPincode().matches("^56\\d{4}$")) {
            return "redirect:/user/register?error=invalid_pincode";
        }
        
        user.setStatus("Pending"); 
        try {
            userRepo.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return "redirect:/user/register?error=email_exists";
        }
        return "redirect:/user/login?registered=true"; 
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "user_login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        user user = userRepo.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getUser_id());
            return "redirect:/user/dashboard";
        }

        model.addAttribute("error", "Invalid credentials");
        return "user_login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        user currentUser = (user) session.getAttribute("loggedInUser");
        
        if (currentUser == null) {
            return "redirect:/user/login"; 
        }

        
        user updatedUser = userRepo.findById(currentUser.getUser_id()).orElse(currentUser);
        
        
        List<gasagency> localAgencies = agencyRepo.findByLocality(updatedUser.getLocality());
        List<Orders> orders = ordersRepo.findByUserId(updatedUser.getUser_id());

     // 🔥 limit to 50
     if (orders.size() > 50) {
         orders = orders.subList(0, 50);
     }

     // Map supplier details
     Map<Long, Supplier> supplierMap = new HashMap<>();

     for (Orders o : orders) {
         if (o.getSupplier_id() != null) {
             supplierRepo.findById(o.getSupplier_id().intValue())
                 .ifPresent(s -> supplierMap.put(o.getBooking_id(), s));
         }
     }

     model.addAttribute("orders", orders);
     model.addAttribute("supplierMap", supplierMap);
        
        List<Payment> payments = paymentRepo.findByOrders_UserId(updatedUser.getUser_id());
        
        if (updatedUser.getSelectedAgency() != null) {
            Stocks stock = stockRepo.findByAgency(updatedUser.getSelectedAgency()).orElse(null);
            model.addAttribute("agencyStock", stock);
        }
        
        model.addAttribute("user", updatedUser);
        model.addAttribute("agencies", localAgencies);
        model.addAttribute("ordersList", orders);
        model.addAttribute("supplierMap", supplierMap);
        model.addAttribute("payments", payments);

        return "user_dasboard";
    }
    @PostMapping("/select-agency")
    public String selectAgency(@RequestParam String agency_id, 
                               @RequestParam(value="existing_connection_id", required=false) String existingConnId,
                               HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/user/login";
        
        user currentUser = userRepo.findById(userId).orElse(null);

        if (currentUser != null) {
            gasagency agency = agencyRepo.findById(agency_id).orElse(null);

            if (agency != null) {
                // If they provided an existing connection ID, validate it
                if (existingConnId != null && !existingConnId.trim().isEmpty()) {
                    String agencyName = agency.getAgency_name();
                    String prefix = agencyName.length() >= 3 ? agencyName.substring(0, 3) : agencyName;
                    prefix = prefix.toUpperCase();
                    
                    if (!existingConnId.matches("(?i)" + prefix + "-\\d{3}")) {
                        return "redirect:/user/dashboard?error=invalid_connection_id";
                    }
                    
                    currentUser.setConnection_id(existingConnId.toUpperCase());
                } else {
                    currentUser.setConnection_id(null);
                }
                
                currentUser.setSelectedAgency(agency);
                currentUser.setStatus("Pending");
                
                userRepo.save(currentUser);
                session.setAttribute("loggedInUser", currentUser);
            }
        }
        return "redirect:/user/dashboard";
    }
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/user/login";

        user currentUser = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", currentUser);

        // Logic to find agencies in the same locality
        if (currentUser != null && currentUser.getLocality() != null) {
            List<gasagency> agencies = agencyRepo.findByLocality(currentUser.getLocality());
            
            // SORTING: Same company agencies move to the top of the list
            if (currentUser.getSelectedAgency() != null) {
                agencies.sort((a, b) -> {
                    boolean aMatches = a.getAgency_name().equalsIgnoreCase(currentUser.getSelectedAgency().getAgency_name());
                    boolean bMatches = b.getAgency_name().equalsIgnoreCase(currentUser.getSelectedAgency().getAgency_name());
                    if (aMatches && !bMatches) return -1;
                    if (!aMatches && bMatches) return 1;
                    return 0;
                });
            }
            
            model.addAttribute("agencies", agencies);
        }
        return "user_profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") user updatedUser, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        user existingUser = userRepo.findById(userId).orElse(null);

        if (existingUser != null) {
            
            // Validate 6-digit Pincode starting with 56
            if (updatedUser.getPincode() != null && !updatedUser.getPincode().matches("^56\\d{4}$")) {
                return "redirect:/user/profile?error=invalid_pincode";
            }
            
            // Check if locality changed
            if (!existingUser.getLocality().equalsIgnoreCase(updatedUser.getLocality())) {
                existingUser.setLocality(updatedUser.getLocality());
                existingUser.setAddress(updatedUser.getAddress());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setPincode(updatedUser.getPincode());
                
                // If they change locality the status is set pending
                existingUser.setStatus("Pending Transfer");
                existingUser.setSelectedAgency(null); 
                
                userRepo.save(existingUser);
                return "redirect:/user/profile?msg=localityChanged";
            }

            // Standard updates (Name, Phone, etc.)
            existingUser.setFull_name(updatedUser.getFull_name());
            userRepo.save(existingUser);
        }
        return "redirect:/user/dashboard?success=true";
    }
  
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/user/login?logout=true"; 
    }
}