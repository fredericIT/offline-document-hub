// AdminDashboard.java
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class AdminDashboard extends Frame {
    private User currentUser;
    private VehicleDAO vehicleDAO;
    private ExchangeOfferDAO offerDAO;
    private UserDAO userDAO;
    private TextArea reportArea;
    private Panel mainPanel;
    private CardLayout cardLayout;

    public AdminDashboard(User user) {
        this.currentUser = user;
        vehicleDAO = new VehicleDAO();
        offerDAO = new ExchangeOfferDAO();
        userDAO = new UserDAO();
        initializeUI();
        applyAdminDashboardStyling();
        addLogoutButton();
        loadStatistics();
    }

    private void initializeUI() {
        setTitle("GVEI - Admin Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Use CardLayout for different views
        cardLayout = new CardLayout();
        mainPanel = new Panel(cardLayout);

        // Enhanced Menu bar
        MenuBar menuBar = new MenuBar();

        Menu viewMenu = new Menu("View Data");
        MenuItem viewUsersItem = new MenuItem("View All Users");
        MenuItem viewUsersTableItem = new MenuItem("View All Users (Table)");
        MenuItem viewVehiclesItem = new MenuItem("View All Vehicles");
        MenuItem viewVehiclesTableItem = new MenuItem("View All Vehicles (Table)");
        MenuItem viewOffersItem = new MenuItem("View All Offers");
        MenuItem viewOffersTableItem = new MenuItem("View All Offers (Table)");

        Menu reportsMenu = new Menu("Reports & Charts");
        MenuItem statisticsItem = new MenuItem("View Statistics");
        MenuItem chartsItem = new MenuItem("View Charts");
        MenuItem exportItem = new MenuItem("Export Report");

        Menu manageMenu = new Menu("Manage");
        MenuItem manageOffersItem = new MenuItem("Manage Exchange Offers");

        viewMenu.add(viewUsersItem);
        viewMenu.add(viewUsersTableItem);
        viewMenu.addSeparator();
        viewMenu.add(viewVehiclesItem);
        viewMenu.add(viewVehiclesTableItem);
        viewMenu.addSeparator();
        viewMenu.add(viewOffersItem);
        viewMenu.add(viewOffersTableItem);

        reportsMenu.add(statisticsItem);
        reportsMenu.add(chartsItem);
        reportsMenu.add(exportItem);

        manageMenu.add(manageOffersItem);

        menuBar.add(viewMenu);
        menuBar.add(reportsMenu);
        menuBar.add(manageMenu);
        setMenuBar(menuBar);

        // Create different views
        createStatisticsView();
        createUsersTableView();
        createVehiclesTableView();
        createOffersTableView();
        createChartsView();

        add(mainPanel, BorderLayout.CENTER);

        // Event handlers
        viewUsersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "STATISTICS");
                loadUsers();
            }
        });

        viewUsersTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "USERS_TABLE");
                loadUsersTableData();
            }
        });

        viewVehiclesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "STATISTICS");
                loadVehicles();
            }
        });

        viewVehiclesTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "VEHICLES_TABLE");
                loadVehiclesTableData();
            }
        });

        viewOffersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "STATISTICS");
                loadOffers();
            }
        });

        viewOffersTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "OFFERS_TABLE");
                loadOffersTableData();
            }
        });

        statisticsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "STATISTICS");
                loadStatistics();
            }
        });

        chartsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "CHARTS");
                loadCharts();
            }
        });

        exportItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportReport();
            }
        });

        manageOffersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OfferManagementForm().setVisible(true);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                logout();
            }
        });
    }

    private void createStatisticsView() {
        reportArea = new TextArea(20, 80);
        reportArea.setEditable(false);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(reportArea);
        mainPanel.add(scrollPane, "STATISTICS");
    }

    private void createUsersTableView() {
        Panel tablePanel = new Panel(new BorderLayout());
        tablePanel.add(EnhancedStyleUtils.createLabel("All Registered Users",
                EnhancedStyleUtils.HEADER_FONT, EnhancedStyleUtils.PRIMARY_COLOR), BorderLayout.NORTH);

        // Create search components and add listeners
        EnhancedStyleUtils.SearchComponents searchComps = EnhancedStyleUtils.createSearchComponents("Search users...");
        searchComps.searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUsersTableData();
            }
        });
        searchComps.clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchComps.searchField.setText("");
                loadUsersTableData();
            }
        });
        tablePanel.add(searchComps.panel, BorderLayout.NORTH);

        mainPanel.add(tablePanel, "USERS_TABLE");
    }

    private void createVehiclesTableView() {
        Panel tablePanel = new Panel(new BorderLayout());
        tablePanel.add(EnhancedStyleUtils.createLabel("All Registered Vehicles",
                EnhancedStyleUtils.HEADER_FONT, EnhancedStyleUtils.PRIMARY_COLOR), BorderLayout.NORTH);

        // Create search components and add listeners
        EnhancedStyleUtils.SearchComponents searchComps = EnhancedStyleUtils.createSearchComponents("Search vehicles...");
        searchComps.searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadVehiclesTableData();
            }
        });
        searchComps.clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchComps.searchField.setText("");
                loadVehiclesTableData();
            }
        });
        tablePanel.add(searchComps.panel, BorderLayout.NORTH);

        mainPanel.add(tablePanel, "VEHICLES_TABLE");
    }

    private void createOffersTableView() {
        Panel tablePanel = new Panel(new BorderLayout());
        tablePanel.add(EnhancedStyleUtils.createLabel("All Exchange Offers",
                EnhancedStyleUtils.HEADER_FONT, EnhancedStyleUtils.PRIMARY_COLOR), BorderLayout.NORTH);

        String[] filterOptions = {"All", "Pending", "Approved", "Rejected"};

        // Create search components and add listeners
        EnhancedStyleUtils.SearchComponents searchComps = EnhancedStyleUtils.createSearchComponents("Search offers...");
        searchComps.searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadOffersTableData();
            }
        });
        searchComps.clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchComps.searchField.setText("");
                loadOffersTableData();
            }
        });

        // Create filter components and add item listener for Choice
        EnhancedStyleUtils.FilterComponents filterComps = EnhancedStyleUtils.createFilterComponents(filterOptions);
        filterComps.filterChoice.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    loadOffersTableData();
                }
            }
        });

        Panel topPanel = new Panel(new GridLayout(2, 1));
        topPanel.add(searchComps.panel);
        topPanel.add(filterComps.panel);
        tablePanel.add(topPanel, BorderLayout.NORTH);

        mainPanel.add(tablePanel, "OFFERS_TABLE");
    }

    private void createChartsView() {
        Panel chartsPanel = new Panel(new GridLayout(2, 2, 10, 10));
        chartsPanel.setBackground(EnhancedStyleUtils.BACKGROUND_COLOR);
        mainPanel.add(chartsPanel, "CHARTS");
    }

    // Table data loading methods
    private void loadUsersTableData() {
        List<User> users = userDAO.getAllUsers();

        String[] headers = {"User ID", "Name", "Email", "Role", "Registered Date"};
        String[][] data = new String[users.size()][5];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = String.valueOf(user.getUserId());
            data[i][1] = user.getName();
            data[i][2] = user.getEmail();
            data[i][3] = user.getRole().toUpperCase();
            data[i][4] = "2024"; // You can add registration date to your User model
        }

        updateTableView("USERS_TABLE", headers, data);
    }

    private void loadVehiclesTableData() {
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();

        String[] headers = {"Vehicle ID", "Plate No", "Type", "Fuel Type", "Year", "Mileage", "Condition", "Owner ID"};
        String[][] data = new String[vehicles.size()][8];

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            data[i][0] = String.valueOf(vehicle.getVehicleId());
            data[i][1] = vehicle.getPlateNo();
            data[i][2] = vehicle.getVehicleType();
            data[i][3] = vehicle.getFuelType();
            data[i][4] = String.valueOf(vehicle.getManufactureYear());
            data[i][5] = String.valueOf(vehicle.getMileage()) + " km";
            data[i][6] = String.valueOf(vehicle.getConditionRating()) + "/10";
            data[i][7] = String.valueOf(vehicle.getOwnerId());
        }

        updateTableView("VEHICLES_TABLE", headers, data);
    }

    private void loadOffersTableData() {
        List<ExchangeOffer> offers = offerDAO.getAllOffers();

        String[] headers = {"Offer ID", "Vehicle ID", "Exchange Value", "Subsidy %", "Total Subsidy", "Status", "Created Date"};
        String[][] data = new String[offers.size()][7];

        for (int i = 0; i < offers.size(); i++) {
            ExchangeOffer offer = offers.get(i);
            data[i][0] = String.valueOf(offer.getOfferId());
            data[i][1] = String.valueOf(offer.getVehicleId());
            data[i][2] = String.format("$%.2f", offer.getExchangeValue());
            data[i][3] = String.format("%.1f%%", offer.getSubsidyPercent());
            data[i][4] = String.format("$%.2f", offer.getExchangeValue() * offer.getSubsidyPercent() / 100);
            data[i][5] = offer.getStatus().toUpperCase();
            data[i][6] = offer.getCreatedDate() != null ? offer.getCreatedDate() : "N/A";
        }

        updateTableView("OFFERS_TABLE", headers, data);
    }

    private void updateTableView(String viewName, String[] headers, String[][] data) {
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof Panel) {
                Panel panel = (Panel) comp;
                // Remove old table if exists
                if (panel.getComponentCount() > 1) {
                    Component centerComponent = ((BorderLayout)panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (centerComponent != null) {
                        panel.remove(centerComponent);
                    }
                }

                // Add new table
                Panel tablePanel = EnhancedStyleUtils.createTablePanel(headers, data);
                panel.add(tablePanel, BorderLayout.CENTER);
                panel.validate();
                panel.repaint();
                break;
            }
        }
    }

    private void loadCharts() {
        Panel chartsPanel = (Panel) mainPanel.getComponent(4); // CHARTS panel
        chartsPanel.removeAll();

        // Vehicle Type Distribution Chart
        Map<String, Integer> vehicleTypeData = new HashMap<>();
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
        for (Vehicle vehicle : vehicles) {
            vehicleTypeData.put(vehicle.getVehicleType(),
                    vehicleTypeData.getOrDefault(vehicle.getVehicleType(), 0) + 1);
        }
        chartsPanel.add(EnhancedStyleUtils.createChartPanel("Vehicle Types Distribution", vehicleTypeData));

        // Offer Status Distribution Chart
        Map<String, Integer> offerStatusData = new HashMap<>();
        List<ExchangeOffer> offers = offerDAO.getAllOffers();
        for (ExchangeOffer offer : offers) {
            offerStatusData.put(offer.getStatus(),
                    offerStatusData.getOrDefault(offer.getStatus(), 0) + 1);
        }
        chartsPanel.add(EnhancedStyleUtils.createChartPanel("Offer Status Distribution", offerStatusData));

        // Fuel Type Distribution Chart
        Map<String, Integer> fuelTypeData = new HashMap<>();
        for (Vehicle vehicle : vehicles) {
            fuelTypeData.put(vehicle.getFuelType(),
                    fuelTypeData.getOrDefault(vehicle.getFuelType(), 0) + 1);
        }
        chartsPanel.add(EnhancedStyleUtils.createChartPanel("Fuel Types Distribution", fuelTypeData));

        // User Role Distribution Chart
        Map<String, Integer> userRoleData = new HashMap<>();
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            userRoleData.put(user.getRole(),
                    userRoleData.getOrDefault(user.getRole(), 0) + 1);
        }
        chartsPanel.add(EnhancedStyleUtils.createChartPanel("User Roles Distribution", userRoleData));

        chartsPanel.validate();
        chartsPanel.repaint();
    }

    private void applyAdminDashboardStyling() {
        setBackground(EnhancedStyleUtils.BACKGROUND_COLOR);

        // Style menu bar
        MenuBar menuBar = getMenuBar();
        if (menuBar != null) {
            menuBar.setFont(EnhancedStyleUtils.BODY_FONT);
            // Style menus
            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                Menu menu = menuBar.getMenu(i);
                menu.setFont(EnhancedStyleUtils.SUBHEADER_FONT);
                // Style menu items
                for (int j = 0; j < menu.getItemCount(); j++) {
                    MenuItem item = menu.getItem(j);
                    item.setFont(EnhancedStyleUtils.BODY_FONT);
                }
            }
        }

        // Style report area
        if (reportArea != null) {
            reportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            reportArea.setBackground(EnhancedStyleUtils.CARD_COLOR);
            reportArea.setForeground(EnhancedStyleUtils.TEXT_COLOR);
        }
    }

    private void addLogoutButton() {
        Panel headerPanel = new Panel(new BorderLayout());
        headerPanel.setBackground(EnhancedStyleUtils.PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(1200, 60));

        // Title
        Label titleLabel = EnhancedStyleUtils.createLabel("GVEI - Admin Dashboard - Welcome " + currentUser.getName(),
                EnhancedStyleUtils.TITLE_FONT, EnhancedStyleUtils.TEXT_LIGHT);
        titleLabel.setAlignment(Label.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Logout button
        Panel logoutPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(EnhancedStyleUtils.PRIMARY_COLOR);
        Button logoutBtn = EnhancedStyleUtils.createButton("Logout", EnhancedStyleUtils.DANGER_COLOR);
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        logoutPanel.add(logoutBtn);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void logout() {
        this.setVisible(false);
        new MainMenu().setVisible(true);
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers();
        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL REGISTERED USERS ===\n\n");

        for (User user : users) {
            sb.append(String.format("ID: %d, Name: %s, Email: %s, Role: %s\n",
                    user.getUserId(), user.getName(), user.getEmail(), user.getRole()));
        }

        sb.append("\nTotal Users: ").append(users.size());
        reportArea.setText(sb.toString());
    }

    private void loadVehicles() {
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL REGISTERED VEHICLES ===\n\n");

        for (Vehicle vehicle : vehicles) {
            sb.append(String.format("Plate: %s, Type: %s, Fuel: %s, Year: %d, Mileage: %dkm\n",
                    vehicle.getPlateNo(), vehicle.getVehicleType(), vehicle.getFuelType(),
                    vehicle.getManufactureYear(), vehicle.getMileage()));
        }

        sb.append("\nTotal Vehicles: ").append(vehicles.size());
        reportArea.setText(sb.toString());
    }

    private void loadOffers() {
        List<ExchangeOffer> offers = offerDAO.getAllOffers();
        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL EXCHANGE OFFERS ===\n\n");

        for (ExchangeOffer offer : offers) {
            sb.append(String.format("Offer #%d: Vehicle ID: %d, Value: $%.2f, Subsidy: %.1f%%, Status: %s\n",
                    offer.getOfferId(), offer.getVehicleId(), offer.getExchangeValue(),
                    offer.getSubsidyPercent(), offer.getStatus()));
        }

        sb.append("\nTotal Offers: ").append(offers.size());
        reportArea.setText(sb.toString());
    }

    private void loadStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GVEI STATISTICS DASHBOARD ===\n\n");

        int totalExchanged = offerDAO.getTotalExchangedVehicles();
        double totalSubsidies = offerDAO.getTotalSubsidies();
        double carbonReduction = offerDAO.getEstimatedCarbonReduction();

        sb.append("Total Exchanged Vehicles: ").append(totalExchanged).append("\n");
        sb.append("Total Subsidies Provided: $").append(String.format("%.2f", totalSubsidies)).append("\n");
        sb.append("Estimated Carbon Reduction: ").append(String.format("%.1f", carbonReduction)).append(" tons CO2/year\n");

        // Additional statistics
        List<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
        long eligibleCount = allVehicles.stream()
                .filter(vehicleDAO::isEligibleForExchange)
                .count();

        sb.append("\nTotal Registered Vehicles: ").append(allVehicles.size()).append("\n");
        sb.append("Eligible Vehicles: ").append(eligibleCount).append("\n");
        sb.append("Non-Eligible Vehicles: ").append(allVehicles.size() - eligibleCount).append("\n");

        List<ExchangeOffer> allOffers = offerDAO.getAllOffers();
        long pendingCount = allOffers.stream().filter(o -> "pending".equals(o.getStatus())).count();
        long approvedCount = allOffers.stream().filter(o -> "approved".equals(o.getStatus())).count();
        long rejectedCount = allOffers.stream().filter(o -> "rejected".equals(o.getStatus())).count();

        sb.append("\nExchange Offer Status:\n");
        sb.append("  Pending: ").append(pendingCount).append("\n");
        sb.append("  Approved: ").append(approvedCount).append("\n");
        sb.append("  Rejected: ").append(rejectedCount).append("\n");

        reportArea.setText(sb.toString());
    }

    private void exportReport() {
        FileDialog fileDialog = new FileDialog(this, "Export Report", FileDialog.SAVE);
        fileDialog.setFile("gvei_report.txt");
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        if (directory != null && filename != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(directory + filename))) {
                writer.write(reportArea.getText());

                // Show success message
                Dialog successDialog = new Dialog(this, "Success", true);
                successDialog.setLayout(new FlowLayout());
                successDialog.setSize(300, 100);
                successDialog.setLocationRelativeTo(this);
                successDialog.add(new Label("Report exported successfully to: " + filename));
                Button okButton = new Button("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        successDialog.dispose();
                    }
                });
                successDialog.add(okButton);
                successDialog.setVisible(true);

            } catch (IOException e) {
                System.err.println("Error exporting report: " + e.getMessage());

                // Show error message
                Dialog errorDialog = new Dialog(this, "Error", true);
                errorDialog.setLayout(new FlowLayout());
                errorDialog.setSize(300, 100);
                errorDialog.setLocationRelativeTo(this);
                errorDialog.add(new Label("Error exporting report: " + e.getMessage()));
                Button okButton = new Button("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        errorDialog.dispose();
                    }
                });
                errorDialog.add(okButton);
                errorDialog.setVisible(true);
            }
        }
    }

    // Inner class for offer management
    class OfferManagementForm extends Frame {
        private List<ExchangeOffer> offers;
        private Choice offerChoice;
        private Choice statusChoice;
        private TextArea notesArea;
        private Button updateButton;

        public OfferManagementForm() {
            super("Manage Exchange Offers");
            initializeUI();
            applyOfferManagementStyling();
        }

        private void initializeUI() {
            setSize(500, 400);
            setLayout(new GridLayout(7, 2, 10, 10));
            setLocationRelativeTo(AdminDashboard.this);

            // Row 1: Select Offer
            add(new Label("Select Offer:"));
            offerChoice = new Choice();
            add(offerChoice);

            // Row 2: New Status
            add(new Label("New Status:"));
            statusChoice = new Choice();
            statusChoice.add("pending");
            statusChoice.add("approved");
            statusChoice.add("rejected");
            add(statusChoice);

            // Row 3: Admin Notes Label
            add(new Label("Admin Notes:"));
            add(new Label(""));

            // Row 4-5: Notes TextArea (span 2 rows)
            notesArea = new TextArea(3, 30);
            Panel notesPanel = new Panel(new BorderLayout());
            notesPanel.add(notesArea, BorderLayout.CENTER);
            add(notesPanel);
            add(new Label(""));

            // Row 6: Empty row for spacing
            add(new Label(""));
            add(new Label(""));

            // Row 7: Update Button
            updateButton = new Button("Update Offer");
            Panel buttonPanel = new Panel(new FlowLayout());
            buttonPanel.add(updateButton);
            add(buttonPanel);
            add(new Label(""));

            // Load offers after UI is initialized
            loadOffers();

            // Event handlers
            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateOffer();
                }
            });

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    dispose();
                }
            });
        }

        private void applyOfferManagementStyling() {
            setBackground(EnhancedStyleUtils.BACKGROUND_COLOR);

            // Style all components
            Component[] components = getComponents();
            for (Component comp : components) {
                if (comp instanceof Label) {
                    Label label = (Label) comp;
                    if (!label.getText().isEmpty()) {
                        label.setFont(EnhancedStyleUtils.SUBHEADER_FONT);
                        label.setForeground(EnhancedStyleUtils.PRIMARY_COLOR);
                    }
                } else if (comp instanceof Choice) {
                    comp.setFont(EnhancedStyleUtils.BODY_FONT);
                    comp.setBackground(EnhancedStyleUtils.CARD_COLOR);
                } else if (comp instanceof TextArea) {
                    TextArea area = (TextArea) comp;
                    area.setFont(EnhancedStyleUtils.BODY_FONT);
                    area.setBackground(EnhancedStyleUtils.CARD_COLOR);
                    area.setForeground(EnhancedStyleUtils.TEXT_COLOR);
                } else if (comp instanceof Button) {
                    Button btn = (Button) comp;
                    btn.setBackground(EnhancedStyleUtils.ACCENT_COLOR);
                    btn.setForeground(EnhancedStyleUtils.TEXT_LIGHT);
                    btn.setFont(EnhancedStyleUtils.BUTTON_FONT);
                }
            }

            // Style update button specifically
            if (updateButton != null) {
                updateButton.setBackground(EnhancedStyleUtils.ACCENT_COLOR);
                updateButton.setForeground(EnhancedStyleUtils.TEXT_LIGHT);
                updateButton.setFont(EnhancedStyleUtils.BUTTON_FONT);
            }
        }

        private void loadOffers() {
            offers = offerDAO.getAllOffers();
            offerChoice.removeAll();

            for (ExchangeOffer offer : offers) {
                offerChoice.add("Offer #" + offer.getOfferId() + " - Vehicle " + offer.getVehicleId() +
                        " - " + offer.getStatus());
            }

            // If no offers, disable update button
            if (offers.isEmpty()) {
                updateButton.setEnabled(false);
                offerChoice.add("No offers available");
            } else {
                updateButton.setEnabled(true);
            }
        }

        private void updateOffer() {
            if (offers.isEmpty()) {
                showMessage("No offers available to update!", "Error");
                return;
            }

            int selectedIndex = offerChoice.getSelectedIndex();
            if (selectedIndex < 0 || selectedIndex >= offers.size()) {
                showMessage("Please select a valid offer!", "Error");
                return;
            }

            ExchangeOffer selectedOffer = offers.get(selectedIndex);
            String newStatus = statusChoice.getSelectedItem();
            String notes = notesArea.getText().trim();

            if (offerDAO.updateOfferStatus(selectedOffer.getOfferId(), newStatus, notes)) {
                showMessage("Offer updated successfully!", "Success");
                loadOffers(); // Refresh the list
                notesArea.setText(""); // Clear notes
            } else {
                showMessage("Failed to update offer!", "Error");
            }
        }

        private void showMessage(String message, String title) {
            Dialog dialog = new Dialog(this, title, true);
            dialog.setLayout(new FlowLayout());
            dialog.setSize(300, 100);
            dialog.setLocationRelativeTo(this);
            dialog.setBackground(EnhancedStyleUtils.BACKGROUND_COLOR);

            Label messageLabel = new Label(message);
            messageLabel.setFont(EnhancedStyleUtils.SUBHEADER_FONT);
            messageLabel.setForeground(EnhancedStyleUtils.TEXT_COLOR);
            dialog.add(messageLabel);

            Button okButton = EnhancedStyleUtils.createButton("OK", EnhancedStyleUtils.ACCENT_COLOR);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            dialog.add(okButton);
            dialog.setVisible(true);
        }
    }
}