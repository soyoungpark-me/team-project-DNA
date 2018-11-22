package com.konkuk.dna.post;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Post implements Serializable{
    private int writer_idx;
    private String avatar;
    private String nickname;

    private int posting_idx;
    private String date;
    private String title;
    private String content;
    private double longitude;
    private double latitude;
    private Boolean onlyme;

    private ArrayList<Comment> comments;

    public Post() {}

    public Post(int posting_idx, int writer_idx, String avatar, String nickname, String date, String title, String content,
                double longitude, double latitude, int likeCount, boolean onlyme, ArrayList<Comment> comments) {
        this.writer_idx = writer_idx;
        this.avatar = avatar;
        this.nickname = nickname;
        this.posting_idx = posting_idx;
        this.date = date;
        this.title = title;
        this.content = content;
        this.longitude = longitude;
        this.latitude = latitude;
        this.likeCount = likeCount;
        this.onlyme = onlyme;
        this.commentCount = commentCount;
//        this.scrapCount = scrapCount;
        this.comments = comments;
    }

    private int likeCount;
    private int commentCount;
//    private int scrapCount;

    public int getIdx() {
        return writer_idx;
    }
    public void setIdx(int idx) {
        this.writer_idx = writer_idx;
    }

    public int getPostingIdx() {
        return posting_idx;
    }
    public void setPostingIdx(int idx) {
        this.posting_idx = posting_idx;
    }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean getOnlyme() {
        return onlyme;
    }
    public void setOnlyme(boolean onlyme) {
        this.onlyme = onlyme;
    }

    public int getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

//    public int getScrapCount() {
//        return scrapCount;
//    }
//    public void setScrapCount(int scrapCount) {
//        this.scrapCount = scrapCount;
//    }

    public ArrayList<Comment> getComments() {
        return comments;
    }
//    public void setComments(ArrayList<Comment> comments) {
//        this.comments = comments;
//    }

//    public int commentCount() {
//        return comments.size();
//    }
}
