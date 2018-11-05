package com.konkuk.dna.chat;

public class ChatUser {
    private int idx;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private String ID;
    private String nickname;
    private String avatar;
    private String info;
    private Boolean inside;


    public ChatUser(int idx, String nickname, String avatar, Boolean inside) {
        this.idx = idx;
        this.nickname = nickname;
        this.avatar = avatar;
        this.inside = inside;
    }

    public ChatUser(int idx, String ID, String nickname, String avatar, String info, Boolean inside) {
        this.idx = idx;
        this.nickname = nickname;
        this.avatar = avatar;
        this.inside = inside;
        this.info = info;
        this.ID = ID;
    }


    public int getIdx() {
        return idx;
    }
    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getInside() {
        return inside;
    }
    public void setInside(Boolean inside) {
        this.inside = inside;
    }
}
