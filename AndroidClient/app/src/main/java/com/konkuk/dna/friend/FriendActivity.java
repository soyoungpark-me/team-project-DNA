package com.konkuk.dna.friend;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.R;
import com.konkuk.dna.friend.fragments.FriendFragment;
import com.konkuk.dna.friend.fragments.NotifyFragment;
import com.konkuk.dna.friend.fragments.RoomFragment;
import com.konkuk.dna.utils.helpers.InitHelpers;

public class FriendActivity extends BaseActivity implements View.OnClickListener {
    final int ROOM_FRAGMENT = 1;
    final int FRIEND_FRAGMENT = 2;
    final int NOTIFY_FRAGMENT = 3;

    private DrawerLayout menuDrawer;
    private Button roomBtn, friendBtn, notifyBtn;
    private ViewPager roomFragContainer;

//    private FrameLayout roomFragContainer;

    int currentFragment; // 현재 프래그먼트 구별하기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        init();
    }

    public void init() {
        menuDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 2);
        roomFragContainer = (ViewPager) findViewById(R.id.roomFragContainer);
        roomBtn = (Button) findViewById(R.id.roomBtn);
        friendBtn = (Button) findViewById(R.id.friendBtn);
        notifyBtn = (Button) findViewById(R.id.notifyBtn);

        final Button buttons[] = new Button[]{ roomBtn, friendBtn, notifyBtn };

        roomFragContainer.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        roomFragContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                for(Button button: buttons) {
                    button.setSelected(false);
                }
                buttons[position].setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        roomFragContainer.setCurrentItem(0);
        buttons[0].setSelected(true);

        roomBtn.setOnClickListener(movePageListener);
        friendBtn.setOnClickListener(movePageListener);
        notifyBtn.setOnClickListener(movePageListener);
    }

    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int)((String)v.getTag()).charAt(0) - 48;
            roomFragContainer.setCurrentItem(tag);
        }
    };

    @Override
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
        }
    }

    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new RoomFragment();
                case 1:
                    return new FriendFragment();
                case 2:
                    return new NotifyFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 3;
        }
    }
}