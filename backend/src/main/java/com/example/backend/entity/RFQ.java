package com.example.backend.entity;

public class RFQ {

    private Long rfqId;
    private String name;
    private String bidStartTime;
    private String bidCloseTime;
    private String forcedCloseTime;
    private String currentBidCloseTime;
    private Integer triggerWindowMinutes;
    private Integer extensionDurationMinutes;
    private String extensionType;

    private String status;

    public RFQ() {}

    public Long getRfqId() {
        return rfqId;
    }

    public void setRfqId(Long rfqId) {
        this.rfqId = rfqId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBidStartTime() {
        return bidStartTime;
    }

    public void setBidStartTime(String bidStartTime) {
        this.bidStartTime = bidStartTime;
    }

    public String getBidCloseTime() {
        return bidCloseTime;
    }

    public void setBidCloseTime(String bidCloseTime) {
        this.bidCloseTime = bidCloseTime;
    }

    public String getForcedCloseTime() {
        return forcedCloseTime;
    }

    public void setForcedCloseTime(String forcedCloseTime) {
        this.forcedCloseTime = forcedCloseTime;
    }

    public Integer getTriggerWindowMinutes() {
        return triggerWindowMinutes;
    }

    public void setTriggerWindowMinutes(Integer triggerWindowMinutes) {
        this.triggerWindowMinutes = triggerWindowMinutes;
    }

    public Integer getExtensionDurationMinutes() {
        return extensionDurationMinutes;
    }

    public void setExtensionDurationMinutes(Integer extensionDurationMinutes) {
        this.extensionDurationMinutes = extensionDurationMinutes;
    }

    public String getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getCurrentBidCloseTime() {
    return currentBidCloseTime;
}

public void setCurrentBidCloseTime(String currentBidCloseTime) {
    this.currentBidCloseTime = currentBidCloseTime;
}
}