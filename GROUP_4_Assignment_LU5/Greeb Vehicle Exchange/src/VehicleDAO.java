// VehicleDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public boolean registerVehicle(Vehicle vehicle) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO vehicles (owner_id, plate_no, vehicle_type, fuel_type, manufacture_year, mileage, condition_rating) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, vehicle.getOwnerId());
            pstmt.setString(2, vehicle.getPlateNo());
            pstmt.setString(3, vehicle.getVehicleType());
            pstmt.setString(4, vehicle.getFuelType());
            pstmt.setInt(5, vehicle.getManufactureYear());
            pstmt.setInt(6, vehicle.getMileage());
            pstmt.setInt(7, vehicle.getConditionRating());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error registering vehicle: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeStatement(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM vehicles WHERE owner_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ownerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setPlateNo(rs.getString("plate_no"));
                vehicle.setVehicleType(rs.getString("vehicle_type"));
                vehicle.setFuelType(rs.getString("fuel_type"));
                vehicle.setManufactureYear(rs.getInt("manufacture_year"));
                vehicle.setMileage(rs.getInt("mileage"));
                vehicle.setConditionRating(rs.getInt("condition_rating"));
                vehicle.setRegisteredDate(rs.getString("registered_date"));
                vehicles.add(vehicle);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching vehicles: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(pstmt);
            DatabaseConnection.closeConnection(conn);
        }

        return vehicles;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT v.*, u.name as owner_name FROM vehicles v JOIN users u ON v.owner_id = u.user_id";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setPlateNo(rs.getString("plate_no"));
                vehicle.setVehicleType(rs.getString("vehicle_type"));
                vehicle.setFuelType(rs.getString("fuel_type"));
                vehicle.setManufactureYear(rs.getInt("manufacture_year"));
                vehicle.setMileage(rs.getInt("mileage"));
                vehicle.setConditionRating(rs.getInt("condition_rating"));
                vehicle.setRegisteredDate(rs.getString("registered_date"));
                vehicles.add(vehicle);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all vehicles: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(stmt);
            DatabaseConnection.closeConnection(conn);
        }

        return vehicles;
    }

    public boolean isEligibleForExchange(Vehicle vehicle) {
        int currentYear = java.time.Year.now().getValue();
        int vehicleAge = currentYear - vehicle.getManufactureYear();

        // Eligibility criteria: Age > 5 years and fuel type is petrol or diesel
        return vehicleAge > 5 &&
                (vehicle.getFuelType().equals("petrol") || vehicle.getFuelType().equals("diesel"));
    }

    public double calculateExchangeValue(Vehicle vehicle) {
        int currentYear = java.time.Year.now().getValue();
        int vehicleAge = currentYear - vehicle.getManufactureYear();

        double baseValue = 5000; // Base value in USD
        double ageDeduction = vehicleAge * 500;
        double mileageDeduction = vehicle.getMileage() * 0.1;
        double conditionBonus = vehicle.getConditionRating() * 100;

        double value = baseValue - ageDeduction - mileageDeduction + conditionBonus;
        return Math.max(value, 1000); // Minimum value of 1000 USD
    }

    public double calculateSubsidyPercent(Vehicle vehicle) {
        double baseSubsidy = 20.0; // Base 20% subsidy

        // Additional subsidy based on vehicle age
        int currentYear = java.time.Year.now().getValue();
        int vehicleAge = currentYear - vehicle.getManufactureYear();
        double ageBonus = Math.min((vehicleAge - 5) * 2, 20); // Up to 20% extra for older vehicles

        // Bonus for high mileage vehicles
        double mileageBonus = Math.min(vehicle.getMileage() / 10000, 10); // Up to 10% extra

        return Math.min(baseSubsidy + ageBonus + mileageBonus, 50); // Max 50% subsidy
    }
}