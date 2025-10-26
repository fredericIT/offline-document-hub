// ExchangeOfferDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeOfferDAO {

    public boolean createExchangeOffer(ExchangeOffer offer) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO exchange_offers (vehicle_id, exchange_value, subsidy_percent, status) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, offer.getVehicleId());
            pstmt.setDouble(2, offer.getExchangeValue());
            pstmt.setDouble(3, offer.getSubsidyPercent());
            pstmt.setString(4, offer.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating exchange offer: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeStatement(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<ExchangeOffer> getOffersByVehicle(int vehicleId) {
        List<ExchangeOffer> offers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM exchange_offers WHERE vehicle_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, vehicleId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ExchangeOffer offer = new ExchangeOffer();
                offer.setOfferId(rs.getInt("offer_id"));
                offer.setVehicleId(rs.getInt("vehicle_id"));
                offer.setExchangeValue(rs.getDouble("exchange_value"));
                offer.setSubsidyPercent(rs.getDouble("subsidy_percent"));
                offer.setStatus(rs.getString("status"));
                offer.setCreatedDate(rs.getString("created_date"));
                offer.setProcessedDate(rs.getString("processed_date"));
                offer.setAdminNotes(rs.getString("admin_notes"));
                offers.add(offer);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching offers: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(pstmt);
            DatabaseConnection.closeConnection(conn);
        }

        return offers;
    }

    public List<ExchangeOffer> getAllOffers() {
        List<ExchangeOffer> offers = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT eo.*, v.plate_no, u.name as owner_name " +
                    "FROM exchange_offers eo " +
                    "JOIN vehicles v ON eo.vehicle_id = v.vehicle_id " +
                    "JOIN users u ON v.owner_id = u.user_id";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                ExchangeOffer offer = new ExchangeOffer();
                offer.setOfferId(rs.getInt("offer_id"));
                offer.setVehicleId(rs.getInt("vehicle_id"));
                offer.setExchangeValue(rs.getDouble("exchange_value"));
                offer.setSubsidyPercent(rs.getDouble("subsidy_percent"));
                offer.setStatus(rs.getString("status"));
                offer.setCreatedDate(rs.getString("created_date"));
                offer.setProcessedDate(rs.getString("processed_date"));
                offer.setAdminNotes(rs.getString("admin_notes"));
                offers.add(offer);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all offers: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(stmt);
            DatabaseConnection.closeConnection(conn);
        }

        return offers;
    }

    public boolean updateOfferStatus(int offerId, String status, String adminNotes) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE exchange_offers SET status = ?, admin_notes = ?, processed_date = CURRENT_TIMESTAMP WHERE offer_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, adminNotes);
            pstmt.setInt(3, offerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating offer status: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeStatement(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    // Statistics methods
    public int getTotalExchangedVehicles() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as total FROM exchange_offers WHERE status = 'approved'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error getting total exchanged vehicles: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(pstmt);
            DatabaseConnection.closeConnection(conn);
        }

        return 0;
    }

    public double getTotalSubsidies() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(exchange_value * subsidy_percent / 100) as total_subsidies FROM exchange_offers WHERE status = 'approved'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_subsidies");
            }

        } catch (SQLException e) {
            System.err.println("Error getting total subsidies: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(pstmt);
            DatabaseConnection.closeConnection(conn);
        }

        return 0;
    }

    public double getEstimatedCarbonReduction() {
        // Estimate: Each exchanged vehicle reduces ~2 tons of CO2 per year
        return getTotalExchangedVehicles() * 2.0;
    }
}