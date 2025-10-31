package cc.ranmc.game.card.server.sql;

import lombok.Getter;

@Getter
public class SQLFilter {
    private String result = "";

    public SQLFilter set(String name, String value) {
        result += " SET " + name + " = '" + value + "'";
        return this;
    }

    public SQLFilter set(String name, int value) {
        result += " SET " + name + " = " + value;
        return this;
    }

    public SQLFilter set(String name, long value) {
        result += " SET " + name + " = " + value;
        return this;
    }

    public SQLFilter andSet(String name, String value) {
        result += " , " + name + " = '" + value + "'";
        return this;
    }

    public SQLFilter andSet(String name, int value) {
        result += " , " + name + " = " + value;
        return this;
    }

    public SQLFilter andSet(String name, long value) {
        result += " , " + name + " = " + value;
        return this;
    }

    public SQLFilter setNull(String name) {
        result += " SET " + name + " = null";
        return this;
    }

    public SQLFilter where(String value) {
        result += " WHERE ID = " + value;
        return this;
    }

    public SQLFilter where(int value) {
        result += " WHERE ID = " + value;
        return this;
    }

    public SQLFilter whereLower(String name, String value) {
        result += " WHERE LOWER(" + name + ") = LOWER('" + value + "')";
        return this;
    }

    public SQLFilter where(String name, String value) {
        result += " WHERE " + name + " = '" + value + "'";
        return this;
    }

    public SQLFilter where(String name, int value) {
        result += " WHERE " + name + " = " + value;
        return this;
    }

    public SQLFilter whereLike(String name, String value) {
        result += " WHERE " + name + " LIKE '" + value + "'";
        return this;
    }

    public SQLFilter whereLike(String name, int value) {
        result += " WHERE " + name + " LIKE " + value;
        return this;
    }

    public SQLFilter andWhere(String name, String value) {
        result += " AND " + name + " = '" + value + "'";
        return this;
    }

    public SQLFilter andWhere(String name, int value) {
        result += " AND " + name + " = " + value;
        return this;
    }

    public SQLFilter andWhereLike(String name, String value) {
        result += " AND " + name + " LIKE '" + value + "'";
        return this;
    }

    public SQLFilter orWhereLike(String name, String value) {
        result += " OR " + name + " LIKE '" + value + "'";
        return this;
    }

    public SQLFilter andWhereLike(String name, int value) {
        result += " AND " + name + " LIKE " + value;
        return this;
    }

    public SQLFilter andBetween(String name, int v1, int v2) {
        result += " AND (" + name + " BETWEEN " + v1 + " and " + v2 + ")";
        return this;
    }

    public SQLFilter andContrast(String name, String contrast, long value) {
        result += " AND " + name + " " + contrast + " " + value;
        return this;
    }

    public SQLFilter andLessThan(String name, long value) {
        result += " AND " + name + " < " + value;
        return this;
    }

    public SQLFilter whereMoreThan(String name, long value) {
        result += " WHERE " + name + " > " + value;
        return this;
    }

    public SQLFilter limit(int limit) {
        result += " LIMIT " + limit;
        return this;
    }

    public SQLFilter order(String order) {
        result += " ORDER BY " + order;
        return this;
    }

    public SQLFilter limit(int offset, int limit) {
        result += " LIMIT " + offset + ", " + limit;
        return this;
    }
}
