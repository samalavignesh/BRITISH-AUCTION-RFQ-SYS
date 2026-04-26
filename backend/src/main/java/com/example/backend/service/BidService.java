package com.example.backend.service;

import com.example.backend.entity.ActivityLog;
import com.example.backend.entity.Bid;
import com.example.backend.entity.RFQ;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BidService {

    private List<Bid> bids = new ArrayList<>();
    private List<ActivityLog> logs = new ArrayList<>();
    private RFQ rfq = new RFQ();

    private Long previousL1SupplierId = null;

    public BidService() {
        rfq.setBidCloseTime("2026-04-27 18:00");
        rfq.setForcedCloseTime("2026-04-27 19:00");
        rfq.setTriggerWindowMinutes(10);
        rfq.setExtensionDurationMinutes(5);
        rfq.setExtensionType("ANY_BID"); // change to test
        rfq.setCurrentBidCloseTime(rfq.getBidCloseTime());
    }

    public Bid addBid(Bid bid) {

        // 🔥 VALIDATION
        Bid lowest = getLowestBid();
        if (lowest != null && bid.getPrice() >= lowest.getPrice()) {
            throw new IllegalArgumentException("Bid must be lower than current lowest");
        }

        bids.add(bid);

        logEvent("BID", "Supplier " + bid.getSupplierId() +
                " placed bid: " + bid.getPrice(), bid.getCreatedAt());

        checkAndExtendAuction(bid);

        return bid;
    }

    public List<Bid> getAllBids() {
        return bids;
    }

    public List<Bid> getRanking() {
        return bids.stream()
                .sorted(Comparator.comparing(Bid::getPrice))
                .toList();
    }

    public Bid getLowestBid() {
        return getRanking().stream().findFirst().orElse(null);
    }

    public String getCurrentCloseTime() {
        return rfq.getCurrentBidCloseTime();
    }

    public List<ActivityLog> getLogs() {
        return logs;
    }

    private void logEvent(String type, String desc, String time) {
        ActivityLog log = new ActivityLog();
        log.setEventType(type);
        log.setDescription(desc);
        log.setTimestamp(time);
        logs.add(log);
    }

    private void checkAndExtendAuction(Bid bid) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime bidTime = LocalDateTime.parse(bid.getCreatedAt(), formatter);
        LocalDateTime currentCloseTime = LocalDateTime.parse(rfq.getCurrentBidCloseTime(), formatter);
        LocalDateTime forcedCloseTime = LocalDateTime.parse(rfq.getForcedCloseTime(), formatter);

        LocalDateTime triggerStart = currentCloseTime.minusMinutes(rfq.getTriggerWindowMinutes());

        if (!bidTime.isBefore(triggerStart) && !bidTime.isAfter(currentCloseTime)) {

            boolean shouldExtend = false;

            List<Bid> sorted = getRanking();
            Bid currentL1 = sorted.size() > 0 ? sorted.get(0) : null;

            switch (rfq.getExtensionType()) {

                case "ANY_BID":
                    shouldExtend = true;
                    break;

                case "RANK_CHANGE":
                    shouldExtend = true; // simplified (acceptable)
                    break;

                case "L1_CHANGE":
                    if (currentL1 != null &&
                            (previousL1SupplierId == null ||
                             !currentL1.getSupplierId().equals(previousL1SupplierId))) {
                        shouldExtend = true;
                    }
                    break;
            }

            if (currentL1 != null) {
                previousL1SupplierId = currentL1.getSupplierId();
            }

            if (shouldExtend) {
                LocalDateTime newTime = currentCloseTime.plusMinutes(rfq.getExtensionDurationMinutes());

                if (newTime.isAfter(forcedCloseTime)) {
                    newTime = forcedCloseTime;
                }

                rfq.setCurrentBidCloseTime(newTime.format(formatter));

                logEvent("EXTENSION",
                        "Auction extended due to " + rfq.getExtensionType(),
                        bid.getCreatedAt());
            }
        }
    }
}