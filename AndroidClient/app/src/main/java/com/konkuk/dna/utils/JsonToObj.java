package com.konkuk.dna.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konkuk.dna.chat.ChatMessage;
import com.konkuk.dna.friend.message.DMRoom;

import java.util.ArrayList;
import java.util.HashMap;

import static com.konkuk.dna.utils.ConvertType.DatetoStr;
import static com.konkuk.dna.utils.ConvertType.getStringNoQuote;

public class JsonToObj {
    /*
    * 클래스 설명
    * : Json Java Object로 변환하는 클래스
    * */



    /*
    * 로그인 할때 날아온 Json변환 메소드
    * */
    public HashMap LoginJsonToObj(String jsonResult){

        HashMap<String, String> hm = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")){
            // 리스폰스가 정상이고 서버 응답이 200이라면?
            hm.put("issuccess", "true");
            JsonObject resultObject = (JsonObject) jsonObject.get("result");
            JsonObject profileObject = (JsonObject) resultObject.get("profile");

            // 유저정보를 해시맵에 저장
            hm.put("idx", profileObject.get("idx").toString());
            hm.put("id", profileObject.get("id").toString());
            hm.put("nickname", profileObject.get("nickname").toString());
            hm.put("avatar", profileObject.get("avatar").toString());
            hm.put("description", profileObject.get("description").toString());
            hm.put("radius", profileObject.get("radius").toString());
            hm.put("is_anonymity", profileObject.get("is_anonymity").toString());

            //토큰을 해시맵에 저장
            JsonObject tokenObject = (JsonObject) resultObject.get("token");
            hm.put("accessToken", tokenObject.get("accessToken").toString());
            hm.put("refreshToken", tokenObject.get("refreshToken").toString());

            Log.e("!!!", jsonResult);
            //Log.e("!!!", tokenObject.get("accessToken").toString());

        }else{
            //리스폰스에 하자가 있다면
            Log.e(jsonObject.get("code").toString(), jsonObject.get("message").toString());
            hm.put("issuccess", "false");
            hm.put("code", jsonObject.get("code").toString());
            hm.put("message", jsonObject.get("message").toString());
        }

