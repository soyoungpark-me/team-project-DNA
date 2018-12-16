package com.konkuk.dna.post;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konkuk.dna.MainActivity;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.konkuk.dna.map.MapFragment;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.konkuk.dna.utils.ConvertType.DatetoStr;
import static com.konkuk.dna.utils.JsonToObj.PostingJsonToObj;
import static com.konkuk.dna.utils.ServerURL.LOCAL_HOST;

public class PostDetailActivity extends BaseActivity {
    protected DrawerLayout menuDrawer;
    private MapFragment mapFragment;
    private ScrollView postScrollView;
    private ImageView postAvatar;
    private ImageButton addFriendBtn;
    private LinearLayout deletPostBtn;
    private Button commentSaveBtn;
    private TextView postNickname, postDate, postTitle, postContent,
    postLikeBtnIcon, postLikeBtnText, postScrapBtnIcon, postScrapBtnText,
    postLikeCnt, postCommentCnt, postShareBtnIcon, postShareBtnText;
    private EditText commentEdit;
    private ListView commentList;
    private CommentAdapter commentAdapter;
    private int idx;

    private static Typeface fontAwesomeS;

    public Post getPost() {
        return post;
    }

    private Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_detail);

//        Intent intent = getIntent();
//        idx = intent.getIntExtra("pidx", 0);
//        post = new Post();
//        try {
//            post = new showPostingAsyncTask().execute(idx).get();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        Post extra = (Post) getIntent().getSerializableExtra("post");
//        post = (extra == null) ? new Post() : extra;

        init();
    }

    public void init() {
        if(fontAwesomeS == null) {
            fontAwesomeS = Typeface.createFromAsset(this.getAssets(), "fonts/fa-solid-900.ttf");
        }

        menuDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 2);

        postScrollView = (ScrollView) findViewById(R.id.postScrollView);
        postAvatar = (ImageView) findViewById(R.id.postAvatar);
        postNickname = (TextView) findViewById(R.id.postNickname);

        deletPostBtn = (LinearLayout) findViewById(R.id.myPostDeleteBtn);
        TextView postDeleteBtnText = findViewById(R.id.myPostDeleteBtnText);
        postDeleteBtnText.setTypeface(fontAwesomeS);
        deletPostBtn.setVisibility(View.VISIBLE);

        addFriendBtn = (ImageButton) findViewById(R.id.addFriendBtn);

        postDate = (TextView) findViewById(R.id.postDate);
        postTitle = (TextView) findViewById(R.id.postTitle);
        postContent = (TextView) findViewById(R.id.postContent);
        postLikeBtnIcon = (TextView) findViewById(R.id.postLikeBtnIcon);
        postLikeBtnText = (TextView) findViewById(R.id.postLikeBtnText);
        postScrapBtnIcon = (TextView) findViewById(R.id.postScrapBtnIcon);
        postScrapBtnText = (TextView) findViewById(R.id.postScrapBtnText);
        postShareBtnIcon = (TextView) findViewById(R.id.postShareBtnIcon);
        postShareBtnText = (TextView) findViewById(R.id.postShareBtnText);

        postLikeCnt = (TextView) findViewById(R.id.postLikeCnt);
        postCommentCnt = (TextView) findViewById(R.id.postCommentCnt);
//        postScrapCnt = (TextView) findViewById(R.id.postScrapCnt);

        commentEdit = (EditText) findViewById(R.id.commentEdit);
        commentList = (ListView) findViewById(R.id.commentList);
        commentSaveBtn = (Button) findViewById(R.id.commentSaveBtn);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

//        idx = (Integer)getIntent().getSerializableExtra("pidx");
        Post extra = (Post) getIntent().getSerializableExtra("post");
//        idx = extra.getPostingIdx();
        post = (extra == null) ? new Post() : extra;
        idx = post.getPostingIdx();
