package com.vincestyling.ixiaoshuo.db.statment;

public class DeleteStatment extends Statment {

    public static DeleteStatment build(String table) {
        DeleteStatment deleteStmt = new DeleteStatment();
        deleteStmt.statment.append("delete");
        deleteStmt.from(table);
        return deleteStmt;
    }

}
