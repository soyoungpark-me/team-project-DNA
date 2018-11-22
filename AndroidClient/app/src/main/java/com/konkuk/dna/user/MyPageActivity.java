package com.konkuk.dna.user;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konkuk.dna.post.PostDetailActivity;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.AnimHelpers;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.konkuk.dna.post.Comment;
import com.konkuk.dna.post.Post;

import java.util.ArrayList;
import java.util.Arrays;

import static com.konkuk.dna.utils.JsonToObj.PostingJsonToObj;

public class MyPageActivity extends BaseActivity {
    protected DrawerLayout menuDrawer;
    private LinearLayout myPageProfile;
    private TextView myPageInfo, myPostAngle, scrapPostAngle;
    private ListView myPostList, scrapPostList;
    private UserPostListAdapter myPostListAdatper, scrapPostListAdatper;
    private ArrayList<Post> myPosts;
    private ArrayList<Post> scrapPosts;

    private boolean myPostListIsOpen = true, scrapPostListIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_page);

        init();
    }

    public void init() {
        menuDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 2);

        myPageProfile = (LinearLayout) findViewById(R.id.myPageProfile);
        myPageInfo = (TextView) findViewById(R.id.myPageInfo);
        myPostAngle = (TextView) findViewById(R.id.myPostAngle);
        scrapPostAngle = (TextView) findViewById(R.id.scrapPostAngle);
        myPostList = (ListView) findViewById(R.id.myPostList);
        scrapPostList = (ListView) findViewById(R.id.scrapPostList);
        InitHelpers.setProfile(myPageProfile);

        myPosts = new ArrayList<Post>();
        scrapPosts = new ArrayList<Post>();

        // TODO 내가 작성한 포스트와 스크랩한 포스트의 리스트를 서버에서 불러와 추가해줘야 합니다.
        try {
            myPosts = new myPostingAsync(this).execute(0).get();
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            scrapPosts = new myPostingAsync(this).execute(1).get();
        } catch (Exception e){
            e.printStackTrace();
        }

        /* 내가 쓴 포스팅 */
        myPostListAdatper = new UserPostListAdapter(this, R.layout.post_list_item, myPosts, false);
        myPostList.setAdapter(myPostListAdatper);

        /* 스크랩한 포스팅 */
        scrapPostListAdatper = new UserPostListAdapter(this, R.layout.post_list_item, scrapPosts, true);
        scrapPostList.setAdapter(scrapPostListAdatper);

        myPostList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent postIntent = new Intent(MyPageActivity.this, PostDetailActivity.class);
                postIntent.putExtra("post", myPosts.get(i));
                startActivity(postIntent);
            }
        });

        scrapPostList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent postIntent = new Intent(MyPageActivity.this, PostDetailActivity.class);
                postIntent.putExtra("post", scrapPosts.get(i));
                startActivity(postIntent);
            }
        });
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

            case R.id.myPostBtn: // 내가 쓴 포스팅 버튼 클릭
                if (myPostListIsOpen) {
                    myPostAngle.animate().rotation(180).start();
                    AnimHelpers.animateListHeight(this, myPostList, 63, myPosts.size(), 0);
                } else {
                    myPostAngle.animate().rotation(0).start();
                    AnimHelpers.animateListHeight(this, myPostList, 63, 0, myPosts.size());
                }
                myPostListIsOpen = !myPostListIsOpen;
                break;

            case R.id.scrapPostBtn: // 스크랩한 포스팅 버튼 클릭
                if (scrapPostListIsOpen) {
                    scrapPostAngle.animate().rotation(0).start();
                    AnimHelpers.animateListHeight(this, scrapPostList, 63, scrapPosts.size(), 0);
                } else {
                    scrapPostAngle.animate().rotation(-180).start();
                    AnimHelpers.animateListHeight(this, scrapPostList, 63, 0, scrapPosts.size());
                }
                scrapPostListIsOpen = !scrapPostListIsOpen;
                break;

            case R.id.profileUpdateBtn: // 프로필 수정 버튼 클릭
                Intent updateIntent = new Intent(this, UserFormActivity.class);
                startActivity(updateIntent);
                break;
        }
    }
}

class myPostingAsync extends AsyncTask<Integer, Void, ArrayList<Post>> {

    private Context context;
    private Dbhelper dbhelper;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public myPostingAsync(Context context){
        this.context = context;
    }

    @Override
    protected ArrayList<Post> doInBackground(Integer... ints){
        ArrayList<Post> postings = new ArrayList<>();
        String result = null;

        HttpReqRes httpReqRes = new HttpReqRes();
        dbhelper = new Dbhelper(context);

        switch(ints[0]){
            case 0:
                result = httpReqRes.requestHttpGetWASPIwToken("https://dna.soyoungpark.me:9013/api/posting/showMine/", dbhelper.getAccessToken());
                break;
            case 1:
                result = httpReqRes.requestHttpGetWASPIwToken("https://dna.soyoungpark.me:9013/api/posting/bookmark/", dbhelper.getAccessToken());
                break;
        }

        Log.v("mypageactivity", "show bookmark httpreq result" + result);
        postings = PostingJsonToObj(result, 1);

//        Log.v("mypageactivity", "postings test : " + postings.get(0).getContent());
        return postings;
    }

    @Override
    protected void onPostExecute(ArrayList<Post> postings) {

        super.onPostExecute(postings);
    }
}
