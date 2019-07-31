package com.tzl.agriculture.util;

import org.apache.commons.lang.StringUtils;

public class TextUtil {
    /**
     *
     * @param str
     * @return
     */
    public static String checkStr2Num(String str){
        if (StringUtils.isEmpty(str)){
            return "0";
        }else {
            return str;
        }
    }


    public static String checkStr2Str(String str){
        if (StringUtils.isEmpty(str)){
            return "";
        }else {
            return str;
        }
    }


    public static boolean checkStrNull(String str){
        if (StringUtils.isEmpty(str) || str.equals("null")){
            return true;
        }else {
            return false;
        }
    }
}
