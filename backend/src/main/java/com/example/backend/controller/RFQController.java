package com.example.backend.controller;

import com.example.backend.entity.Bid;
import com.example.backend.entity.RFQ;
import com.example.backend.service.BidService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RFQController {
    @Autowired
    private BidService bidService;

    @GetMapping("/rfq")
    public RFQ getSampleRFQ() {
        RFQ rfq = new RFQ();
        rfq.setRfqId(1L);
        rfq.setName("Sample RFQ");
        rfq.setBidStartTime("2026-04-27 10:00");
        rfq.setBidCloseTime("2026-04-27 18:00");
        rfq.setForcedCloseTime("2026-04-27 19:00");
        rfq.setTriggerWindowMinutes(10);
        rfq.setExtensionDurationMinutes(5);
        rfq.setExtensionType("ANY_BID");
        rfq.setStatus("ACTIVE");

        return rfq;
    }


        @GetMapping("/bid")
    public Bid getSampleBid() {
        Bid bid = new Bid();
        bid.setBidId(1L);
        bid.setRfqId(1L);
        bid.setSupplierId(101L);
        bid.setPrice(950.0);
        bid.setCreatedAt("2026-04-27 17:55");

        return bid;
    }
        @PostMapping("/bid")
    public Bid placeBid(@RequestBody Bid bid) {
        return bidService.addBid(bid);
    }




        @GetMapping("/bids")
    public List<Bid> getAllBids() {
        return bidService.getAllBids();
    }


        @GetMapping("/bids/lowest")
    public Bid getLowestBid() {
        return bidService.getLowestBid();
    }


    @GetMapping("/rfq/current-time")
public String getCurrentCloseTime() {
    return bidService.getCurrentCloseTime();
}
}