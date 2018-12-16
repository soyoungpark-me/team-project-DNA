package com.konkuk.dna.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konkuk.dna.chat.ChatMessage;
import com.konkuk.dna.chat.ChatUser;
import com.konkuk.dna.friend.manage.Friend;
import com.konkuk.dna.friend.manage.Request;
import com.konkuk.dna.friend.message.DMMessage;
import com.konkuk.dna.friend.message.DMRoom;
import com.konkuk.dna.post.Comment;
import com.konkuk.dna.post.Post;
import com.konkuk.dna.utils.dbmanage.Dbhelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static com.konkuk.dna.utils.ConvertType.DatetoStr;
import static com.konkuk.dna.utils.ConvertType.getStringAddQuote;
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
            hm.put("anonymity", profileObject.get("anonymity").toString());
            hm.put("searchable", profileObject.get("searchable").toString());
            hm.put("address", "불러오는 중..");

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
     * 유저정보 검색 Jsn변환 메소드
     * */
    public static Friend SearchUserJsonToObj(String jsonResult){

        String id, nickname, avatar, description;
        int idx;
        boolean status = false;

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")){
            // 리스폰스가 정상이고 서버 응답이 200이라면?
            JsonObject resultObject = (JsonObject) jsonObject.get("result");
            id = getStringNoQuote(String.valueOf(resultObject.get("id")));
            idx = Integer.parseInt(String.valueOf(resultObject.get("idx")));
            nickname = getStringNoQuote(String.valueOf(resultObject.get("nickname")));
            avatar = getStringNoQuote(String.valueOf(resultObject.get("avatar")));
            description = getStringNoQuote(String.valueOf(resultObject.get("description")));
            if(description.equals("null")){
                description = "";
            }

        }else{
            //리스폰스에 하자가 있다면
            Log.e(jsonObject.get("code").toString(), jsonObject.get("message").toString());
//            hm.put("issuccess", "false");
//            hm.put("code", jsonObject.get("code").toString());
//            hm.put("message", jsonObject.get("message").toString());
            return null;
        }

