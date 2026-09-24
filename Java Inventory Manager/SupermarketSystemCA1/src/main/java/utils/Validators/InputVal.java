package utils.Validators;

public class InputVal {




    public static String truncateString(String stringToTruncate, int length){
        if (stringToTruncate != null) {
            if (stringToTruncate.length() <= length) {
                return stringToTruncate;
            } else {
                return stringToTruncate.substring(0, length);
            }
        }
        else
            return null;
    }

    public static boolean validStringlength(String strToCheck, int maxLength){
        if (strToCheck != null ){
            return strToCheck.length() <= maxLength;
        }
        return false;
    }

    public static boolean validRangeDouble(double numbertoCheck, double min, double max) {
        return ((numbertoCheck >= (min) && (numbertoCheck <= (max))));

    }

    public static boolean validRangeInt(int numberToCheck, int min, int max) {
        return ((numberToCheck >= min) && (numberToCheck <= max));
    }
    public static String twoDecPlaces(double number) {
        return String.format("%.2f", number);
    }
}
