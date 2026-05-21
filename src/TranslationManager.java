import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Centralized translation service.
 * Supports classpath bundles, optional external packs, fallback language, and listeners.
 */
public final class TranslationManager {
    private static final String DEFAULT_LANGUAGE = "en";
    private static final TranslationManager INSTANCE = new TranslationManager();

    private final ConcurrentHashMap<String, Properties> cache = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Runnable> languageListeners = new CopyOnWriteArrayList<>();
    private final List<Path> externalLanguageDirs = new ArrayList<>();
    private volatile String currentLanguage = DEFAULT_LANGUAGE;

    private TranslationManager() {
        externalLanguageDirs.add(Paths.get(System.getProperty("user.home"), ".nsolo", "lang"));
        externalLanguageDirs.add(Paths.get("lang"));
        // Warm up fallback and current language cache.
        ensureLoaded(DEFAULT_LANGUAGE);
    }

    public static TranslationManager getInstance() {
        return INSTANCE;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setLanguage(String languageCode) {
        String normalized = normalizeLanguageCode(languageCode);
        ensureLoaded(normalized);
        if (!normalized.equals(currentLanguage)) {
            currentLanguage = normalized;
            notifyLanguageChanged();
        }
    }

    public String tr(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }

        Properties current = ensureLoaded(currentLanguage);
        String value = current.getProperty(key);
        if (value != null) {
            return value;
        }

        if (!DEFAULT_LANGUAGE.equals(currentLanguage)) {
            Properties fallback = ensureLoaded(DEFAULT_LANGUAGE);
            value = fallback.getProperty(key);
            if (value != null) {
                return value;
            }
        }

        return key;
    }

    public String trf(String key, Object... args) {
        String pattern = tr(key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        Locale locale = Locale.forLanguageTag(currentLanguage);
        return new MessageFormat(pattern, locale).format(args);
    }

    public void addLanguageChangeListener(Runnable listener) {
        if (listener != null) {
            languageListeners.addIfAbsent(listener);
        }
    }

    public void removeLanguageChangeListener(Runnable listener) {
        languageListeners.remove(listener);
    }

    public Set<String> getCachedLanguages() {
        return cache.keySet();
    }

    private String normalizeLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            return DEFAULT_LANGUAGE;
        }
        return languageCode.trim().toLowerCase(Locale.ROOT);
    }

    private Properties ensureLoaded(String languageCode) {
        return cache.computeIfAbsent(languageCode, this::loadLanguageProperties);
    }

    private Properties loadLanguageProperties(String languageCode) {
        Properties merged = new Properties();

        if (!DEFAULT_LANGUAGE.equals(languageCode)) {
            merged.putAll(loadLanguageProperties(DEFAULT_LANGUAGE));
        }

        Properties selected = loadSingleLanguageFile(languageCode);
        if (selected != null) {
            merged.putAll(selected);
        }

        return merged;
    }

    private Properties loadSingleLanguageFile(String languageCode) {
        String fileName = "messages_" + languageCode + ".properties";

        // External packs first so downloadable packs can override bundled defaults.
        for (Path dir : externalLanguageDirs) {
            Path file = dir.resolve(fileName);
            if (Files.exists(file) && Files.isRegularFile(file)) {
                try (InputStream in = Files.newInputStream(file)) {
                    Properties p = new Properties();
                    p.load(in);
                    return p;
                } catch (Exception ignored) {
                    // Continue fallback chain.
                }
            }
        }

        String classPath = "/resources/lang/" + fileName;
        try (InputStream in = TranslationManager.class.getResourceAsStream(classPath)) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                return p;
            }
        } catch (Exception ignored) {
            // Continue fallback chain.
        }

        try (InputStream in = TranslationManager.class.getClassLoader().getResourceAsStream("resources/lang/" + fileName)) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                return p;
            }
        } catch (Exception ignored) {
            // Continue fallback chain.
        }

        return new Properties();
    }

    private void notifyLanguageChanged() {
        for (Runnable listener : languageListeners) {
            try {
                listener.run();
            } catch (Exception ignored) {
                // Do not let one listener break others.
            }
        }
    }
}

