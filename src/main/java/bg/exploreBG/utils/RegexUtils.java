package bg.exploreBG.utils;

public final class RegexUtils {

    //NOTE: Shared regex for validating village/town/city names or trails - start point / end point
    public static final String PLACE_REGEX = "^[A-Za-z]+(\\s?[A-Za-z]+)*$";

    // Add other shared regex patterns here in the future as needed
    // public static final String ANOTHER_REGEX = "your-other-regex-here";

    // Private constructor to prevent instantiation
    private RegexUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
