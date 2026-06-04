package pl.edu.pb.smuggling.transport.model;

public enum VehicleType {
    SAMOCHOD_OSOBOWY("Samochód Osobowy"),
    BUS("Bus"),
    CIEZAROWKA("Ciężarówka"),
    VAN("Van");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
