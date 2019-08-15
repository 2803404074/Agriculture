package com.tzl.agriculture.fragment.vip.model;

import java.util.List;

public class MingxMo {
    private String dateFormat;
    private String income;
    private String expend;
    private List<Option> options;

    public String getDateFormat() {
        return dateFormat;
    }

    public String getIncome() {
        return income;
    }

    public String getExpend() {
        return expend;
    }

    public List<Option> getOptions() {
        return options;
    }

    public static class Option{
        private int optionType;//0 加，1减
        private String notes;
        private String integral;
        private String createTime;
        private String source;

        public int getOptionType() {
            return optionType;
        }

        public void setOptionType(int optionType) {
            this.optionType = optionType;
        }

        public String getNotes() {
            return notes;
        }

        public String getIntegral() {
            return integral;
        }

        public String getCreateTime() {
            return createTime;
        }
    }
}
