package com.example.backend.service;

import com.example.backend.entity.Bid;
import com.example.backend.entity.RFQ;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BidService {

    private void checkAndExtendAuction(Bid bid) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    LocalDateTime bidTime = LocalDateTime.parse(bid.getCreatedAt(), formatter);
    LocalDateTime currentCloseTime = LocalDateTime.parse(rfq.getCurrentBidCloseTime(), formatter);
    LocalDateTime forcedCloseTime = LocalDateTime.parse(rfq.getForcedCloseTime(), formatter);

    // Trigger window start
    LocalDateTime triggerStart = currentCloseTime.minusMinutes(rfq.getTriggerWindowMinutes());

    // Check if bid is within trigger window
    if (bidTime.isAfter(triggerStart) && bidTime.isBefore(currentCloseTime)) {

        LocalDateTime newCloseTime = currentCloseTime.plusMinutes(rfq.getExtensionDurationMinutes());

        // Do not exceed forced close time
        if (newCloseTime.isAfter(forcedCloseTime)) {
            newCloseTime = forcedCloseTime;
        }

        rfq.setCurrentBidCloseTime(newCloseTime.format(formatter));

        System.out.println("Auction Extended to: " + rfq.getCurrentBidCloseTime());
    }
}
    
    public BidService() {
    rfq.setBidCloseTime("2026-04-27 18:00");
    rfq.setForcedCloseTime("2026-04-27 19:00");
    rfq.setTriggerWindowMinutes(10);
    rfq.setExtensionDurationMinutes(5);
    rfq.setCurrentBidCloseTime(rfq.getBidCloseTime());
}

    private List<Bid> bids = new ArrayList<>();
    private RFQ rfq = new RFQ();

    // Add new bid
    public Bid addBid(Bid bid) {
    bids.add(bid);

    checkAndExtendAuction(bid);

    return bid;
}

    // Get all bids
    public List<Bid> getAllBids() {
        return bids;
    }

    // Get lowest bid (L1)
    public Bid getLowestBid() {
        return bids.stream()
                .min(Comparator.comparing(Bid::getPrice))
                .orElse(null);
    }
}