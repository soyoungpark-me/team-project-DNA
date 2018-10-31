package com.konkuk.dna.user;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.AnimHelpers;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.konkuk.dna.post.Comment;
import com.konkuk.dna.post.Post;

import java.util.ArrayList;
import java.util.Arrays;

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
        myPosts.add(new Post(1, "http://slingshotesports.com/wp-content/uploads/2017/07/34620595595_b4c90a2e22_b.jpg",
                "3457soso", "2018.10.05", "제목입니다",
                "이건 내용인데 사실 많이 쓸 필요는 없긴 한데... \n그래도 왠지 많이 써야할 것 같아서 쓰긴 씁니다.\n메롱메롱\n페이커가 최고임",
                127.081958, 37.537484, 1, 2, 3,
                new ArrayList<Comment>(
                        Arrays.asList(new Comment(null,"test","2018.10.05","이건 댓글입니다."),
                                new Comment(null,"test","2018.10.05","이건 댓글입니다."))
                )
        ));
        myPosts.add(new Post(2, "http://slingshotesports.com/wp-content/uploads/2017/07/34620595595_b4c90a2e22_b.jpg",
                "3457soso", "2018.10.05", "제목입니다22",
                "이건 내용인데 사실 많이 쓸 필요는 없긴 한데... \n그래도 왠지 많이 써야할 것 같아서 쓰긴 씁니다.\n메롱메롱\n페이커가 최고임",
                127.083559, 37.535743, 3, 2, 1,
                new ArrayList<Comment>(
                        Arrays.asList(new Comment(null,"test","2018.10.05","이건 댓글입니다."),
                                new Comment(null,"test","2018.10.05","이건 댓글입니다."))
                )
        ));
        scrapPosts.add(new Post(3, "http://slingshotesports.com/wp-content/uploads/2017/07/34620595595_b4c90a2e22_b.jpg",
                "3457soso", "2018.10.05", "제목입니다33",
                "이건 내용인데 사실 많이 쓸 필요는 없긴 한데... \n그래도 왠지 많이 써야할 것 같아서 쓰긴 씁니다.\n메롱메롱\n페이커가 최고임",
                127.083559, 37.536343, 2, 1, 3,
                new ArrayList<Comment>(
                        Arrays.asList(new Comment(null,"test","2018.10.05","이건 댓글입니다."),
                                new Comment(null,"test","2018.10.05","이건 댓글입니다."))
                )
        ));

        /* 내가 쓴 포스팅 */
        myPostListAdatper = new UserPostListAdapter(this, R.layout.post_list_item, myPosts, false);
        myPostList.setAdapter(myPostListAdatper);

        /* 스크랩한 포스팅 */
        scrapPostListAdatper = new UserPostListAdapter(this, R.layout.post_list_item, scrapPosts, true);
        scrapPostList.setAdapter(scrapPostListAdatper);
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
