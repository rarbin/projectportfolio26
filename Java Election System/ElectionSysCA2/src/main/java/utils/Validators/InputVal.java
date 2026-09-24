package utils.Validators;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputVal {

    public static boolean validStringlength(String input, int maxLength) {
        return input != null && !input.trim().isEmpty() && input.length() <= maxLength;
    }
    public static boolean isValidYear(int year) {
        return year >= 1900 && year <= LocalDate.now().getYear();
    }

    public static boolean isValidVoteCount(int votes) {
        return votes >= 0;
    }

    public static boolean isValidSeats(int seats) {
        return seats > 0;
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String twoDecPlaces(double value) {
        return String.format("%.2f", value);
    }


}