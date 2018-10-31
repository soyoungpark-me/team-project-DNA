package com.konkuk.dna.post;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Post implements Serializable{
    private int idx;
    private String avatar;
    private String nickname;

    private String date;
    private String title;
    private String content;
    private double longitude;
    private double latitude;

    private ArrayList<Comment> comments;

    public Post() {}

    public Post(int idx, String avatar, String nickname, String date, String title, String content,
                double longitude, double latitude, int likeCount, int commentCount, int scrapCount,
                ArrayList<Comment> comments) {
        this.idx = idx;
        this.avatar = avatar;
        this.nickname = nickname;
        this.date = date;
        this.title = title;
        this.content = content;
        this.longitude = longitude;
        this.latitude = latitude;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.scrapCount = scrapCount;
        this.comments = comments;
    }

    private int likeCount;
    private int commentCount;
    private int scrapCount;

    public int getIdx() {
        return idx;
    }
    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getScrapCount() {
        return scrapCount;
    }
    public void setScrapCount(int scrapCount) {
        this.scrapCount = scrapCount;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public int commentCount() {
        return comments.size();
    }
}
