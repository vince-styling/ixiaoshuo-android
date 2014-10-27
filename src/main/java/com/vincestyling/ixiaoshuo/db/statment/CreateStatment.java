package com.vincestyling.ixiaoshuo.db.statment;

public class CreateStatment extends Statment {
    private StringBuilder fields = new StringBuilder();
    private StringBuilder values = new StringBuilder();

    public static CreateStatment build(String table) {
        CreateStatment createStmt = new CreateStatment();
        createStmt.statment.append("insert or ignore into ").append(table);
        return createStmt;
    }

    private short fieldCount;

    public CreateStatment put(String field, Object value) {
        if (++fieldCount > 1) {
            values.append(", ");
            fields.append(", ");
        }
        append(values, value);
        fields.append(field);
        return this;
    }

    @Override
    public String toString() {
        statment.append('(').append(fields).append(')');
        statment.append(" values(").append(values);
        return statment.append(')').toString();
    }
}
