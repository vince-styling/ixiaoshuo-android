package com.vincestyling.ixiaoshuo.db.statment;

import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class Statment {
    protected StringBuilder statment = new StringBuilder();

    public Statment from(String table) {
        statment.append(" from ").append(table);
        return this;
    }

    public Statment where(String field) {
        statment.append(" where ").append(field);
        return this;
    }

    public Statment and(String field) {
        statment.append(" and ").append(field);
        return this;
    }

    public Statment orderby(String field) {
        statment.append(" order by ").append(field);
        return this;
    }

    public Statment desc() {
        statment.append(" desc");
        return this;
    }

    public Statment limit(int rowcount) {
        statment.append(" limit ").append(rowcount);
        return this;
    }

    public Statment offset(int offset, int rowcount) {
        statment.append(" limit ").append(offset).append(", ").append(rowcount);
        return this;
    }

    public Statment eq(Object value) {
        return term('=', value);
    }

    public Statment gt(Object value) {
        return term('>', value);
    }

    public Statment lt(Object value) {
        return term('<', value);
    }

    private Statment term(char op, Object value) {
        statment.append(' ').append(op).append(' ');
        append(value);
        return this;
    }

    protected void append(Object value) {
        append(statment, value);
    }

    protected void append(StringBuilder statment, Object value) {
        if (value instanceof String) {
            statment.append('\'').append(escape(value.toString())).append('\'');
        } else {
            statment.append(value);
        }
    }

    private String escape(String str) {
        return StringUtil.isEmpty(str) ? "" :
                str.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\");
    }

    @Override
    public String toString() {
        return statment.toString();
    }
}
