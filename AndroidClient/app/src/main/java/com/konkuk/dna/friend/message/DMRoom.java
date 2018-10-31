package com.konkuk.dna.friend.message;

import java.util.ArrayList;

public class DMRoom {
    private int idx;
    private int userIdx;
    private String nickname;
    private String avatar;
    private String lastMessage;
    private String lastType;
    private String updateDate;

    public DMRoom(int idx, int userIdx, String nickname, String avatar,
                  String lastMessage, String lastType, String updateDate) {
        this.idx = idx;
        this.userIdx = userIdx;
        this.nickname = nickname;
        this.avatar = avatar;
        this.lastMessage = lastMessage;
        this.lastType = lastType;
        this.updateDate = updateDate;
    }

    public int getIdx() { return idx; }
    public void setIdx(int idx) { this.idx = idx; }

    public int getUserIdx() { return userIdx; }
    public void setUserIdx(int userIdx) { this.userIdx = userIdx; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastType() { return lastType; }
    public void setLastType(String lastType) { this.lastType = lastType; }

    public String getUpdateDate() { return updateDate; }
    public void setUpdateDate(String updateDate) { this.updateDate = updateDate; }
}
