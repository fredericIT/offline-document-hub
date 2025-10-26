// ExchangeOffer.java
public class ExchangeOffer {
    private int offerId;
    private int vehicleId;
    private double exchangeValue;
    private double subsidyPercent;
    private String status;
    private String createdDate;
    private String processedDate;
    private String adminNotes;

    public ExchangeOffer() {}

    public ExchangeOffer(int offerId, int vehicleId, double exchangeValue,
                         double subsidyPercent, String status) {
        this.offerId = offerId;
        this.vehicleId = vehicleId;
        this.exchangeValue = exchangeValue;
        this.subsidyPercent = subsidyPercent;
        this.status = status;
    }

    // Getters and setters
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public double getExchangeValue() { return exchangeValue; }
    public void setExchangeValue(double exchangeValue) { this.exchangeValue = exchangeValue; }

    public double getSubsidyPercent() { return subsidyPercent; }
    public void setSubsidyPercent(double subsidyPercent) { this.subsidyPercent = subsidyPercent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getProcessedDate() { return processedDate; }
    public void setProcessedDate(String processedDate) { this.processedDate = processedDate; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}