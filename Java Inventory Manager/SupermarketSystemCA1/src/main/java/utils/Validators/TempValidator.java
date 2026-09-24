package utils.Validators;

public class TempValidator {
    public static final String UNREFRIGERATED = "UNREFRIGERATED";
    public static final String REFRIGERATED = "REFRIGERATED";
    public static final String FROZEN = "FROZEN";

    private static final double REFRIGERATED_THRESHOLD = 6.0;
    private static final double FROZEN_THRESHOLD = -5.0;


    /**
     * Convert double temperature to category
     * @param temperature
     * @return
     */
    public static String tempToCategory(double temperature) {
        if (temperature <= FROZEN_THRESHOLD) {
            return FROZEN;
        } else if (temperature < REFRIGERATED_THRESHOLD) {
            return REFRIGERATED;
        } else {
            return UNREFRIGERATED;
        }
    }

    /**
     *
     * @param category
     * @return
     */
    public static String getCategoryDesc(String category) {
        if (category.equals(UNREFRIGERATED)) {
            return "Room temperature storage (6°C and above)";
        } else if (category.equals(REFRIGERATED)) {
            return "Refrigerated storage (-5°C to 5°C)";
        } else {
            return "Frozen storage (-5°C and below)";
        }
    }

    public static String getCategoryMenu() {
        return """
                Select temperature category:
                1) UNREFRIGERATED
                2) REFRIGERATED
                3) FROZEN""";
    }

    public static String getCategoryFromChoice(int choice) {
        return switch (choice) {
            case 1 -> UNREFRIGERATED;
            case 2 -> REFRIGERATED;
            case 3 -> FROZEN;
            default -> null;
        };
    }

}
