package cc.ranmc.game.card.server.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SQLRow {
    private final Map<String, Object> data = new HashMap<>();

    public SQLRow() {}

    public SQLRow(Map<String, Object> data) {
        if (data != null) {
            this.data.putAll(data);
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public String getString(String key, String defaultValue) {
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public Integer getInt(String key, Integer defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return value != null ? Integer.parseInt(value.toString()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Double getDouble(String key, Double defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return value != null ? Double.parseDouble(value.toString()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean) return (Boolean) value;
        if (value != null) return Boolean.parseBoolean(value.toString());
        return defaultValue;
    }

    public Long getLong(String key, Long defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return value != null ? Long.parseLong(value.toString()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public String getString(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    public Integer getInt(String key) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return value != null ? Integer.parseInt(value.toString()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getDouble(String key) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return value != null ? Double.parseDouble(value.toString()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean getBoolean(String key) {
        Object value = data.get(key);
        if (value instanceof Boolean) return (Boolean) value;
        return value != null && Boolean.parseBoolean(value.toString());
    }

    public Long getLong(String key) {
        Object value = data.get(key);
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return value != null ? Long.parseLong(value.toString()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Object getObject(String key) {
        return data.get(key);
    }

    public SQLRow set(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public Set<String> keySet() {
        return data.keySet();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
