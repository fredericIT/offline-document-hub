// VehicleRegistrationForm.java
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class VehicleRegistrationForm extends Frame {
    private TextField plateNoField, yearField, mileageField, conditionField;
    private Choice vehicleTypeChoice, fuelTypeChoice;
    private Button registerButton, cancelButton;
    private Label messageLabel, titleLabel;
    private VehicleDAO vehicleDAO;
    private User currentUser;

    public VehicleRegistrationForm(User user) {
        this.currentUser = user;
        vehicleDAO = new VehicleDAO();
        initializeUI();
        applyVehicleFormStyling();
    }

    private void initializeUI() {
        setTitle("GVEI - Vehicle Registration");
        setSize(500, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Header Panel
        Panel headerPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(StyleUtils.PRIMARY_COLOR);
        titleLabel = new Label("Register New Vehicle");
        titleLabel.setFont(StyleUtils.HEADER_FONT);
        titleLabel.setForeground(StyleUtils.TEXT_LIGHT);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main form panel
        Panel formPanel = new Panel(new GridLayout(8, 2, 15, 15));
        formPanel.setBackground(StyleUtils.BACKGROUND_COLOR);

        // Plate Number
        formPanel.add(createFormLabel("Plate Number:"));
        plateNoField = StyleUtils.createTextField(20);
        formPanel.add(plateNoField);

        // Vehicle Type
        formPanel.add(createFormLabel("Vehicle Type:"));
        vehicleTypeChoice = new Choice();
        vehicleTypeChoice.add("car");
        vehicleTypeChoice.add("bus");
        vehicleTypeChoice.add("motorcycle");
        vehicleTypeChoice.add("truck");
        formPanel.add(vehicleTypeChoice);

        // Fuel Type
        formPanel.add(createFormLabel("Fuel Type:"));
        fuelTypeChoice = new Choice();
        fuelTypeChoice.add("petrol");
        fuelTypeChoice.add("diesel");
        fuelTypeChoice.add("hybrid");
        formPanel.add(fuelTypeChoice);

        // Manufacture Year
        formPanel.add(createFormLabel("Manufacture Year:"));
        yearField = StyleUtils.createTextField(10);
        formPanel.add(yearField);

        // Mileage
        formPanel.add(createFormLabel("Mileage (km):"));
        mileageField = StyleUtils.createTextField(10);
        formPanel.add(mileageField);

        // Condition
        formPanel.add(createFormLabel("Condition (1-10):"));
        conditionField = StyleUtils.createTextField(5);
        conditionField.setText("5");
        formPanel.add(conditionField);

        // Message label
        messageLabel = new Label("");
        messageLabel.setForeground(StyleUtils.DANGER_COLOR);
        messageLabel.setAlignment(Label.CENTER);
        formPanel.add(messageLabel);
        formPanel.add(new Label("")); // Empty cell

        // Buttons panel
        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.setBackground(StyleUtils.BACKGROUND_COLOR);
        registerButton = StyleUtils.createButton("Register Vehicle", StyleUtils.ACCENT_COLOR);
        cancelButton = StyleUtils.createButton("Cancel", StyleUtils.SECONDARY_COLOR);

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add panels to frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event handlers
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerVehicle();
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

    private void applyVehicleFormStyling() {
        setBackground(StyleUtils.BACKGROUND_COLOR);

        // Style choice components
        if (vehicleTypeChoice != null) {
            vehicleTypeChoice.setFont(StyleUtils.BODY_FONT);
            vehicleTypeChoice.setBackground(StyleUtils.CARD_COLOR);
        }
        if (fuelTypeChoice != null) {
            fuelTypeChoice.setFont(StyleUtils.BODY_FONT);
            fuelTypeChoice.setBackground(StyleUtils.CARD_COLOR);
        }

        // Style labels in the form
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof Panel) {
                stylePanelComponents((Panel) comp);
            }
        }
    }

    private void stylePanelComponents(Panel panel) {
        Component[] comps = panel.getComponents();
        for (Component comp : comps) {
            if (comp instanceof Label) {
                Label label = (Label) comp;
                if (!label.getText().isEmpty() && label != messageLabel) {
                    label.setFont(StyleUtils.SUBHEADER_FONT);
                    label.setForeground(StyleUtils.PRIMARY_COLOR);
                }
            } else if (comp instanceof Choice) {
                comp.setFont(StyleUtils.BODY_FONT);
                comp.setBackground(StyleUtils.CARD_COLOR);
            } else if (comp instanceof Panel) {
                stylePanelComponents((Panel) comp);
            }
        }
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(StyleUtils.SUBHEADER_FONT);
        label.setForeground(StyleUtils.PRIMARY_COLOR);
        label.setAlignment(Label.RIGHT);
        return label;
    }

    private void registerVehicle() {
        try {
            String plateNo = plateNoField.getText().trim();
            String vehicleType = vehicleTypeChoice.getSelectedItem();
            String fuelType = fuelTypeChoice.getSelectedItem();
            int year = Integer.parseInt(yearField.getText().trim());
            int mileage = Integer.parseInt(mileageField.getText().trim());
            int condition = Integer.parseInt(conditionField.getText().trim());

            if (plateNo.isEmpty()) {
                messageLabel.setText("Please enter plate number");
                return;
            }

            if (condition < 1 || condition > 10) {
                messageLabel.setText("Condition must be between 1-10");
                return;
            }

            Vehicle vehicle = new Vehicle();
            vehicle.setOwnerId(currentUser.getUserId());
            vehicle.setPlateNo(plateNo);
            vehicle.setVehicleType(vehicleType);
            vehicle.setFuelType(fuelType);
            vehicle.setManufactureYear(year);
            vehicle.setMileage(mileage);
            vehicle.setConditionRating(condition);

            if (vehicleDAO.registerVehicle(vehicle)) {
                messageLabel.setText("Vehicle registered successfully!");
                messageLabel.setForeground(StyleUtils.ACCENT_COLOR);

                // Clear fields after successful registration
                clearFormFields();
            } else {
                messageLabel.setText("Registration failed. Plate number may already exist.");
                messageLabel.setForeground(StyleUtils.DANGER_COLOR);
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter valid numbers for year, mileage, and condition");
            messageLabel.setForeground(StyleUtils.DANGER_COLOR);
        }
    }

    private void clearFormFields() {
        plateNoField.setText("");
        yearField.setText("");
        mileageField.setText("");
        conditionField.setText("5");
        vehicleTypeChoice.select(0);
        fuelTypeChoice.select(0);
    }
}