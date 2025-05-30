package org.cdpg.dx.database.postgres.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Join {

    private JoinType joinType;
    private String table;
    private String tableAlias;
    private String onColumn;
    private String joinColumn;

    public enum JoinType {
        INNER("INNER JOIN"),
        LEFT("LEFT JOIN"),
        RIGHT("RIGHT JOIN"),
        FULL("FULL JOIN"),
        CROSS("CROSS JOIN");

        private final String joinType;

        JoinType(String joinType) {
            this.joinType = joinType;
        }

        public String getJoinType() {
            return joinType;
        }

        public static JoinType fromString(String value) {
            for (JoinType type : JoinType.values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid JoinType: " + value);
        }
    }

    public Join() {
    }

    public Join(JsonObject json) {
        JoinConverter.fromJson(json, this);
        if (json.containsKey("joinType")) {
            this.joinType = JoinType.fromString(json.getString("joinType"));
        }
    }

    public Join(Join other) {
        this.joinType = other.joinType;
        this.table = other.table;
        this.tableAlias = other.tableAlias;
        this.onColumn = other.onColumn;
        this.joinColumn = other.joinColumn;
    }

    public Join(JoinType joinType, String table, String tableAlias, String onColumn, String joinColumn) {
        this.joinType = joinType;
        this.table = table;
        this.tableAlias = tableAlias;
        this.onColumn = onColumn;
        this.joinColumn = joinColumn;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JoinConverter.toJson(this, json);
        if (joinType != null) {
            json.put("joinType", joinType.name());
        }
        return json;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Join setJoinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    public String getTable() {
        return table;
    }

    public Join setTable(String table) {
        this.table = table;
        return this;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public Join setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }

    public String getOnColumn() {
        return onColumn;
    }

    public Join setOnColumn(String onColumn) {
        this.onColumn = onColumn;
        return this;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public Join setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
        return this;
    }

    public String toSQL() {
        String tableWithAlias = (tableAlias != null && !tableAlias.isEmpty())
            ? table + " AS " + tableAlias
            : table;
        String lhs = (tableAlias != null && !tableAlias.isEmpty()) ? tableAlias + "." + joinColumn : table + "." + joinColumn;
        return joinType.getJoinType() + " " + tableWithAlias + " ON " + lhs + " = " + onColumn;
    }
}