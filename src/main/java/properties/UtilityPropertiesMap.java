package properties;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class UtilityPropertiesMap extends AbstractMap<String, String> {

    final UtilityPropertiesMap parent;
    final Map<String, String> delegate;

    UtilityPropertiesMap(UtilityPropertiesMap parent, Map<String, String> delegate) {
        this.delegate = requireNonNull(delegate);
        this.parent = parent;
    }

    UtilityPropertiesMap(Map<String, String> delegate) {
        this(null, delegate);
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return delegate.entrySet();
    }

    /**
     * Creates a UtilityPropertiesMap from a Properties object.
     *
     * @param p The Properties object to create the map from.
     * @return A UtilityPropertiesMap created from the Properties object.
     */
    static UtilityPropertiesMap create(Properties p) {
        Map<String, String> copy = new HashMap<>();
        p.stringPropertyNames().forEach(s -> copy.put(s, p.getProperty(s)));
        return new UtilityPropertiesMap(copy);
    }

    @Override
    public String get(Object key) {
        String exactMatch = super.get(key);
        if (exactMatch != null) {
            return exactMatch;
        }

        if (!(key instanceof String keyString)) {
            return null;
        }

        String formattedKey = formatKey(keyString);
        String formattedMatch = super.get(formattedKey);
        if (formattedMatch != null) {
            return formattedMatch;
        }

        if (parent == null) {
            return null;
        }
        return parent.get(key);
    }

    /**
     * Formats a key to handle variations in case and characters.
     *
     * @param key The key to format.
     * @return The formatted key.
     */
    String formatKey(String key) {
        return key
                .replace(".", "_")
                .replace("-", "_")
                .toLowerCase(Locale.ENGLISH);
    }
}