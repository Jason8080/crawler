package com.gm.demo.crawler.dao.model;

import java.io.Serializable;

public class Gather implements Serializable {
    private Integer id;

    private String tab;

    private String page;

    private String data;

    private String filters;

    private String echo;

    private String extracts;

    private String apiExample;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab == null ? null : tab.trim();
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page == null ? null : page.trim();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters == null ? null : filters.trim();
    }

    public String getEcho() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo == null ? null : echo.trim();
    }

    public String getExtracts() {
        return extracts;
    }

    public void setExtracts(String extracts) {
        this.extracts = extracts == null ? null : extracts.trim();
    }

    public String getApiExample() {
        return apiExample;
    }

    public void setApiExample(String apiExample) {
        this.apiExample = apiExample == null ? null : apiExample.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", tab=").append(tab);
        sb.append(", page=").append(page);
        sb.append(", data=").append(data);
        sb.append(", filters=").append(filters);
        sb.append(", echo=").append(echo);
        sb.append(", extracts=").append(extracts);
        sb.append(", apiExample=").append(apiExample);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}