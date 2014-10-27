package com.vincestyling.ixiaoshuo.db.statment;

public class UpdateStatment extends Statment {

    public static UpdateStatment build(String table) {
        UpdateStatment updateStmt = new UpdateStatment();
        updateStmt.statment.append("update ").append(table).append(" set ");
        return updateStmt;
    }

    private short fieldCount;

    public UpdateStatment set(String field, Object value) {
        if (++fieldCount > 1) statment.append(", ");
        statment.append(field);
        eq(value);
        return this;
    }
}