//        idx = extra.getPostingIdx();
//        try {
//            Log.v("postdetail", "pidx from,,,," + idx);
////            post = new showPostingAsyncTask().execute(idx).get();
////            post = (extra == null) ? new showPostingAsyncTask().execute(idx).get() : extra;
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        post = new showPostingAsyncTask().execute(idx).get();

        if (post.getAvatar() != null) {
            Picasso.get().load(post.getAvatar()).into(postAvatar);
        }
        postNickname.setText(post.getNickname());

        //내 인덱스와 포스팅의 인덱스를 비교한다.
        Dbhelper dbhelper = new Dbhelper(this);
        int myIdx = dbhelper.getMyIdx(); //dbhelper에서 내 인덱스값 저장
        dbhelper.close();

        if(post.getIdx()!= myIdx){
            //내 글이 아니라면 삭제할 수 없으므로 삭제버튼 제거
            deletPostBtn.setVisibility(View.GONE);

            if (false) { // TODO 여기에 [친구 관계가 아닌] 조건을 추가해주면 됩니다.
                addFriendBtn.setVisibility(View.GONE);
            }
        }else{
            //작성자가 내가 맞다면 삭제버튼은 유지, 친구관계 버튼은 제거
            addFriendBtn.setVisibility(View.GONE);
        }

//        Log.v("postdetail", "comment testing,,, : " + post.getComments().get(0).getAvatar());
        postDate.setText(DatetoStr(post.getDate()));
        postTitle.setText(post.getTitle());
        postContent.setText(post.getContent());

        // TODO 내가 좋아요를 누른 글인지, 스크랩 한 글인지에 따라 버튼 색깔이 달라져야 합니다.
        // TODO 지금은 좋아요와 스크랩 둘 다 해당하도록 설정되어 있는데,
        // TODO 해당하지 않을 경우 R.color.concrete를 적용해주면 됩니다!
        postLikeBtnIcon.setTextColor(getResources().getColor(R.color.grayLight));
        postLikeBtnText.setTextColor(getResources().getColor(R.color.grayLight));
        postScrapBtnIcon.setTextColor(getResources().getColor(R.color.grayLight));
        postScrapBtnText.setTextColor(getResources().getColor(R.color.grayLight));
        postShareBtnIcon.setTextColor(getResources().getColor(R.color.colorChatPurple));
        postShareBtnText.setTextColor(getResources().getColor(R.color.colorChatPurple));

        postLikeCnt.setText(post.getLikeCount()+"개");
        postCommentCnt.setText(post.getCommentCount()+"개");
//        postScrapCnt.setText(post.getScrapCount()+"개");
        commentAdapter = new CommentAdapter(this, R.layout.post_comment_item, post.getComments());
        commentList.setAdapter(commentAdapter);

        // 댓글 갯수에 맞춰서 height 설정하기
        final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        for (int i = 0; i < commentAdapter.getCount(); i++) {
            View childView = commentAdapter.getView(i, null, commentList);
            childView.measure(UNBOUNDED, UNBOUNDED);
            totalHeight += childView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = commentList.getLayoutParams();
        params.height = totalHeight + (commentList.getDividerHeight() * (commentList.getCount() - 1));
        commentList.setLayoutParams(params);
        commentList.requestLayout();

        // 생성된 후 최상단으로 스크롤을 올려줍니다
        postScrollView.smoothScrollTo(0, 0);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;

            case R.id.menuBtn: // 메뉴 버튼 클릭
                if (!menuDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    menuDrawer.openDrawer(Gravity.RIGHT);
                }
                break;

            case R.id.addFriendBtn: // 친구 추가 버튼 클릭
                Log.d("PostDetail", "add friend");
                new addFriendAsync(this).execute(post.getIdx());
                break;

            case R.id.myPostDeleteBtn: // 포스트 삭제 버튼
                Log.d("PostDetail", "delete post");
                new PostingAsyncTask(this, post, postLikeCnt, postLikeBtnIcon, postLikeBtnText, postScrapBtnIcon, postScrapBtnText).execute(3, idx);
                break;

            case R.id.postLikeBtn: // 좋아요 버튼 클릭 : 1
                Log.d("PostDetail", "like");
                new PostingAsyncTask(this, post, postLikeCnt, postLikeBtnIcon, postLikeBtnText, postScrapBtnIcon, postScrapBtnText).execute(1, idx);
                break;

            case R.id.postShareBtn:
                FragmentManager fragmentManager = getFragmentManager();
                PostShareFragment postShareFragment = new PostShareFragment();

                postShareFragment.show(fragmentManager, "postShareFragment");
                break;

            case R.id.postScrapBtn: // 스크랩 버튼 클릭 : 2
                Log.d("PostDetail", "scrap");
                new PostingAsyncTask(this, post, postLikeCnt, postLikeBtnIcon, postLikeBtnText, postScrapBtnIcon, postScrapBtnText).execute(2, idx);
                break;

            case R.id.commentSaveBtn:
                Log.d("PostDetail", "save comment");
                new writeCommentAsync(this, postCommentCnt, post, commentAdapter, commentList, postScrollView, commentEdit).execute(String.valueOf(idx), commentEdit.getText().toString());
                break;
        }
       // onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.initMapCenter(post.getLongitude(), post.getLatitude(), 0);
    }

}

