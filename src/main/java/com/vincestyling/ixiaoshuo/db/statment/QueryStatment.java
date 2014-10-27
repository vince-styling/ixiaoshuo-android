package com.vincestyling.ixiaoshuo.db.statment;

public class QueryStatment extends Statment {

    public static QueryStatment build(String... fields) {
        QueryStatment queryStmt = new QueryStatment();
        queryStmt.statment.append("select");
        if (fields != null && fields.length > 0) {
            for (int i = 0; i < fields.length; i++) {
                if (i > 0) queryStmt.statment.append(',');
                queryStmt.statment.append(' ').append(fields[i]);
            }
        }
        return queryStmt;
    }

    public static QueryStatment fields() {
        return build().query_fields();
    }

    private QueryStatment query_fields() {
        statment.append(" *");
        return this;
    }

    public static QueryStatment rowcount() {
        return build().query_rowcount();
    }

    private QueryStatment query_rowcount() {
        statment.append(" count(*)");
        return this;
    }
}
