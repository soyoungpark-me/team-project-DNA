package com.konkuk.dna.friend.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konkuk.dna.R;
import com.konkuk.dna.utils.EventListener;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.ServerURL;
import com.konkuk.dna.utils.dbmanage.Dbhelper;

import com.konkuk.dna.friend.message.DMActivity;
import com.konkuk.dna.friend.message.DMRoom;
import com.konkuk.dna.friend.message.DMRoomListAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.konkuk.dna.utils.JsonToObj.DMRoomJsonToObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomFragment extends Fragment {
    private ListView roomList;
    private ArrayList<DMRoom> rooms;
    private DMRoomListAdapter dmRoomListAdapter;

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";     // 일반 메시지 전송
    private final String TYPE_LOCATION = "Location";    // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";       // 이미지 전송

    private static final int SOCKET_DIRECT = 7;

    public RoomFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public void onResume() {

        DMRoomAsyncTask dmrat = new DMRoomAsyncTask(getActivity(), dmRoomListAdapter, roomList);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            dmrat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            dmrat.execute();
        }

        super.onResume();
    }

    public void init() {
        roomList = (ListView) getView().findViewById(R.id.roomList);
        rooms = new ArrayList<>();

        // TODO 서버에서 room 리스트를 받아와서 초기화시켜줘야 합니다.
        DMRoomAsyncTask dmrat = new DMRoomAsyncTask(getActivity(), dmRoomListAdapter, roomList);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            dmrat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            dmrat.execute();
        }
//        rooms.add(new DMRoom(0, 1, "3457soso", "https://pbs.twimg.com/media/DbYfg2IWkAENdiS.jpg", "내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용", TYPE_MESSAGE, "2018-01-24"));
//        rooms.add(new DMRoom(1, 2, "test", null, "마지막 메시지2", TYPE_MESSAGE, "2018-01-23"));
//        rooms.add(new DMRoom(2, 3, "avatar", null, "마지막 메시지2", TYPE_MESSAGE, "2018-01-22"));
//
//        dmRoomListAdapter = new DMRoomListAdapter(getActivity(), R.layout.chat_item_room, rooms);
//        roomList.setAdapter(dmRoomListAdapter);

        roomList.setClickable(true);
        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                DMRoom room = (DMRoom) roomList.getItemAtPosition(position);
                Intent intent = new Intent (getContext(), DMActivity.class);
                intent.putExtra("roomIdx", room.getIdx());
                intent.putExtra("roomWho", room.getNickname());
                intent.putExtra("roomUpdated", room.getUpdateDate());

                if(getActivity().getIntent().getIntExtra("postNum", -1) != -1) {
                    intent.putExtra("postNum", getActivity().getIntent().getIntExtra("postNum", -1));
                    intent.putExtra("postTitle", getActivity().getIntent().getStringExtra("postTitle"));
                }
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventListener(EventListener event) {

        switch (event.message) {
            case SOCKET_DIRECT:
                DMRoomAsyncTask dmrat = new DMRoomAsyncTask(getActivity(), dmRoomListAdapter, roomList);
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                    dmrat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    dmrat.execute();
                }
                break;
            default:
                break;
        }
    }
}


/*
 * 비동기 Http 연결 작업 클래스
 * */
class DMRoomAsyncTask extends AsyncTask<Double, Integer, ArrayList<DMRoom>> {

    private Context context;
    private String m_token;

    private Dbhelper dbhelper;

    private DMRoomListAdapter dmRoomListAdapter;
    private ListView roomList;


    public DMRoomAsyncTask(Context context, DMRoomListAdapter dmRoomListAdapter, ListView roomList){
        this.context=context;
        this.dmRoomListAdapter = dmRoomListAdapter;
        this.roomList = roomList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<DMRoom> doInBackground(Double... doubles) {

        //ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
        ArrayList<DMRoom> rooms = new ArrayList<>();

        HttpReqRes httpreq = new HttpReqRes();
        dbhelper = new Dbhelper(context);
        m_token = dbhelper.getAccessToken();

        String repDMRooms = httpreq.requestHttpGETDMRooms(ServerURL.DNA_SERVER+ServerURL.PORT_SOCKET_API+"/rooms/", m_token);

        Log.e("!!!!!!!", repDMRooms);

        //TODO 오브젝트 치환
        rooms = DMRoomJsonToObj(repDMRooms, dbhelper.getMyIdx());
//
//        rooms.add(new DMRoom(0, 1, "3457soso", "https://pbs.twimg.com/media/DbYfg2IWkAENdiS.jpg", "내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용", TYPE_MESSAGE, "2018-01-24"));
//        rooms.add(new DMRoom(1, 2, "test", null, "마지막 메시지2", TYPE_MESSAGE, "2018-01-23"));
//        rooms.add(new DMRoom(2, 3, "avatar", null, "마지막 메시지2", TYPE_MESSAGE, "2018-01-22"));

        return rooms;
    }

    @Override
    protected void onPostExecute(ArrayList<DMRoom> rooms) {
        super.onPostExecute(rooms);

        dmRoomListAdapter = new DMRoomListAdapter(context, R.layout.chat_item_room, rooms);
        roomList.setAdapter(dmRoomListAdapter);

        dmRoomListAdapter.notifyDataSetChanged();
    }
}