class showPostingAsyncTask extends AsyncTask<Integer, Integer, Post> {
    String res = null;

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }

    @Override
    protected Post doInBackground(Integer... ints) {
        HttpReqRes httpReqRes = new HttpReqRes();
        Post post = new Post();
        int idx = ints[0];
        Log.v("postdetail", "idx in asynctassk : " + idx);
        res = httpReqRes.requestHttpGetPosting("https://dna.soyoungpark.me:9013/api/posting/show/" + idx);

        post = PostingJsonToObj(res, 2).get(0);
//        Log.v("postdetail", "comment" + post.getComments().get(0).getAvatar());

        return post;
    }

//    @Override
//    protected void onPostExecute(Post posting) {
//        super.onPostExecute(posting);
//
//    }
}

class PostingAsyncTask extends AsyncTask<Integer, Integer, Integer> {
    Context context;
    Dbhelper dbhelper;
    TextView postLikeBtnIcon,postLikeBtnText,postScrapBtnIcon, postScrapBtnText, postLikeCnt;
    Post post;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public PostingAsyncTask(Context context, Post post, TextView postLikeCnt, TextView postLikeBtnIcon, TextView postLikeBtnText, TextView postScrapBtnIcon, TextView postScrapBtnText){
        this.context = context;
        this.post = post;
        this.postLikeCnt = postLikeCnt;
        this.postLikeBtnIcon = postLikeBtnIcon;
        this.postLikeBtnText = postLikeBtnText;
        this.postScrapBtnIcon = postScrapBtnIcon;
        this.postScrapBtnText = postScrapBtnText;
    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        dbhelper = new Dbhelper(context);
        HttpReqRes httpReqRes = new HttpReqRes();
//        Post post = new Post();
        int num = ints[0];
        String res = null;
        int ret = 0;

        switch (num) {
            case 1:     // 포스팅 라잌
                res = httpReqRes.requestHttpPosting("https://dna.soyoungpark.me:9013/api/posting/like/" + ints[1], dbhelper.getAccessToken(), 1);

                if (res.matches("(.*)201(.*)")) {
                    Log.v("postdetail", "status : 201");
                    ret = 1;
                    break;
                } else if (res.matches("(.*)400(.*)")) {
                    Log.v("postdetail", "status : 400");
                    res = httpReqRes.requestHttpPosting("https://dna.soyoungpark.me:9013/api/posting/like/" + ints[1], dbhelper.getAccessToken(), 2);
                    ret = 2;
                }
                break;

            case 2:     // 포스팅 북마크
                res = httpReqRes.requestHttpPosting("https://dna.soyoungpark.me:9013/api/posting/bookmark/" + ints[1], dbhelper.getAccessToken(), 1);

                if (res.matches("(.*)201(.*)")) {
                    Log.v("postdetail", "status : 201");
                    ret = 3;
                    break;
                } else if (res.matches("(.*)400(.*)")) {
                    Log.v("postdetail", "status : 400");
                    res = httpReqRes.requestHttpPosting("https://dna.soyoungpark.me:9013/api/posting/bookmark/" + ints[1], dbhelper.getAccessToken(), 2);
                    ret = 4;
                }
                break;

            case 3:
                res = httpReqRes.requestHttpPosting("https://dna.soyoungpark.me:9013/api/posting/" + ints[1], dbhelper.getAccessToken(), 2);
                Log.v("postdetail", "delete res : " + res);
                break;
        }

        String result = httpReqRes.requestHttpGetPosting("https://dna.soyoungpark.me:9013/api/posting/show/" + ints[1]);
        post = PostingJsonToObj(result, 2).get(0);

        dbhelper.close();
        return ret;
    }

