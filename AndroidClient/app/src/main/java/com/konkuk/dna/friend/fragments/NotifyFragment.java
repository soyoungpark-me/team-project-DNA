package com.konkuk.dna.friend.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konkuk.dna.R;
import com.konkuk.dna.friend.manage.Friend;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.ServerURL;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.AnimHelpers;
import com.konkuk.dna.utils.helpers.BaseFragment;
import com.konkuk.dna.friend.manage.Request;
import com.konkuk.dna.friend.manage.RequestListAdapter;

import java.util.ArrayList;

import static com.konkuk.dna.utils.HttpReqRes.requestHttpGETUserInfo;
import static com.konkuk.dna.utils.JsonToObj.FriendsJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.SearchReqUserJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.SearchUserJsonToObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends BaseFragment implements View.OnClickListener {
    private Context context = getContext();
    private Dbhelper dbhelper;
    private TextView receivedAngle, acceptedAngle, sendedAngle;
    private ListView receivedList, acceptedList, sendedList;
    private RequestListAdapter receivedListAdapter, acceptedListAdapter, sendedListAdapter;
    private ArrayList<Request> received;
//    private ArrayList<Request> accepted;
    private ArrayList<Request> sended;
    private RelativeLayout receivedBtn, acceptedBtn, sendedBtn;

    private int type;              // 보여줄 요청의 타입을 지정해 타입에 해당하는 뷰를 보여줍니다.
    private int TYPE_RECEIVED = 1; // 받은 요청
    private int TYPE_ACCEPTED = 2; // 상대방이 수락해 친구 관계가 성립된 경우
    private int TYPE_SENEDED = 3;  // 보냈는데 아직 상대방이 수락/거절 하지 않은 요청

    private boolean receivedListIsOpen = true, acceptedListIsOpen = false, sendedListIsOpen = false;

    public NotifyFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notify, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    public void init() {
        dbhelper = new Dbhelper(getContext());
        receivedAngle = (TextView) getView().findViewById(R.id.receivedAngle);
//        acceptedAngle = (TextView) getView().findViewById(R.id.acceptedAngle);
        sendedAngle = (TextView) getView().findViewById(R.id.sendedAngle);
        receivedBtn = (RelativeLayout) getView().findViewById(R.id.receivedBtn);
//        acceptedBtn = (RelativeLayout) getView().findViewById(R.id.acceptedBtn);
        sendedBtn = (RelativeLayout) getView().findViewById(R.id.sendedBtn);
        receivedList = (ListView) getView().findViewById(R.id.receivedList);
//        acceptedList = (ListView) getView().findViewById(R.id.acceptedList);
        sendedList = (ListView) getView().findViewById(R.id.sendedList);

        received = new ArrayList<Request>();
//        accepted = new ArrayList<Request>();
        sended = new ArrayList<Request>();

        if (receivedBtn != null) receivedBtn.setOnClickListener(this);
//        if (acceptedBtn != null) acceptedBtn.setOnClickListener(this);
        if (sendedBtn != null)   sendedBtn.setOnClickListener(this);

        // TODO 승인된 친구 요청과 받은 친구 요청 리스트를 채워줘야 합니다.
        // TODO 어댑터 마지막 인자에 해당 요청의 타입을 명시해줘야 합니다. 타입은 위에 멤버 변수에 있습니다.
        try {
            received = new notifyAsyncTask(getContext()).execute(1).get();
            sended = new notifyAsyncTask(getContext()).execute(2).get();
        }catch (Exception e){
            e.printStackTrace();
        }

        /* 받은 친구 요청 */
        if(received != null) {
            receivedListAdapter = new RequestListAdapter(getActivity(), 0, received, TYPE_RECEIVED);
            receivedList.setAdapter(receivedListAdapter);
            AnimHelpers.animateListHeight(getActivity(), receivedList, 70, 0, received.size());
        }

//        /* 수락된 친구 요청 */
//        acceptedListAdapter = new RequestListAdapter(getActivity(), 0, accepted, TYPE_ACCEPTED);
//        acceptedList.setAdapter(acceptedListAdapter);

        /* 보낸 친구 요청 */
        if(sended != null) {
            sendedListAdapter = new RequestListAdapter(getActivity(), 0, sended, TYPE_SENEDED);
            sendedList.setAdapter(sendedListAdapter);
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.receivedBtn: // 받은 친구 요청 버튼 클릭
                if (receivedListIsOpen) {
                    receivedAngle.animate().rotation(180).start();
                    if(received == null){
                        break;
                    }
                    AnimHelpers.animateListHeight(getActivity(), receivedList, 70, received.size(), 0);
                } else {
                    receivedAngle.animate().rotation(0).start();
                    if(received == null){
                        break;
                    }
                    AnimHelpers.animateListHeight(getActivity(), receivedList, 70, 0, received.size());
                }
                receivedListIsOpen = !receivedListIsOpen;
                break;
//
//            case R.id.acceptedBtn: // 수락된 친구 요청 버튼 클릭
//                if (acceptedListIsOpen) {
//                    acceptedAngle.animate().rotation(0).start();
//                    AnimHelpers.animateListHeight(getActivity(), acceptedList, 70, accepted.size(), 0);
//                } else {
//                    acceptedAngle.animate().rotation(-180).start();
//                    AnimHelpers.animateListHeight(getActivity(), acceptedList, 70, 0, accepted.size());
//                }
//                acceptedListIsOpen = !acceptedListIsOpen;
//                break;

            case R.id.sendedBtn: // 보낸 친구 요청 버튼 클릭
                if (sendedListIsOpen) {
                    sendedAngle.animate().rotation(0).start();
                    if(sended == null){
                        break;
                    }
                    AnimHelpers.animateListHeight(getActivity(), sendedList, 70, sended.size(), 0);
                } else {
                    sendedAngle.animate().rotation(-180).start();
                    if(sended == null){
                        break;
                    }
                    AnimHelpers.animateListHeight(getActivity(), sendedList, 70, 0, sended.size());
                }
            sendedListIsOpen = !sendedListIsOpen;
            break;
        }
    }
}

/*
 * 비동기 Http 연결 작업 클래스
 * */
class notifyAsyncTask extends AsyncTask<Integer, Integer, ArrayList<Request>> {

    private Context context;
    private Dbhelper dbhelper;

    public notifyAsyncTask(Context context){
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Request> doInBackground(Integer... ints) {
        dbhelper = new Dbhelper(context);
        HttpReqRes httpReqRes = new HttpReqRes();
        String res = null;

        ArrayList<Request> requests = new ArrayList<>();

        if(ints[0]==1) {    // req
            res = httpReqRes.requestHttpDoRequests("https://dna.soyoungpark.me:9013/api/friends/showReqList", dbhelper.getAccessToken());
        } else if(ints[0]==2) {     // send
            res = httpReqRes.requestHttpDoRequests("https://dna.soyoungpark.me:9013/api/friends/showSendList", dbhelper.getAccessToken());
        }

        int[] lists = FriendsJsonToObj(res, ints[0]);

        if(lists == null){
            return null;
        }

        for(int i=0; i<lists.length; i++){
            String res1 = requestHttpGETUserInfo(ServerURL.DNA_SERVER+ServerURL.PORT_USER_API+"/user/"+lists[i], dbhelper.getAccessToken());
            Log.v("after http", "after http" + res1);
            requests.add(SearchReqUserJsonToObj(res1));
            Log.v("notifyasync", "show user : " + res1);
        }

        dbhelper.close();
        return requests;
    }

//    @Override
//    protected void onPostExecute() {
//    }
}
