package com.gasbooking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
@Controller
@RequestMapping("/booking")
public class Ordercontroller {
	   @Autowired
	    private Userrepository userRepo;


    @Autowired
    private Orderepository ordersRepo;
    
    @Autowired
    private Stockrepository stockRepo;

    @PostMapping("/create")
    
    public String createBooking(@RequestParam("quantity") int quantity,
                                @RequestParam("cylinderType") String cylinderType,
                                HttpSession session) { 

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/user/login";
        
		user user = userRepo.findById(userId).get();
        Stocks stock = stockRepo.findByAgency(user.getSelectedAgency()).orElse(new Stocks());
        
        int availableStock = cylinderType.equals("Commercial") ? 
            (stock.getCommercial_stock() != null ? stock.getCommercial_stock() : 0) : 
            (stock.getPrivate_stock() != null ? stock.getPrivate_stock() : 0);
            
        if (availableStock < quantity) {
            return "redirect:/user/dashboard?error=out_of_stock";
        }

        Orders order = new Orders();

        order.setUserId(userId);
        order.setAgency_id(user.getSelectedAgency().getAgencyId());
        order.setCylinder_type(cylinderType);;
        order.setDelivery_status("Pending");
        order.setQuantity(quantity);
        order.setConnection_id(user.getConnection_id());
        order.setBooking_date(LocalDate.now());
        order.setStatus("Pending Payment");

        ordersRepo.save(order);

        return "redirect:/payment/page?bookingId=" + order.getBooking_id()
        + "&quantity=" + quantity
        + "&cylinderType=" + cylinderType;
    }
    @GetMapping("/page")
    public String bookingPage(@RequestParam("quantity") int quantity,
                              @RequestParam("cylinderType") String cylinderType,
                              HttpSession session,
                              Model model){

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/user/login";
        user user = userRepo.findById(userId).get();
        Stocks stock = stockRepo.findByAgency(user.getSelectedAgency()).orElse(new Stocks());
        
        int availableStock = cylinderType.equals("Commercial") ? 
            (stock.getCommercial_stock() != null ? stock.getCommercial_stock() : 0) : 
            (stock.getPrivate_stock() != null ? stock.getPrivate_stock() : 0);
            
        if (availableStock < quantity) {
            return "redirect:/user/dashboard?error=out_of_stock";
        }

        double pricePerCylinder;

        if (cylinderType.equals("Commercial")) {
            pricePerCylinder = 1200.0;
        } else {
            pricePerCylinder = 850.0;
        }

        double totalPrice = pricePerCylinder * quantity;

        model.addAttribute("user", user);
        model.addAttribute("quantity", quantity);
        model.addAttribute("cylinderType", cylinderType);
        model.addAttribute("price", totalPrice);

        return "booking";
    }
}