//        onFriends.add(new Friend("3457soso", "socoing", null, "", true));
        return new Friend(id, idx, nickname, avatar, description, status);
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
    public static HashMap RegisterJsonToObj(String jsonResult){

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
        boolean amILike;
        int msg_idx, user_idx, anonymity;
        String nickname, avatar, position, like_count;
        double lng, lat;
        String msg_type, contents, created_at;
        int viewType=-1;

        // 이전 메세지의 idx확인
        int prev_idx = -1;

        Log.d("JsonToObj", jsonObject.toString());

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {
            JsonArray resultArray = (JsonArray) jsonObject.get("result");
            for(int i=0; i<resultArray.size(); i++){
                JsonObject oneObject = (JsonObject) resultArray.get(i);

                //Log.e("message "+i, oneObject.toString());
                JsonObject userObject = (JsonObject) oneObject.get("user");
                user_idx = Integer.parseInt(userObject.get("idx").toString());
                anonymity = Integer.parseInt(userObject.get("anonymity").toString());
                nickname = getStringNoQuote(userObject.get("nickname").toString());
                avatar = getStringNoQuote(userObject.get("avatar").toString());

                JsonObject posObject = (JsonObject) oneObject.get("position");
                JsonArray coorArray = (JsonArray) posObject.get("coordinates");
                lng = Double.parseDouble(coorArray.get(0).toString());
                lat = Double.parseDouble(coorArray.get(1).toString());

                msg_type = getStringNoQuote(oneObject.get("type").toString());

                like_count = oneObject.get("like_count").toString();
                JsonArray wholikesArray = (JsonArray) oneObject.get("likes");

                amILike = false;
                for(int j=0; j<wholikesArray.size(); j++){
                    whoLikes.add(Integer.parseInt(wholikesArray.get(j).toString()));
                    if(Integer.parseInt(wholikesArray.get(j).toString()) == myIdx){
                        amILike = true;
                    }
                }

                contents = getStringNoQuote(oneObject.get("contents").toString());
                created_at = getStringNoQuote(oneObject.get("created_at").toString());
                msg_idx = Integer.parseInt(oneObject.get("idx").toString());

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
                //Log.e("msg", msg_idx+" "+contents+" "+DatetoStr(created_at));
                chatMessages.add(new ChatMessage(user_idx, nickname, avatar, anonymity, contents, DatetoStr(created_at),
                        like_count, msg_type, lng, lat, whoLikes, msg_idx, viewType, amILike));

            }
        }else{
            Log.e("!!!=", "This area NO MSG.");
            chatMessages = null;
        }


        return chatMessages;
    }


    /*
     * DM 방 채팅 리스트 조회
     * */
    public static ArrayList<DMMessage> DMMsgJsonToObj(int myIdx, String nickname, String jsonResult){
        //Log.e("DM MSG", jsonResult);
        ArrayList<DMMessage> dmMessages = new ArrayList<>();
        String type="", created_at="", _id, contents="";
        String avatar;
        int sender_idx=-1;

        // 이전 메세지의 idx확인
        int prev_idx = -1;
        // 내꺼, 너꺼, 사진있는 너꺼
        int viewType=-1;


        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {
            JsonObject resultObject = (JsonObject) jsonObject.get("result");
            avatar = getStringNoQuote(resultObject.get("avatar").toString());

            JsonArray dmsArray = (JsonArray) resultObject.get("DMs");
            for(int i=0; i<dmsArray.size(); i++){
                JsonObject oneDMObject = (JsonObject) dmsArray.get(i);
                type = getStringNoQuote(oneDMObject.get("type").toString());
                created_at = getStringNoQuote(oneDMObject.get("created_at").toString());
                _id = getStringNoQuote(oneDMObject.get("_id").toString());
                sender_idx = Integer.parseInt(oneDMObject.get("sender_idx").toString());
                contents = getStringNoQuote(oneDMObject.get("contents").toString());

                if(myIdx == sender_idx){
                    //지금 메세지가 내 메세지이면
                    if(i!=0 && prev_idx!=sender_idx) {
                        // 이전에 있던 메시지가 다른사람것이라면 프로필이 필요해! 물론 내메세지가 리스트의 마지막이 아니였다면
//                        DMMessage tmp = dmMessages.get(dmMessages.size()-1);
//                        tmp.setViewType(1);
//                        dmMessages.remove(dmMessages.size()-1);
//                        dmMessages.add(tmp);
                    }
                    viewType = 0;

                }else{
                    //지금 메세지가 남의 메세지라면
                    if(i == 0){
                        // 그지역의 첫번째 메세지이면 지금 메세지에 프로필 필요

                        viewType = 1;

                    }else if(i!=0 && prev_idx!=sender_idx){
                        // 이전사람이 내가 아니고 지금메세지와도 다른 사람의 메세지면 프로필 필요
//                        DMMessage tmp = dmMessages.get(dmMessages.size()-1);
//                        tmp.setViewType(1);
//                        dmMessages.remove(dmMessages.size()-1);
//                        dmMessages.add(tmp);

                        viewType = 1;
                    }else{
                        //
                        viewType = 2;
                    }
                }

                prev_idx = sender_idx;

                dmMessages.add(new DMMessage(sender_idx, contents, DatetoStr(created_at), type, viewType, avatar, nickname));
            }
        }
        else{
            Log.e("!!!=", "SERVER ERROR, CANNOT RECEIVE MSG.");
            dmMessages = null;
        }
        return dmMessages;
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
                    JsonObject userObject = (JsonObject) usersarray.get(j);
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

                last_message = "";
                if (oneObject.get("last_message")!=null) {
                    last_message = getStringNoQuote(oneObject.get("last_message").toString());
                }

                last_type = "";
                if (oneObject.get("last_type")!=null) {
                    last_type = getStringNoQuote(oneObject.get("last_type").toString());
                }

                // TODO 멤버변수에 대한 설명이 필요함
                Dmrooms.add(new DMRoom(room_idx, f_idx, f_nickname, f_avatar, last_message, last_type, DatetoStr(updated_at)));
            }

        }else{
            Dmrooms = null;
        }

        return Dmrooms;
    }

    /*
     * DM방 목록 검색으로 날아온 Json변환 메소드
     * */
    public static DMRoom NewDMRoomJsonToObj(String jsonResult, int myIdx){

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
            JsonObject oneObject = (JsonObject) jsonObject.get("result");

            JsonArray blindarray = (JsonArray) oneObject.get("blind");
            for(int j=0; j<blindarray.size(); j++){
                //blind 무엇? blind 배열 안에 현재 유저의 idx에 포함되어 있을 경우 해당 채팅방을 리스트에 보여주지 않습니다.
            }
            _id = getStringNoQuote(oneObject.get("_id").toString());
            room_idx = Integer.parseInt(oneObject.get("idx").toString());

            JsonArray usersarray = (JsonArray) oneObject.get("users");
            for(int j=0; j<2; j++){
                JsonObject userObject = (JsonObject) usersarray.get(j);
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

            last_message = "";
            if (oneObject.get("last_message")!=null) {
                last_message = getStringNoQuote(oneObject.get("last_message").toString());
            }

            last_type = "";
            if (oneObject.get("last_type")!=null) {
                last_type = getStringNoQuote(oneObject.get("last_type").toString());
            }
        }
        else{
            return null;
        }

        return new DMRoom(room_idx, f_idx, f_nickname, f_avatar, last_message, last_type, DatetoStr(updated_at));
    }


    /*
     * 현재 접속 멤버 홛인 Json변환 메소드
     * */
    public static ArrayList<ChatUser> ConnectUserJsonToObj(String jsonResult, int myIdx){

        ArrayList<ChatUser> result= new ArrayList<>();
        int idx, anonymity;
        String nickname, avatar;
        boolean inside;

        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonResult);
        //JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        for(int i=0; i<jsonArray.size(); i++){
            JsonObject arr = (JsonObject) jsonArray.get(i);

            idx = Integer.parseInt(getStringNoQuote(arr.get("idx").toString()));
            nickname = getStringNoQuote(arr.get("nickname").toString());
            avatar = getStringNoQuote(arr.get("avatar").toString());
            anonymity = Integer.parseInt(getStringNoQuote(arr.get("anonymity").toString()));
            inside = arr.get("inside").getAsBoolean();

            if(myIdx != idx){
                result.add(new ChatUser(idx, nickname, avatar, anonymity, inside));
            }
        }

        return result;
    }

    /*
     * Posting 조회로 받아온 Json변환 메소드
     */
    public static ArrayList<Post> PostingJsonToObj(String jsonResult, int num){
        Log.v("jsontoobj", "jsonresult at pjtoo : " + jsonResult);
        ArrayList<Post> postings = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        int posting_idx, writer_idx;
        String avatar, nickname;
        String date, title, content;
        Double longitude, latitude;
        int likeCount;
        Boolean onlyme;
        JsonArray commentList;
        ArrayList<Comment> comments = new ArrayList<>();
        int user_idx, reply_idx;
        String rdate;
        String comment;
        String ravatar, rnickname;

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {
            switch(num) {
                case 1:
                    JsonArray resultArray = (JsonArray) jsonObject.get("result");

                    for (int i = 0; i < resultArray.size(); i++) {
                        JsonObject oneObject = (JsonObject) resultArray.get(i);

                        Log.v("jsontoobj", "oneobject : " + oneObject.toString());

                        posting_idx = Integer.parseInt(oneObject.get("posting_idx").toString());
                        writer_idx = Integer.parseInt(oneObject.get("writer_idx").toString());
                        nickname = getStringNoQuote(String.valueOf(oneObject.get("nickname")));
                        avatar = getStringNoQuote(String.valueOf(oneObject.get("avatar")));
                        date = getStringNoQuote(oneObject.get("posting_date").toString());
                        title = getStringNoQuote(oneObject.get("title").toString());
                        content = getStringNoQuote(oneObject.get("contents").toString());
                        likeCount = Integer.parseInt(oneObject.get("likes_cnt").toString());
                        longitude = Double.parseDouble(oneObject.get("longitude").toString());
                        latitude = Double.parseDouble(oneObject.get("latitude").toString());
                        onlyme = Boolean.parseBoolean(oneObject.get("onlyme").toString());

                        postings.add(new Post(posting_idx, writer_idx, avatar, nickname, date, title, content, longitude, latitude, likeCount, onlyme, comments));

                    }

                    break;

                case 2:
                    JsonObject resultObject = (JsonObject) jsonObject.get("result");
                    if((JsonObject)resultObject.get("pContents")!=null) {
                        JsonObject postObject = (JsonObject) resultObject.get("pContents");

                        posting_idx = Integer.parseInt(postObject.get("posting_idx").toString());
                        writer_idx = Integer.parseInt(postObject.get("writer_idx").toString());
                        nickname = getStringNoQuote(String.valueOf(postObject.get("nickname")));
                        avatar = getStringNoQuote(String.valueOf(postObject.get("avatar")));
                        date = getStringNoQuote(postObject.get("posting_date").toString());
                        title = getStringNoQuote(postObject.get("title").toString());
                        content = getStringNoQuote(postObject.get("contents").toString());
                        likeCount = Integer.parseInt(postObject.get("likes_cnt").toString());
                        longitude = Double.parseDouble(postObject.get("longitude").toString());
                        latitude = Double.parseDouble(postObject.get("latitude").toString());
                        onlyme = Boolean.parseBoolean(postObject.get("onlyme").toString());

                        commentList = (JsonArray) resultObject.get("pReply");
                        for (int j = 0; j < commentList.size(); j++) {
                            JsonObject oneCommentObject = (JsonObject) commentList.get(j);

                            reply_idx = j;
                            rnickname = getStringNoQuote(oneCommentObject.get("nickname").toString());
                            ravatar = getStringNoQuote(oneCommentObject.get("avatar").toString());
                            comment = getStringNoQuote(oneCommentObject.get("reply_contents").toString());
                            rdate = getStringNoQuote(oneCommentObject.get("date").toString());

                            comments.add(new Comment(reply_idx, ravatar, rnickname, rdate, comment));
                            Log.v("jsontoobj", "avatar and nick : " + ravatar + " &&&& " + comments.get(j).getAvatar());
                        }

                        postings.add(new Post(posting_idx, writer_idx, avatar, nickname, date, title, content, longitude, latitude, likeCount, onlyme, comments));

                        postings.get(0).setCommentCount(commentList.size());

                    }
                    else{
                        posting_idx = Integer.parseInt(resultObject.get("posting_idx").toString());
                        writer_idx = Integer.parseInt(resultObject.get("writer_idx").toString());
                        nickname = getStringNoQuote(String.valueOf(resultObject.get("nickname")));
                        avatar = getStringNoQuote(String.valueOf(resultObject.get("avatar")));
                        date = getStringNoQuote(resultObject.get("posting_date").toString());
                        title = getStringNoQuote(resultObject.get("title").toString());
                        content = getStringNoQuote(resultObject.get("contents").toString());
                        likeCount = Integer.parseInt(resultObject.get("likes_cnt").toString());
                        longitude = Double.parseDouble(resultObject.get("longitude").toString());
                        latitude = Double.parseDouble(resultObject.get("latitude").toString());
                        onlyme = Boolean.parseBoolean(resultObject.get("onlyme").toString());

                        Log.v("jsontoobj", "without reply : " + posting_idx + " : " + content);


                        postings.add(new Post(posting_idx, writer_idx, avatar, nickname, date, title, content, longitude, latitude, likeCount, onlyme, comments));
                    }

                    break;

            }
        }else{
            Log.e("!!!=", "No Postings");
        }

        return postings;
    }

    //{
    //     "user":
    //     {
    //         "idx":3,
    //         "nickname":"박소영",
    //         "avatar":"http:\/\/post.phinf.naver.net\/MjAxNzExMDZfMjE3\/MDAxNTA5OTAzMzExOTE0.hWqSum3lvcV-c-vt4dS0XBKtw0KmUj0oHayTd8pjMpcg.uWh86C3tGlvwNnP2Jh8No0xF9DNXokGH3AGdFgBX_SAg.JPEG\/IytQi_Q_5akvz1zJBDXYrVcHQLNg.jpg",
    //         "anonymity":0
    //     },
    //     "position":{
    //         "type":"Point",
    //         "coordinates":[127.1792579,37.5647535]
    //     },
    //     "type":"LoudSpeaker",
    //     "like_count":0,
    //     "likes":[],
    //     "contents":"ㅋㅋ",
    //     "created_at":"2018-11-17T07:43:35.213Z"
    //}

    /*
     * 푸시알림 Json변환 메소드
     * */
    public static ArrayList<String> PushJsonToObj(String jsonResult){

        ArrayList<String> pm= new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        JsonObject userObject = (JsonObject) jsonObject.get("user");

        pm.add(getStringNoQuote(userObject.get("nickname").toString()));
        pm.add(getStringNoQuote(userObject.get("avatar").toString()));
        pm.add(getStringNoQuote(userObject.get("anonymity").toString()));

        pm.add(getStringNoQuote(jsonObject.get("contents").toString()));
        pm.add(getStringNoQuote(userObject.get("idx").toString()));

        //이름, 아바타, 익명, 내용, idx
        return pm;
    }

    /*
     * Posting 조회로 받아온 Json변환 메소드
     */
    public static int[] PostingCntJsonToObj(String jsonResult){
        ArrayList<Post> postings = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        int[] posting_idx;

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {

            JsonArray resultArray = (JsonArray) jsonObject.get("result");

            posting_idx = new int[resultArray.size()];

            for (int i = 0; i < resultArray.size(); i++) {
                JsonObject oneObject = (JsonObject) resultArray.get(i);

                Log.v("jsontoobj", "oneobject : " + oneObject.toString());

                posting_idx[i] = Integer.parseInt(oneObject.get("posting_idx").toString());
                Log.v("jsontoobj", "idx : " + i + ", " + posting_idx[i]);

            }
            return posting_idx;

        }else{
            Log.e("!!!=", "No Postings");
        }

        return null;
    }

    /*
     * Friends 조회로 받아온 Json변환 메소드
     */
    public static int[] FriendsJsonToObj(String jsonResult, Dbhelper dbhelper){
        Log.v("jsontoobj", "jsonresult at fjtoo : " + jsonResult);
        ArrayList<Friend> friends = new ArrayList<>();
        int[] friends_idx;

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        int user1_idx, user2_idx;

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {
            JsonObject resultObject = (JsonObject) jsonObject.get("user");

            JsonArray resultArray = (JsonArray) jsonObject.get("result");

            friends_idx = new int[resultArray.size()];

            for (int i = 0; i < resultArray.size(); i++) {
                JsonObject oneObject = (JsonObject) resultArray.get(i);

                Log.v("jsontoobj", "oneobject : " + oneObject.toString());

                user1_idx = Integer.parseInt(oneObject.get("user1_idx").toString());
                user2_idx = Integer.parseInt(oneObject.get("user2_idx").toString());

                Log.v("jsontoobj", "friendjtoo, u1idx : " + user1_idx + ", u2idx : " + user2_idx);

                if(dbhelper.getMyIdx() == user1_idx) {
//                    friends.add(new Friend(user2_idx));
                    friends_idx[i] = user2_idx;
                }
                else{
//                    friends.add(new Friend(user1_idx));
                    friends_idx[i] = user1_idx;
                }
            }
            return friends_idx;
        }else{
            Log.e("!!!=", "No Postings");
        }

        return null;
    }

    public static ArrayList<Double> getLocationContents(String contents){

        contents = contents.replace("\\", "");
        ArrayList<Double> location = new ArrayList<>();

        try{
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(contents);
            Double lat = jsonObject.get("lat").getAsDouble();
            Double lng = jsonObject.get("lng").getAsDouble();
            location.add(lat);
            location.add(lng);
        }catch (Exception e){
            return null;
        }

        return location;
    }

    public static String getAddressContents(String jsonResult){
        String address = null;

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);
        JsonArray resultArray = (JsonArray) jsonObject.get("results");
        JsonObject oneObject = (JsonObject) resultArray.get(0);

        JsonObject regionObject = (JsonObject) oneObject.get("region");
        JsonObject area1Object = (JsonObject) regionObject.get("area1");
        JsonObject area2Object = (JsonObject) regionObject.get("area2");
        JsonObject area3Object = (JsonObject) regionObject.get("area3");
        JsonObject area4Object = (JsonObject) regionObject.get("area4");
        String area1 = getStringNoQuote(String.valueOf(area1Object.get("name")));
        String area2 = getStringNoQuote(String.valueOf(area2Object.get("name")));
        String area3 = getStringNoQuote(String.valueOf(area3Object.get("name")));
        String area4 = getStringNoQuote(String.valueOf(area4Object.get("name")));

        address = area1 + " " + area2 + " " + area3 + " " + area4;
        return address;
    }

    /*
     * 유저정보 검색 Jsn변환 메소드
     * */
    public static Request SearchReqUserJsonToObj(String jsonResult){

        String nickname, avatar, date;
        int idx;
        boolean status = false;

        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")){
            // 리스폰스가 정상이고 서버 응답이 200이라면?
            JsonObject resultObject = (JsonObject) jsonObject.get("result");
            idx = Integer.parseInt(resultObject.get("idx").toString());
            nickname = getStringNoQuote(String.valueOf(resultObject.get("nickname")));
            avatar = getStringNoQuote(String.valueOf(resultObject.get("avatar")));
            date = sdf.format(dt).toString();
            Log.v("jsontoobj", "searchrequser date : " + date);

        }else{
            //리스폰스에 하자가 있다면
            Log.e("Check", jsonResult);
            //Log.e(jsonObject.get("code").toString(), jsonObject.get("message").toString());
            return null;
        }

        return new Request(idx, nickname, avatar, date);
    }


    /*
     * Posting 조회로 받아온 Json변환 메소드
     */
    public static int[] FriendsJsonToObj(String jsonResult, int num){
        Log.v("jsontoobj", "jsonresult at fjtoo : " + jsonResult);
        ArrayList<Request> reqs = new ArrayList<>();

        int[] sender_idx, receiver_idx;

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonResult);

        if(jsonObject.get("status")!=null && jsonObject.get("status").toString().equals("200")) {
            JsonArray resultArray = (JsonArray) jsonObject.get("result");

            sender_idx = new int[resultArray.size()];
            receiver_idx = new int[resultArray.size()];

            for (int i = 0; i < resultArray.size(); i++) {
                JsonObject oneObject = (JsonObject) resultArray.get(i);

                Log.v("jsontoobj", "oneobject : " + oneObject.toString());

                sender_idx[i] = Integer.parseInt(oneObject.get("sender_idx").toString());
                receiver_idx[i] = Integer.parseInt(oneObject.get("receiver_idx").toString());

                if(num==1){     // 내가 받은 요청 조회
                    return sender_idx;
                }else {             // 내가 보낸 요청 조회
                    return receiver_idx;
                }

            }

        }else{
            Log.e("!!!=", "No Postings");
        }

        return null;
    }



}


