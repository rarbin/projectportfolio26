package models;

public enum ElectionType {
    GENERAL("General Election"),
    LOCAL("Local Election"),
    EUROPEAN("European Election"),
    PRESIDENTIAL("Presidential Election"),
    REFERENDUM("Referendum");

    private final String displayName;

    ElectionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}