        return hm;
    }

    /*
    * auth토큰 획득 Json변환 메소드
    * */
    public HashMap TokenJsonToObj(String jsonResult){

        HashMap<String, String> hm = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")){
            // 리스폰스가 정상이고 서버 응답이 200이라면?
            hm.put("issuccess", "true");
            JsonObject resultObject = (JsonObject) jsonObject.get("result");

            // 해시맵에 저장
            hm.put("accessToken", resultObject.get("accessToken").toString());
            Log.e("!!!=accessToken", resultObject.get("accessToken").toString());

        }else{
            //리스폰스에 하자가 있다면
            Log.e(jsonObject.get("code").toString(), jsonObject.get("message").toString());
            hm.put("issuccess", "false");
            hm.put("code", jsonObject.get("code").toString());
            hm.put("message", jsonObject.get("message").toString());
        }

        return hm;
    }

    /*
     * 회원가입 Json변환 메소드
     * */
    public HashMap RegisterJsonToObj(String jsonResult){

        HashMap<String, String> hm = new HashMap<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("201")){
            // 리스폰스가 정상이고 서버 응답이 200이라면?
            hm.put("issuccess", "true");
            JsonObject resultObject = (JsonObject) jsonObject.get("result");

            // 해시맵에 저장
            hm.put("idx", resultObject.get("idx").toString());
            hm.put("id", resultObject.get("id").toString());
            Log.e("!!!=id", resultObject.get("id").toString());

        }else{
            //리스폰스에 하자가 있다면
            if(jsonObject.get("code")!=null) {
                //ID 중복확인 메세지라면
                Log.e(jsonObject.get("code").toString(), jsonObject.get("message").toString());
                hm.put("issuccess", "false");
                hm.put("code", jsonObject.get("code").toString());
                hm.put("message", jsonObject.get("message").toString());
            }else{
                //ID를 쓰지 않았다면
                JsonObject uidObject = (JsonObject) jsonObject.get("errors");
                JsonObject msgObject = (JsonObject) uidObject.get("id");
                hm.put("name", jsonObject.get("name").toString());
                hm.put("message", msgObject.get("message").toString());

                Log.e(jsonObject.get("name").toString(), msgObject.get("message").toString());
            }
        }

        return hm;
    }

    /*
     * 전체 메세지 조회해서 날아온 Json변환 메소드
     * */
    public static ArrayList<ChatMessage> ChatAllJsonToObj(int myIdx, String jsonResult){
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        ArrayList<Integer> whoLikes = new ArrayList<>();
        int user_idx;
        String nickname, avatar, position, like_count;
        double lng, lat;
        String msg_type, _id, contents, created_at;
        int msg_idx, __v;
        int viewType=-1;

        // 이전 메세지의 idx확인
        int prev_idx = -1;

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {
            JsonArray resultArray = (JsonArray) jsonObject.get("result");

            for(int i=0; i<resultArray.size(); i++){
                JsonObject oneObject = (JsonObject) resultArray.get(i);

                //Log.e("message "+i, oneObject.toString());
                JsonObject userObject = (JsonObject) oneObject.get("user");
                user_idx = Integer.parseInt(userObject.get("idx").toString());
                nickname = getStringNoQuote(userObject.get("nickname").toString());
                avatar = getStringNoQuote(userObject.get("avatar").toString());

                JsonObject posObject = (JsonObject) oneObject.get("position");
                JsonArray coorArray = (JsonArray) posObject.get("coordinates");
                lng = Double.parseDouble(coorArray.get(0).toString());
                lat = Double.parseDouble(coorArray.get(1).toString());

                msg_type = getStringNoQuote(oneObject.get("type").toString());

                like_count = oneObject.get("like_count").toString();
                JsonArray wholikesArray = (JsonArray) oneObject.get("likes");
                for(int j=0; j<wholikesArray.size(); j++){
                    whoLikes.add(Integer.parseInt(wholikesArray.get(j).toString()));
                }

                _id = getStringNoQuote(oneObject.get("_id").toString());
                contents = getStringNoQuote(oneObject.get("contents").toString());
                created_at = getStringNoQuote(oneObject.get("created_at").toString());
                msg_idx = Integer.parseInt(oneObject.get("idx").toString());
                __v = Integer.parseInt(oneObject.get("__v").toString());



                if(myIdx == user_idx){
                    //지금 메세지가 내 메세지이면
                    if(i!=0 && prev_idx!=user_idx) {
                        // 이전에 있던 메시지가 다른사람것이라면 프로필이 필요해! 물론 내메세지가 리스트의 마지막이 아니였다면
                        ChatMessage tmp = chatMessages.get(chatMessages.size()-1);
                        tmp.setViewType(1);
                        chatMessages.remove(chatMessages.size()-1);
                        chatMessages.add(tmp);
                    }

                    viewType = 0;

                }else{
                    //지금 메세지가 남의 메세지라면
                    if(i==resultArray.size()-1){
                        // 그지역의 첫번째 메세지이면 지금 메세지에 프로필 필요
                        viewType = 1;

                    }else if(i!=0 && prev_idx!=user_idx && prev_idx != myIdx){
                        // 이전사람이 내가 아니고 지금메세지와도 다른 사람의 메세지면 프로필 필요
                        ChatMessage tmp = chatMessages.get(chatMessages.size()-1);
                        tmp.setViewType(1);
                        chatMessages.remove(chatMessages.size()-1);
                        chatMessages.add(tmp);

                        viewType = 2;
                    }else{
                        //
                        viewType = 2;
                    }
                }

                prev_idx = user_idx;

                Log.e(msg_type, resultArray.get(i).toString());

                //Log.e("!!!", user_idx+"/"+nickname+"/"+avatar+"/"+contents+"/"+created_at+"/"+like_count+"/"+msg_type+"/"+msg_idx);
                chatMessages.add(new ChatMessage(user_idx, nickname, avatar, contents, DatetoStr(created_at), like_count, msg_type, lng, lat, whoLikes, msg_idx, viewType));

            }
        }else{
            Log.e("!!!=", "This area NO MSG.");
            chatMessages = null;
        }

        return chatMessages;
    }

    /*
     * DM방 목록 검색으로 날아온 Json변환 메소드
     * */
    public static ArrayList<DMRoom> DMRoomJsonToObj(String jsonResult, int myIdx){
        ArrayList<DMRoom> Dmrooms = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);


        String _id, created_at, updated_at;
        int room_idx, __v;
        String m__id, m_nickname, m_avatar;
        int m_idx;
        String f__id="", f_nickname="", f_avatar="";
        int f_idx=0;
        String last_message, last_type;


        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {
            JsonArray resultArray = (JsonArray) jsonObject.get("result");

            for(int i=0; i<resultArray.size(); i++){
                JsonObject oneObject = (JsonObject) resultArray.get(i);

                JsonArray blindarray = (JsonArray) oneObject.get("blind");
                for(int j=0; j<blindarray.size(); j++){
                    //blind 무엇? blind 배열 안에 현재 유저의 idx에 포함되어 있을 경우 해당 채팅방을 리스트에 보여주지 않습니다.
                }
                _id = getStringNoQuote(oneObject.get("_id").toString());
                room_idx = Integer.parseInt(oneObject.get("idx").toString());

                JsonArray usersarray = (JsonArray) oneObject.get("users");
                for(int j=0; j<2; j++){
                    JsonObject userObject = (JsonObject) usersarray.get(i);
                    if(myIdx == Integer.parseInt(userObject.get("idx").toString())){
                        m__id =  getStringNoQuote(userObject.get("_id").toString());
                        m_idx = Integer.parseInt(userObject.get("idx").toString());
                        m_nickname =  getStringNoQuote(userObject.get("nickname").toString());
                        m_avatar =  getStringNoQuote(userObject.get("avatar").toString());
                    }else{
                        f__id =  getStringNoQuote(userObject.get("_id").toString());
                        f_idx = Integer.parseInt(userObject.get("idx").toString());
                        f_nickname =  getStringNoQuote(userObject.get("nickname").toString());
                        f_avatar =  getStringNoQuote(userObject.get("avatar").toString());
                    }
                }
                created_at = getStringNoQuote(oneObject.get("created_at").toString());
                updated_at = getStringNoQuote(oneObject.get("updated_at").toString());
                __v = Integer.parseInt(oneObject.get("__v").toString());
//                last_message = getStringNoQuote(oneObject.get("last_message").toString()); // NullPointerException 발생
                last_message = "";
                last_type = getStringNoQuote(oneObject.get("last_type").toString());


                Log.e(f_nickname,last_message);
                // TODO 멤버변수에 대한 설명이 필요함
                Dmrooms.add(new DMRoom(room_idx, f_idx, f_nickname, f_avatar, last_message, last_type, DatetoStr(updated_at)));
            }

        }else{
            Dmrooms = null;
        }

        return Dmrooms;
    }
}
