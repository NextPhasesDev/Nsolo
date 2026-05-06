import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static ResourceBundle bundle;

    public static void load(String languageCode) {
        Locale locale;

        switch (languageCode) {
            case "bem":
                locale = Locale.of("bem");
                break;
            case "ny":
                locale = Locale.of("ny");
                break;
            default:
                locale = Locale.of("en");
        }

        bundle = ResourceBundle.getBundle("resources.lang.messages", locale);
    }

    public static String get(String key) {
        try {
            if (bundle == null) {
                load("en");
            }
            return bundle.getString(key);
        } catch (Exception e) {
            return key; // fallback
        }
    }
}