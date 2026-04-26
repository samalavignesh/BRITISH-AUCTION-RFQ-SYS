package com.example.backend.service;

import com.example.backend.entity.ActivityLog;
import com.example.backend.entity.Bid;
import com.example.backend.entity.RFQ;
import com.example.backend.repository.ActivityLogRepository;
import com.example.backend.repository.BidRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ActivityLogRepository logRepository;

    private RFQ rfq = new RFQ();
    private Long previousL1SupplierId = null;

    public BidService() {
        rfq.setBidCloseTime("2026-04-27 18:00");
        rfq.setForcedCloseTime("2026-04-27 19:00");
        rfq.setTriggerWindowMinutes(10);
        rfq.setExtensionDurationMinutes(5);
        rfq.setExtensionType("ANY_BID");
        rfq.setCurrentBidCloseTime(rfq.getBidCloseTime());
    }

    public Bid addBid(Bid bid) {

        Bid lowest = getLowestBid();
        if (lowest != null && bid.getPrice() >= lowest.getPrice()) {
            throw new IllegalArgumentException("Bid must be lower than current lowest");
        }

        bidRepository.save(bid);

        logEvent("BID", "Supplier " + bid.getSupplierId() + " bid " + bid.getPrice(), bid.getCreatedAt());

        checkAndExtendAuction(bid);

        return bid;
    }

    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    public List<Bid> getRanking() {
        return bidRepository.findAll()
                .stream()
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
        return logRepository.findAll();
    }

    private void logEvent(String type, String desc, String time) {
        ActivityLog log = new ActivityLog();
        log.setEventType(type);
        log.setDescription(desc);
        log.setTimestamp(time);
        logRepository.save(log);
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
                    shouldExtend = true;
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