package com.gm.demo.crawler.dao.model;

import java.io.Serializable;

public class Metadata implements Serializable {
    private Integer id;

    private String field;

    private String dataType;

    private Integer len;

    private String comment;

    private String def;

    private String tab;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field == null ? null : field.trim();
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def == null ? null : def.trim();
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab == null ? null : tab.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", field=").append(field);
        sb.append(", dataType=").append(dataType);
        sb.append(", len=").append(len);
        sb.append(", comment=").append(comment);
        sb.append(", def=").append(def);
        sb.append(", tab=").append(tab);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}