package com.konkuk.dna.friend.manage;

public class Friend {
    private String id;
    private String nickname;
    private String avatar;
    private String info;
    private Boolean status;

    public Friend(String id, String nickname, String avatar, String info, Boolean status) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.info = info;
        this.status = status;
    }

    public String getID() { return id; }
    public void setID(String id) { this.id = id; }

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

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean inside) {
        this.status = status;
    }
}
