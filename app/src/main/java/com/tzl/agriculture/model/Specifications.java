package com.tzl.agriculture.model;

import java.util.List;

/**
 * 规格
 */
public class Specifications {
    private int id;
    private String name;//规格标题
    private List<ValueList> valueList;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ValueList> getValueList() {
        return valueList;
    }

    public void setValueList(List<ValueList> valueList) {
        this.valueList = valueList;
    }

    public static class ValueList{
        private String specsId;//规格id，用于匹配产品
        private String specsValue;//规格名称

        public String getSpecsId() {
            return specsId;
        }

        public void setSpecsId(String specsId) {
            this.specsId = specsId;
        }

        public String getSpecsValue() {
            return specsValue;
        }

        public void setSpecsValue(String specsValue) {
            this.specsValue = specsValue;
        }
    }
}