    @Override
    protected void onPostExecute(Integer num) {
        super.onPostExecute(num);

        switch(num) {
            case 1: // 라잌
                postLikeCnt.setText(post.getLikeCount()+"개");
                postLikeBtnIcon.setTextColor(context.getResources().getColor(R.color.alizarin));
                postLikeBtnText.setTextColor(context.getResources().getColor(R.color.alizarin));
                break;

            case 2: //언라잌
                postLikeCnt.setText(post.getLikeCount()+"개");
                postLikeBtnIcon.setTextColor(context.getResources().getColor(R.color.grayLight));
                postLikeBtnText.setTextColor(context.getResources().getColor(R.color.grayLight));
                break;

            case 3: // 북마크
                postScrapBtnIcon.setTextColor(context.getResources().getColor(R.color.sunflower));
                postScrapBtnText.setTextColor(context.getResources().getColor(R.color.sunflower));
                break;

            case 4: // 언북맠
                postScrapBtnIcon.setTextColor(context.getResources().getColor(R.color.grayLight));
                postScrapBtnText.setTextColor(context.getResources().getColor(R.color.grayLight));
                break;
        }
    }
}

class writeCommentAsync extends AsyncTask<String, String, Post> {
    private Context context;
    private Dbhelper dbhelper;
    TextView postCommentCnt;
    Post post;
    CommentAdapter commentAdapter;
    ListView commentList;
    ScrollView postScrollView;
    EditText commentEdit;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    public writeCommentAsync(Context context, TextView postCommentCnt, Post post, CommentAdapter commentAdapter, ListView commentList, ScrollView postScrollView, EditText commentEdit){
        this.context = context;
        this.postCommentCnt = postCommentCnt;
        this.post = post;
        this.commentAdapter = commentAdapter;
        this.commentList = commentList;
        this.postScrollView = postScrollView;
        this.commentEdit = commentEdit;
    }

    @Override
    protected Post doInBackground(String... strings){
        HttpReqRes httpReqRes = new HttpReqRes();
        dbhelper = new Dbhelper(context);
        httpReqRes.requestHttpPostWriteComment("https://dna.soyoungpark.me:9013/api/posting/reply/" + Integer.parseInt(strings[0]), dbhelper, strings[1]);

        String res = httpReqRes.requestHttpGetPosting("https://dna.soyoungpark.me:9013/api/posting/show/" + Integer.parseInt(strings[0]));
        Post posting = PostingJsonToObj(res, 2).get(0);
        dbhelper.close();
        return posting;
    }

    @Override
    protected void onPostExecute(Post posting) {
        super.onPostExecute(posting);
        post = posting;
        postCommentCnt.setText(post.getCommentCount()+"개");
//        postScrapCnt.setText(post.getScrapCount()+"개");
        commentAdapter = new CommentAdapter(context, R.layout.post_comment_item, post.getComments());
        commentList.setAdapter(commentAdapter);

        // 댓글 갯수에 맞춰서 height 설정하기
        final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        for (int i = 0; i < commentAdapter.getCount(); i++) {
            View childView = commentAdapter.getView(i, null, commentList);
            childView.measure(UNBOUNDED, UNBOUNDED);
            totalHeight += childView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = commentList.getLayoutParams();
        params.height = totalHeight + (commentList.getDividerHeight() * (commentList.getCount() - 1));
        commentList.setLayoutParams(params);
        commentList.requestLayout();

        commentEdit.setText("");

        // 생성된 후 최상단으로 스크롤을 올려줍니다
        postScrollView.smoothScrollTo(0, 0);
//        HttpReqRes httpReqRes = new HttpReqRes();
//        httpReqRes.requestHttpGetPosting("https://dna.soyoungpark.me:9013/api/posting/show/" + idx);
    }
}

class addFriendAsync extends AsyncTask<Integer, String, Void> {
    private Context context;
    private Dbhelper dbhelper;

    //    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
    public addFriendAsync(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... ints) {
        HttpReqRes httpReqRes = new HttpReqRes();
        dbhelper = new Dbhelper(context);

        httpReqRes.requestHttpPostAddFriend("https://dna.soyoungpark.me:9013/api/friends/", dbhelper, ints[0]);

        dbhelper.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void voids) {

        super.onPostExecute(voids);
    }
}