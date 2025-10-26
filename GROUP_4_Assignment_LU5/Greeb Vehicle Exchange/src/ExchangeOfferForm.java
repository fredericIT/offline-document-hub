// ExchangeOfferForm.java
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ExchangeOfferForm extends Frame {
    private Choice vehicleChoice;
    private Label eligibilityLabel, valueLabel, subsidyLabel, totalLabel, titleLabel;
    private Button checkButton, applyButton, cancelButton;
    private VehicleDAO vehicleDAO;
    private ExchangeOfferDAO offerDAO;
    private User currentUser;
    private Vehicle selectedVehicle;

    public ExchangeOfferForm(User user) {
        this.currentUser = user;
        vehicleDAO = new VehicleDAO();
        offerDAO = new ExchangeOfferDAO();
        initializeUI();
        applyExchangeFormStyling();
        loadUserVehicles();
    }

    private void initializeUI() {
        setTitle("GVEI - Exchange Offer");
        setSize(550, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Header Panel
        Panel headerPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(StyleUtils.PRIMARY_COLOR);
        titleLabel = new Label("Vehicle Exchange Application");
        titleLabel.setFont(StyleUtils.HEADER_FONT);
        titleLabel.setForeground(StyleUtils.TEXT_LIGHT);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main form panel
        Panel formPanel = new Panel(new GridLayout(9, 2, 15, 15));
        formPanel.setBackground(StyleUtils.BACKGROUND_COLOR);

        // Vehicle Selection
        formPanel.add(createFormLabel("Select Vehicle:"));
        vehicleChoice = new Choice();
        vehicleChoice.setFont(StyleUtils.BODY_FONT);
        formPanel.add(vehicleChoice);

        // Check Eligibility Button
        formPanel.add(new Label("")); // Empty cell
        checkButton = new Button("Check Eligibility");
        formPanel.add(checkButton);

        // Eligibility Result
        formPanel.add(createFormLabel("Eligibility Status:"));
        eligibilityLabel = createResultLabel("-");
        formPanel.add(eligibilityLabel);

        // Exchange Value
        formPanel.add(createFormLabel("Exchange Value:"));
        valueLabel = createResultLabel("-");
        formPanel.add(valueLabel);

        // Subsidy Percentage
        formPanel.add(createFormLabel("Subsidy Percentage:"));
        subsidyLabel = createResultLabel("-");
        formPanel.add(subsidyLabel);

        // Total Subsidy
        formPanel.add(createFormLabel("Total Subsidy:"));
        totalLabel = createResultLabel("-");
        formPanel.add(totalLabel);

        // Buttons
        formPanel.add(new Label("")); // Empty cell
        Panel actionPanel = new Panel(new FlowLayout());
        actionPanel.setBackground(StyleUtils.BACKGROUND_COLOR);
        applyButton = new Button("Apply for Exchange");
        cancelButton = new Button("Cancel");

        applyButton.setEnabled(false);

        actionPanel.add(applyButton);
        actionPanel.add(cancelButton);
        formPanel.add(actionPanel);

        add(formPanel, BorderLayout.CENTER);

        // Event handlers
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkEligibility();
            }
        });

        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyForExchange();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    private void applyExchangeFormStyling() {
        setBackground(StyleUtils.BACKGROUND_COLOR);

        // Style choice component
        if (vehicleChoice != null) {
            vehicleChoice.setFont(StyleUtils.BODY_FONT);
            vehicleChoice.setBackground(StyleUtils.CARD_COLOR);
        }

        // Style buttons
        if (checkButton != null) {
            checkButton.setBackground(StyleUtils.SECONDARY_COLOR);
            checkButton.setForeground(StyleUtils.TEXT_LIGHT);
            checkButton.setFont(StyleUtils.BUTTON_FONT);
            checkButton.setPreferredSize(new Dimension(150, 35));
        }

        if (applyButton != null) {
            applyButton.setBackground(StyleUtils.ACCENT_COLOR);
            applyButton.setForeground(StyleUtils.TEXT_LIGHT);
            applyButton.setFont(StyleUtils.BUTTON_FONT);
            applyButton.setPreferredSize(new Dimension(150, 35));
        }

        if (cancelButton != null) {
            cancelButton.setBackground(StyleUtils.DANGER_COLOR);
            cancelButton.setForeground(StyleUtils.TEXT_LIGHT);
            cancelButton.setFont(StyleUtils.BUTTON_FONT);
            cancelButton.setPreferredSize(new Dimension(100, 35));
        }

        // Add hover effects to buttons
        addButtonHoverEffects();
    }

    private void addButtonHoverEffects() {
        // Check button hover effect
        checkButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (checkButton.isEnabled()) {
                    checkButton.setBackground(StyleUtils.SECONDARY_COLOR.brighter());
                }
            }
            public void mouseExited(MouseEvent e) {
                if (checkButton.isEnabled()) {
                    checkButton.setBackground(StyleUtils.SECONDARY_COLOR);
                }
            }
        });

        // Apply button hover effect
        applyButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (applyButton.isEnabled()) {
                    applyButton.setBackground(StyleUtils.ACCENT_COLOR.brighter());
                }
            }
            public void mouseExited(MouseEvent e) {
                if (applyButton.isEnabled()) {
                    applyButton.setBackground(StyleUtils.ACCENT_COLOR);
                }
            }
        });

        // Cancel button hover effect
        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(StyleUtils.DANGER_COLOR.brighter());
            }
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(StyleUtils.DANGER_COLOR);
            }
        });
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(StyleUtils.SUBHEADER_FONT);
        label.setForeground(StyleUtils.PRIMARY_COLOR);
        label.setAlignment(Label.RIGHT);
        return label;
    }

    private Label createResultLabel(String text) {
        Label label = new Label(text);
        label.setFont(StyleUtils.BODY_FONT);
        label.setForeground(StyleUtils.TEXT_COLOR);
        label.setBackground(StyleUtils.CARD_COLOR);
        return label;
    }

    private void loadUserVehicles() {
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUser.getUserId());
        vehicleChoice.removeAll();

        if (vehicles.isEmpty()) {
            vehicleChoice.add("No vehicles available");
            checkButton.setEnabled(false);
        } else {
            for (Vehicle vehicle : vehicles) {
                vehicleChoice.add(vehicle.getPlateNo() + " (" + vehicle.getVehicleType() + ")");
            }
            checkButton.setEnabled(true);
        }
    }

    private void checkEligibility() {
        if (vehicleChoice.getItemCount() == 0 || vehicleChoice.getSelectedItem().equals("No vehicles available")) {
            eligibilityLabel.setText("No vehicles registered");
            eligibilityLabel.setForeground(StyleUtils.DANGER_COLOR);
            return;
        }

        List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(currentUser.getUserId());
        selectedVehicle = vehicles.get(vehicleChoice.getSelectedIndex());

        boolean eligible = vehicleDAO.isEligibleForExchange(selectedVehicle);

        if (eligible) {
            eligibilityLabel.setText("✓ ELIGIBLE");
            eligibilityLabel.setForeground(Color.GREEN);

            double exchangeValue = vehicleDAO.calculateExchangeValue(selectedVehicle);
            double subsidyPercent = vehicleDAO.calculateSubsidyPercent(selectedVehicle);
            double totalSubsidy = exchangeValue * subsidyPercent / 100;

            valueLabel.setText(String.format("$%.2f", exchangeValue));
            valueLabel.setForeground(StyleUtils.ACCENT_COLOR);

            subsidyLabel.setText(String.format("%.1f%%", subsidyPercent));
            subsidyLabel.setForeground(StyleUtils.ACCENT_COLOR);

            totalLabel.setText(String.format("$%.2f", totalSubsidy));
            totalLabel.setForeground(StyleUtils.ACCENT_COLOR);

            applyButton.setEnabled(true);
        } else {
            eligibilityLabel.setText("✗ NOT ELIGIBLE");
            eligibilityLabel.setForeground(Color.RED);
            valueLabel.setText("-");
            valueLabel.setForeground(StyleUtils.TEXT_COLOR);
            subsidyLabel.setText("-");
            subsidyLabel.setForeground(StyleUtils.TEXT_COLOR);
            totalLabel.setText("-");
            totalLabel.setForeground(StyleUtils.TEXT_COLOR);
            applyButton.setEnabled(false);
        }
    }

    private void applyForExchange() {
        if (selectedVehicle == null) return;

        double exchangeValue = vehicleDAO.calculateExchangeValue(selectedVehicle);
        double subsidyPercent = vehicleDAO.calculateSubsidyPercent(selectedVehicle);

        ExchangeOffer offer = new ExchangeOffer();
        offer.setVehicleId(selectedVehicle.getVehicleId());
        offer.setExchangeValue(exchangeValue);
        offer.setSubsidyPercent(subsidyPercent);
        offer.setStatus("pending");

        if (offerDAO.createExchangeOffer(offer)) {
            // Show success message
            Dialog successDialog = new Dialog(this, "Success", true);
            successDialog.setLayout(new FlowLayout());
            successDialog.setSize(350, 150);
            successDialog.setLocationRelativeTo(this);
            successDialog.setBackground(StyleUtils.BACKGROUND_COLOR);

            Label successLabel = new Label("Exchange application submitted successfully!");
            successLabel.setFont(StyleUtils.SUBHEADER_FONT);
            successLabel.setForeground(StyleUtils.ACCENT_COLOR);
            successDialog.add(successLabel);

            Button okButton = StyleUtils.createButton("OK", StyleUtils.ACCENT_COLOR);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    successDialog.dispose();
                    dispose();
                }
            });
            successDialog.add(okButton);

            successDialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    successDialog.dispose();
                }
            });

            successDialog.setVisible(true);
        }
    }
}