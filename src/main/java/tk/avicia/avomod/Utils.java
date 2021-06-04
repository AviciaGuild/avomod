package tk.avicia.avomod;

public class Utils {
    public static String firstLetterCapital(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String getReadableTime(int minutes) {
        return (int) (Math.floor(minutes / 60.0)) + " h " + minutes % 60 + " m";
    }
}
