// Vehicle.java
public class Vehicle {
    private int vehicleId;
    private int ownerId;
    private String plateNo;
    private String vehicleType;
    private String fuelType;
    private int manufactureYear;
    private int mileage;
    private int conditionRating;
    private String registeredDate;

    public Vehicle() {}

    public Vehicle(int vehicleId, int ownerId, String plateNo, String vehicleType,
                   String fuelType, int manufactureYear, int mileage, int conditionRating) {
        this.vehicleId = vehicleId;
        this.ownerId = ownerId;
        this.plateNo = plateNo;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
        this.manufactureYear = manufactureYear;
        this.mileage = mileage;
        this.conditionRating = conditionRating;
    }

    // Getters and setters
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getPlateNo() { return plateNo; }
    public void setPlateNo(String plateNo) { this.plateNo = plateNo; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public int getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(int manufactureYear) { this.manufactureYear = manufactureYear; }

    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }

    public int getConditionRating() { return conditionRating; }
    public void setConditionRating(int conditionRating) { this.conditionRating = conditionRating; }

    public String getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }
}