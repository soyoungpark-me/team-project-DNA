package com.konkuk.dna.chat;

import java.util.ArrayList;

public class ChatMessage {
    private int idx;            // 인덱스
    private String userName;    // 보낸이
    private String avatar;      // 프로필 이미지 url
    private String contents;     // 메시지

    private String date;        // 시간
    private String like;        // 좋아요
    private double lng;         // 경도
    private double lat;         // 위도

    private ArrayList<Integer> whoLikes;
    private int msg_idx;
    private String type;

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";          // 일반 메시지 전송
    private final String TYPE_LOUDSPEAKER = "LoudSpeaker";  // 확성기 전송
    private final String TYPE_LOCATION = "Location";        // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";              // 이미지 전송
    private final String TYPE_SHARE = "Share";              // 포스팅 공유

    private int viewType;


    public ChatMessage(){}


    public ChatMessage(int idx, String userName, String avatar, String contents, String date,
                       String like, String type, double lng, double lat, ArrayList<Integer> whoLikes, int msg_idx, int viewType){
        this.idx = idx;
        this.userName = userName;
        this.avatar = avatar;
        this.contents = contents;
        this.date = date;
        this.like = like;
        this.type = type;
        this.lng = lng;
        this.lat = lat;
        this.whoLikes = whoLikes;
        this.msg_idx = msg_idx;
        this.viewType = viewType;

    }

    public int getIdx() { return idx; }
    public void setIdx(int idx) { this.idx = idx; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContents() { return contents; }
    public void setMessage(String contents){
        this.contents = contents;
    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getLike() { return like; }
    public void setLike(String like) { this.like = like; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public ArrayList<Integer> getWhoLikes() {
        return whoLikes;
    }
    public void setWhoLikes(ArrayList<Integer> whoLikes) {
        this.whoLikes = whoLikes;
    }

    public int getMsg_idx() {
        return msg_idx;
    }
    public void setMsg_idx(int msg_idx) {
        this.msg_idx = msg_idx;
    }

    public int getViewType() {
        return viewType;
    }
    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
