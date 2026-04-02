package com.gasbooking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
public class Paymentcontroller {

    @Autowired
    private Orderepository ordersRepo;

    @Autowired
    private Paymentrepository paymentRepo;
    @Autowired
    private Stockrepository stockRepo;
    @Autowired
    private Gasagencyrepository agencyRepo;
    @Autowired
    private Supplierrepository supplierRepo;
    
    @GetMapping("/payment/page")
    public String showPaymentSuccess(@RequestParam("bookingId") Long booking_id,
                                     
                                     @RequestParam("cylinderType") String cylinderType,HttpSession session,
                                     Model model)  {      
        // 1. Fetch the order details using the bookingId from the URL
        Orders orders = ordersRepo.findById(booking_id).orElse(null);
        
        if (orders != null) {
            // 2. Update the Order Status in the 'orders' table
            orders.setStatus("Confirmed & Paid");
            ordersRepo.save(orders);
            List<Supplier> suppliers = supplierRepo
                    .findByAgency_AgencyId(orders.getAgency_id());

            if (!suppliers.isEmpty()) {
                Supplier supplier = suppliers.get(0); // simple logic

                orders.setSupplier_id((long) supplier.getSupplier_id());
                ordersRepo.save(orders);
            }

            // 3. Create a new record for the 'payments' table
            Payment payment = paymentRepo.findByOrders(orders); // check existing
            if (payment == null) {
                payment = new Payment();
            }
            payment.setOrders(orders);
            double pricePerCylinder;

            if (cylinderType.equals("Commercial")) {
                pricePerCylinder = 1200.0;
            } else {
                pricePerCylinder = 850.0;
            }
            int orderQuantity = orders.getQuantity();
            

            double totalAmount = pricePerCylinder * orderQuantity;
            payment.setAmount(totalAmount);
            payment.setPaymentMethod("Online");
            payment.setPaymentStatus("Success");
            payment.setPaymentDate(LocalDate.now());
            payment.setTxn_Id("TXN_" + System.currentTimeMillis());
            gasagency agency = agencyRepo.findById(orders.getAgency_id()).orElse(null);
            Stocks agencyStock = stockRepo.findByAgency(agency).orElse(null);
            if (agencyStock != null) {

                int privateStock = (agencyStock.getPrivate_stock() != null) ? agencyStock.getPrivate_stock() : 0;
                int commercialStock = (agencyStock.getCommercial_stock() != null) ? agencyStock.getCommercial_stock() : 0;
                int reservedStock = (agencyStock.getReserved() != null) ? agencyStock.getReserved() : 0;

             // Prevents stock from going below zero during a booking
                if (cylinderType.toLowerCase().contains("commercial")) {
                    int newStock = Math.max(0, commercialStock - orderQuantity);
                    agencyStock.setCommercial_stock(newStock);
                } else {
                    int newStock = Math.max(0, privateStock - orderQuantity);
                    agencyStock.setPrivate_stock(newStock);
                }

                // Increase reserved
                reservedStock = reservedStock + orderQuantity;
                agencyStock.setReserved(reservedStock);

                
                agencyStock.setQuantity(agencyStock.getPrivate_stock() + agencyStock.getCommercial_stock() + agencyStock.getReserved());

                agencyStock.setLast_updated(LocalDate.now());

                stockRepo.save(agencyStock);
            }
            // This saves the data to the payments table you showed in phpMyAdmin
            paymentRepo.save(payment);
            orders = ordersRepo.findById(booking_id).get();
            // 4. Pass objects to the HTML page
            model.addAttribute("orders", orders);
            model.addAttribute("payment", payment);
        } else {
            // If bookingId is invalid, you can redirect to an error page or dashboard
            return "redirect:/user/dashboard";
        }

        return "payment"; // Returns payment_success.html
    }
}