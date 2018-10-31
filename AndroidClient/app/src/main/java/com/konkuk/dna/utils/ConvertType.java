package com.konkuk.dna.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertType {
    /*
    * Date를 실제 어플리케이션에서 사용하는 양식으로 변경
    * */
    public static String DatetoStr(String datestr){
        // "2018-10-23T06:19:47.180Z"

        String result="";
        SimpleDateFormat formatter = new SimpleDateFormat ( "MM월 dd일 HH:mm" );

        try {
            Date dateorigin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(datestr);
            dateorigin.setHours(dateorigin.getHours()+9);
            result = formatter.format(dateorigin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * String에서 따옴표 제거하는 메소드
     * */
    public static String getStringNoQuote(String strQuote){

        //strQuote.substring(1, strQuote.length());
//        String[] nonQuote = strQuote.split("\"");
//        return nonQuote[1];
        //return strQuote;
        return strQuote.replaceAll("^\"|\"$", "");
    }

    /*
     * String에서 따옴표 추가하는 메소드
     * */
    public static String getStringAddQuote(String strQuote){

        String addQuote = "\"" + strQuote + "\"";
        return addQuote;
    }
}
