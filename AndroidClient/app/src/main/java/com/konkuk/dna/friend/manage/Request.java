package com.konkuk.dna.friend.manage;

public class Request {
    int idx;
    String nickname;
    String avatar;
    String date;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Request(int idx, String nickname, String avatar, String date) {
        this.idx = idx;
        this.nickname = nickname;
        this.avatar = avatar;
        this.date = date;
    }
}

