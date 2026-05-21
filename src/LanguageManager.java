public final class LanguageManager {
    private static final TranslationManager TRANSLATIONS = TranslationManager.getInstance();

    private LanguageManager() {
    }

    public static void load(String languageCode) {
        TRANSLATIONS.setLanguage(languageCode);
    }

    public static String getCurrentLanguage() {
        return TRANSLATIONS.getCurrentLanguage();
    }

    public static String get(String key) {
        return TRANSLATIONS.tr(key);
    }

    public static String format(String key, Object... args) {
        return TRANSLATIONS.trf(key, args);
    }

    // Alias helpers to keep UI code short.
    public static String t(String key) {
        return get(key);
    }

    public static String tf(String key, Object... args) {
        return format(key, args);
    }

    public static void addLanguageChangeListener(Runnable listener) {
        TRANSLATIONS.addLanguageChangeListener(listener);
    }

    public static void removeLanguageChangeListener(Runnable listener) {
        TRANSLATIONS.removeLanguageChangeListener(listener);
    }
}