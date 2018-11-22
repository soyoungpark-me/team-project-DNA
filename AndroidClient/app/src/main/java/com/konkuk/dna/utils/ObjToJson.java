package com.konkuk.dna.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.konkuk.dna.utils.dbmanage.Dbhelper;

public class ObjToJson {

     /*
     * 정보저장(store)용 Json 생성
     * */
    public static JsonObject StoreObjToJson(Dbhelper dbhelp, Double lng, Double lat){
        Dbhelper dbhelper = dbhelp;

        JsonObject jObj = new JsonObject();

        jObj.addProperty("token", dbhelper.getAccessToken());
        jObj.addProperty("idx", dbhelper.getMyIdx());
        jObj.addProperty("nickname", dbhelper.getMyNickname());
        jObj.addProperty("avatar", dbhelper.getMyAvatar());
        jObj.addProperty("anonymity", dbhelper.getMyAnonymity());

        JsonArray posarr = new JsonArray();
        posarr.add(lng);
        posarr.add(lat);
        jObj.add("position", posarr);

        jObj.addProperty("radius", dbhelper.getMyRadius());
        return jObj;
    }

    /*
    * 메세지 전송 용 Json 생성
    * */
    public static JsonObject SendMsgObjToJson(Dbhelper dbhelp, Double lng, Double lat, String msgType, String contents) {

        Dbhelper dbhelper = dbhelp;

        JsonObject jObj = new JsonObject();

        jObj.addProperty("token", dbhelper.getAccessToken());

        JsonObject mdataObj = new JsonObject();
        mdataObj.addProperty("lng", lng);
        mdataObj.addProperty("lat", lat);
        mdataObj.addProperty("type", msgType);
        mdataObj.addProperty("contents", contents);

        jObj.add("messageData", mdataObj);
        jObj.addProperty("radius", dbhelper.getMyRadius());
        jObj.addProperty("testing", false);

        return jObj;
    }


    /*
     * 메세지 전송 용 Json 생성
     * */
    public static JsonObject SendDMObjToJson(int roomIdx, String msgType, String contents) {

        //JsonObject jObj = new JsonObject();
        JsonObject mdataObj = new JsonObject();

        mdataObj.addProperty("room_idx", roomIdx);
        mdataObj.addProperty("type", msgType);
        mdataObj.addProperty("contents", contents);

        //jObj.add("messageData", mdataObj);

        Log.e("SendDMG2J", mdataObj.toString());

        return mdataObj;
    }

    /*
     * 위치정보 전송 용 Json 생성
     * */
    public static JsonObject LocationObjToJson(Double lat, Double lng) {

        //JsonObject jObj = new JsonObject();
        JsonObject jObj = new JsonObject();

        jObj.addProperty("lat", lat);
        jObj.addProperty("lng", lng);

        return jObj;
    }

}
