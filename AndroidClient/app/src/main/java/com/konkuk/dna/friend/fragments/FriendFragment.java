package com.konkuk.dna.friend.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.konkuk.dna.utils.helpers.AnimHelpers;
import com.konkuk.dna.utils.helpers.BaseFragment;
import com.konkuk.dna.friend.manage.FriendDetailFragment;
import com.konkuk.dna.friend.manage.OnFriendListAdapter;
import com.konkuk.dna.R;
import com.konkuk.dna.friend.manage.Friend;
import com.konkuk.dna.friend.manage.FriendListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends BaseFragment implements View.OnClickListener {
    private EditText friendSearchEdit;
    private ImageButton friendSearchBtn;
    private OnFriendListAdapter onFriendListAdapter;
    private FriendListAdapter allFriendListAdapter;

    private RecyclerView onFriendList;
    private ListView allFriendList;
    private ArrayList<Friend> onFriends, allFriends;

    public FriendFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    public void init() {
        friendSearchEdit = (EditText) getView().findViewById(R.id.friendSearchEdit);
        friendSearchBtn = (ImageButton) getView().findViewById(R.id.friendSearchBtn);
        onFriendList = (RecyclerView) getView().findViewById(R.id.onFriendList);
        allFriendList = (ListView) getView().findViewById(R.id.allFriendList);
        onFriends = new ArrayList<Friend>();
        allFriends = new ArrayList<Friend>();

        if (friendSearchBtn != null) friendSearchBtn.setOnClickListener(this);

        onFriends.add(new Friend("3457soso", "socoing", "http://slingshotesports.com/wp-content/uploads/2017/07/34620595595_b4c90a2e22_b.jpg", "자기소개", true));
        onFriends.add(new Friend("3457soso", "socoing", null, "ㅋㅋ", true));
        onFriends.add(new Friend("3457soso", "socoing", null, "", true));
        onFriends.add(new Friend("3457soso", "socoing", null, "", true));
//        onFriends.add(new Friend("3457soso", "socoing", null, "", true));
//        onFriends.add(new Friend("3457soso", "socoing", null, "", true));
//        onFriends.add(new Friend("3457soso", "socoing", null, "", true));
//        onFriends.add(new Friend("3457soso", "socoing", null, "", true));

        allFriends.add(new Friend("3457soso", "socoing", "http://slingshotesports.com/wp-content/uploads/2017/07/34620595595_b4c90a2e22_b.jpg", "상태 메시지", true));
        allFriends.add(new Friend("3457soso", "socoing", null, "", false));
        allFriends.add(new Friend("3457soso", "socoing", null, "", false));
        allFriends.add(new Friend("3457soso", "socoing", null, "ㅋㅋㅎㅁ", true));
        allFriends.add(new Friend("3457soso", "socoing", null, "", false));
        allFriends.add(new Friend("3457soso", "socoing", null, "", false));

        /* 접속 중인 친구 */
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        onFriendList.setLayoutManager(layoutManager);
        onFriendListAdapter = new OnFriendListAdapter(getContext(), onFriends);
        onFriendList.setAdapter(onFriendListAdapter);

        /* 모든 친구 */
        allFriendListAdapter = new FriendListAdapter(getContext(), 0, allFriends);
        allFriendList.setAdapter(allFriendListAdapter);
        AnimHelpers.setListViewHeight(allFriendList);
        allFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FriendDetailFragment friendDetailFragment = new FriendDetailFragment();

                friendDetailFragment.setData(allFriends.get(i));
                friendDetailFragment.show(fragmentManager, "friendDetailFragment");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.friendSearchBtn: // 검색 버튼 클릭
                Log.d("test", "검색 버튼 클릭됨");
                break;
        }
    }
}
