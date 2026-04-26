package com.example.backend.controller;

import com.example.backend.entity.Bid;
import com.example.backend.entity.RFQ;
import com.example.backend.entity.ActivityLog;
import com.example.backend.service.BidService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class RFQController {

    @Autowired
    private BidService bidService;

    //  Get RFQ details
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

    //  Place a bid
    @PostMapping("/bid")
    public Bid placeBid(@RequestBody Bid bid) {
        return bidService.addBid(bid);
    }

    //  Get all bids
    @GetMapping("/bids")
    public List<Bid> getAllBids() {
        return bidService.getAllBids();
    }

    //  Get lowest bid (L1)
    @GetMapping("/bids/lowest")
    public Bid getLowestBid() {
        return bidService.getLowestBid();
    }

    //  Get ranking (L1, L2, L3...)
    @GetMapping("/ranking")
    public List<Bid> getRanking() {
        return bidService.getRanking();
    }

    //  Get current auction close time
    @GetMapping("/rfq/current-time")
    public String getCurrentCloseTime() {
        return bidService.getCurrentCloseTime();
    }

    //  Get activity logs
    @GetMapping("/logs")
    public List<ActivityLog> getLogs() {
        return bidService.getLogs();
    }
}