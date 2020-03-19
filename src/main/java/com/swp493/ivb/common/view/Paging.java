package com.swp493.ivb.common.view;

import java.util.List;

public class Paging<T> {

    private List<T> items;

    private Integer offset;

    private Integer limit;

    private Integer total;

    public Paging() {
    }

    public Paging(List<T> items, Integer offset, Integer limit, Integer total) {
        this.items = items;
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }

    public void setPageInfo(int total, int limit, int offset){
        this.total = total;
        this.offset = offset;
        this.limit = limit;
        if(offset>=total || offset<0) this.offset = 0;
        if(limit<offset)  this.limit = offset+5;
        if(limit>=total)  this.limit = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
