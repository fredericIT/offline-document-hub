// Enhanced CitizenDashboard.java
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class CitizenDashboard extends Frame {
    private User currentUser;
    private VehicleDAO vehicleDAO;
    private ExchangeOfferDAO offerDAO;
    private TextArea vehicleListArea, offerListArea;
    private Panel mainPanel;
    private CardLayout cardLayout;

    public CitizenDashboard(User user) {
        this.currentUser = user;
        vehicleDAO = new VehicleDAO();
        offerDAO = new ExchangeOfferDAO();
        initializeUI();
        applyCitizenDashboardStyling();
        addLogoutButton();
        loadData();
    }

    private void initializeUI() {
        setTitle("GVEI - Citizen Dashboard - Welcome " + currentUser.getName());
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Use CardLayout for different views
        cardLayout = new CardLayout();
        mainPanel = new Panel(cardLayout);

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu vehicleMenu = new Menu("Vehicles");
        MenuItem registerVehicleItem = new MenuItem("Register Vehicle");
        MenuItem viewVehiclesItem = new MenuItem("View My Vehicles");
        MenuItem viewVehiclesTableItem = new MenuItem("View My Vehicles (Table)");

        Menu exchangeMenu = new Menu("Exchange");
        MenuItem applyExchangeItem = new MenuItem("Apply for Exchange");
        MenuItem viewOffersItem = new MenuItem("View My Offers");
        MenuItem viewOffersTableItem = new MenuItem("View My Offers (Table)");

        vehicleMenu.add(registerVehicleItem);
        vehicleMenu.add(viewVehiclesItem);
        vehicleMenu.add(viewVehiclesTableItem);

        exchangeMenu.add(applyExchangeItem);
        exchangeMenu.add(viewOffersItem);
        exchangeMenu.add(viewOffersTableItem);

        menuBar.add(vehicleMenu);
        menuBar.add(exchangeMenu);
        setMenuBar(menuBar);

        // Create different views
        createTextView();
        createVehicleTableView();
        createOffersTableView();

        add(mainPanel, BorderLayout.CENTER);

        // Event handlers
        registerVehicleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VehicleRegistrationForm(currentUser).setVisible(true);
            }
        });

        viewVehiclesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "TEXT_VIEW");
                loadVehicleData();
            }
        });

        viewVehiclesTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "VEHICLE_TABLE");
                loadVehicleTableData();
            }
        });

        applyExchangeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ExchangeOfferForm(currentUser).setVisible(true);
            }
        });

        viewOffersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "TEXT_VIEW");
                loadOfferData();
            }
        });

        viewOffersTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "OFFERS_TABLE");
                loadOffersTableData();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                logout();
            }
        });
    }

    private void createTextView() {
        Panel textPanel = new Panel(new GridLayout(2, 1));

        // Vehicles panel
        Panel vehiclePanel = new Panel(new BorderLayout());
        vehiclePanel.add(EnhancedStyleUtils.createLabel("My Vehicles:",
                EnhancedStyleUtils.HEADER_FONT, EnhancedStyleUtils.PRIMARY_COLOR), BorderLayout.NORTH);
        vehicleListArea = new TextArea(15, 80);
        vehicleListArea.setEditable(false);
        // FIXED: Create ScrollPane correctly
        ScrollPane vehicleScrollPane = new ScrollPane();
        vehicleScrollPane.add(vehicleListArea);
        vehiclePanel.add(vehicleScrollPane, BorderLayout.CENTER);

        // Offers panel
        Panel offerPanel = new Panel(new BorderLayout());
        offerPanel.add(EnhancedStyleUtils.createLabel("My Exchange Offers:",
                EnhancedStyleUtils.HEADER_FONT, EnhancedStyleUtils.PRIMARY_COLOR), BorderLayout.NORTH);
        offerListArea = new TextArea(15, 80);
        offerListArea.setEditable(false);
        // FIXED: Create ScrollPane correctly
        ScrollPane offerScrollPane = new ScrollPane();
        offerScrollPane.add(offerListArea);
        offerPanel.add(offerScrollPane, BorderLayout.CENTER);

        textPanel.add(vehiclePanel);
        textPanel.add(offerPanel);

        mainPanel.add(textPanel, "TEXT_VIEW");
    }

    private void createVehicleTableView() {
        Panel tablePanel = new Panel(new BorderLayout());
        tablePanel.add(EnhancedStyleUtils.createLabel("My Vehicles - Table View",
                EnhancedStyleUtils.HEADER_FONT, EnhancedStyleUtils.PRIMARY_COLOR), BorderLayout.NORTH);

        // Search and filter panel
        Panel searchPanel = createVehicleSearchPanel();
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Table will be added when data is loaded
        mainPanel.add(tablePanel, "VEHICLE_TABLE");
    }

    private void createOffersTableView() {
        Panel tablePanel = new Panel(new BorderLayout());
        tablePanel.add(EnhancedStyleUtils.createLabel("My Exchange Offers - Table View",
                EnhancedStyleUtils.HEADER_FONT, EnhancedStyleUtils.PRIMARY_COLOR), BorderLayout.NORTH);

        // Search and filter panel
        Panel searchPanel = createOffersSearchPanel();
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        mainPanel.add(tablePanel, "OFFERS_TABLE");
    }

    private Panel createVehicleSearchPanel() {
        // FIXED: Call createSearchPanel with correct number of arguments
        return EnhancedStyleUtils.createSearchPanel("Search vehicles...");
    }

    private Panel createOffersSearchPanel() {
        // FIXED: Call createSearchPanel with correct number of arguments
        return EnhancedStyleUtils.createSearchPanel("Search offers...");
    }

    private void loadVehicleTableData() {
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUser.getUserId());

        String[] headers = {"Plate No", "Type", "Fuel Type", "Year", "Mileage", "Condition", "Eligibility"};
        String[][] data = new String[vehicles.size()][7];

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            boolean eligible = vehicleDAO.isEligibleForExchange(vehicle);

            data[i][0] = vehicle.getPlateNo();
            data[i][1] = vehicle.getVehicleType();
            data[i][2] = vehicle.getFuelType();
            data[i][3] = String.valueOf(vehicle.getManufactureYear());
            data[i][4] = String.valueOf(vehicle.getMileage()) + " km";
            data[i][5] = String.valueOf(vehicle.getConditionRating()) + "/10";
            data[i][6] = eligible ? "ELIGIBLE" : "NOT ELIGIBLE";
        }

        // Update the table view
        Panel tablePanel = (Panel) mainPanel.getComponent(1); // VEHICLE_TABLE panel
        // FIXED: Check if component exists before removing
        if (tablePanel.getComponentCount() > 1) {
            tablePanel.remove(1); // Remove old table
        }
        tablePanel.add(EnhancedStyleUtils.createTablePanel(headers, data), BorderLayout.CENTER);
        tablePanel.validate();
        tablePanel.repaint();
    }

    private void loadOffersTableData() {
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUser.getUserId());
        List<ExchangeOffer> allOffers = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            allOffers.addAll(offerDAO.getOffersByVehicle(vehicle.getVehicleId()));
        }

        String[] headers = {"Offer ID", "Vehicle Plate", "Exchange Value", "Subsidy %", "Total Subsidy", "Status"};
        String[][] data = new String[allOffers.size()][6];

        for (int i = 0; i < allOffers.size(); i++) {
            ExchangeOffer offer = allOffers.get(i);
            Vehicle vehicle = getVehicleById(offer.getVehicleId());

            data[i][0] = String.valueOf(offer.getOfferId());
            data[i][1] = vehicle != null ? vehicle.getPlateNo() : "N/A";
            data[i][2] = String.format("$%.2f", offer.getExchangeValue());
            data[i][3] = String.format("%.1f%%", offer.getSubsidyPercent());
            data[i][4] = String.format("$%.2f", offer.getExchangeValue() * offer.getSubsidyPercent() / 100);
            data[i][5] = offer.getStatus().toUpperCase();
        }

        // Update the table view
        Panel tablePanel = (Panel) mainPanel.getComponent(2); // OFFERS_TABLE panel
        // FIXED: Check if component exists before removing
        if (tablePanel.getComponentCount() > 1) {
            tablePanel.remove(1); // Remove old table
        }
        tablePanel.add(EnhancedStyleUtils.createTablePanel(headers, data), BorderLayout.CENTER);
        tablePanel.validate();
        tablePanel.repaint();
    }

    private Vehicle getVehicleById(int vehicleId) {
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUser.getUserId());
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }

    private void applyCitizenDashboardStyling() {
        setBackground(EnhancedStyleUtils.BACKGROUND_COLOR);

        MenuBar menuBar = getMenuBar();
        if (menuBar != null) {
            menuBar.setFont(EnhancedStyleUtils.BODY_FONT);
            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                Menu menu = menuBar.getMenu(i);
                menu.setFont(EnhancedStyleUtils.SUBHEADER_FONT);
                for (int j = 0; j < menu.getItemCount(); j++) {
                    MenuItem item = menu.getItem(j);
                    item.setFont(EnhancedStyleUtils.BODY_FONT);
                }
            }
        }
    }

    private void addLogoutButton() {
        Panel headerPanel = new Panel(new BorderLayout());
        headerPanel.setBackground(EnhancedStyleUtils.PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(1000, 60));

        Label titleLabel = EnhancedStyleUtils.createLabel("GVEI - Welcome " + currentUser.getName(),
                EnhancedStyleUtils.TITLE_FONT, EnhancedStyleUtils.TEXT_LIGHT);
        titleLabel.setAlignment(Label.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        Panel logoutPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(EnhancedStyleUtils.PRIMARY_COLOR);
        Button logoutBtn = EnhancedStyleUtils.createButton("Logout", EnhancedStyleUtils.DANGER_COLOR);
        logoutBtn.addActionListener(e -> logout());
        logoutPanel.add(logoutBtn);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void logout() {
        this.setVisible(false);
        new MainMenu().setVisible(true);
    }

    private void loadData() {
        loadVehicleData();
        loadOfferData();
    }

    private void loadVehicleData() {
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUser.getUserId());
        StringBuilder sb = new StringBuilder();

        if (vehicles.isEmpty()) {
            sb.append("No vehicles registered.\n");
        } else {
            for (Vehicle vehicle : vehicles) {
                sb.append(String.format("Plate: %s, Type: %s, Fuel: %s, Year: %d, Mileage: %dkm, Condition: %d/10\n",
                        vehicle.getPlateNo(), vehicle.getVehicleType(), vehicle.getFuelType(),
                        vehicle.getManufactureYear(), vehicle.getMileage(), vehicle.getConditionRating()));

                boolean eligible = vehicleDAO.isEligibleForExchange(vehicle);
                sb.append("  Eligibility: ").append(eligible ? "ELIGIBLE" : "NOT ELIGIBLE").append("\n\n");
            }
        }

        vehicleListArea.setText(sb.toString());
    }

    private void loadOfferData() {
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUser.getUserId());
        StringBuilder sb = new StringBuilder();

        if (vehicles.isEmpty()) {
            sb.append("No vehicles to check offers.\n");
        } else {
            for (Vehicle vehicle : vehicles) {
                List<ExchangeOffer> offers = offerDAO.getOffersByVehicle(vehicle.getVehicleId());
                if (!offers.isEmpty()) {
                    sb.append("Vehicle: ").append(vehicle.getPlateNo()).append("\n");
                    for (ExchangeOffer offer : offers) {
                        sb.append(String.format("  Offer #%d: Value: $%.2f, Subsidy: %.1f%%, Status: %s\n",
                                offer.getOfferId(), offer.getExchangeValue(),
                                offer.getSubsidyPercent(), offer.getStatus()));
                    }
                    sb.append("\n");
                }
            }
        }

        if (sb.length() == 0) {
            sb.append("No exchange offers found.\n");
        }

        offerListArea.setText(sb.toString());
    }
}