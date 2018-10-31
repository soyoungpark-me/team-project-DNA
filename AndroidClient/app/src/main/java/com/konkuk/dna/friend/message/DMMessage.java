package com.konkuk.dna.friend.message;

public class DMMessage {
    public DMMessage(int sender, String contents, String date, String type) {
        this.sender = sender;
        this.contents = contents;
        this.date = date;
        this.type = type;
    }

    private int sender;    // 보낸이
    private String contents;     // 메시지
    private String date;        // 시간

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";     // 일반 메시지 전송
    private final String TYPE_LOCATION = "Location";   // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";         // 이미지 전송
    private final String TYPE_SHARE = "Share";         // 포스팅 공유
    private String type;

    public DMMessage(){}


    public int getSender() { return sender; }
    public void setSender(int sender) { this.sender = sender; }

    public String getContents(){
        return contents;
    }
    public void setContents(String message){
        this.contents = message;
    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
