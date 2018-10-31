package com.konkuk.dna.friend.manage;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.konkuk.dna.friend.message.DMActivity;
import com.konkuk.dna.friend.message.DMRoom;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class FriendDetailFragment extends DialogFragment implements View.OnClickListener{
    private static Typeface NSEB;
    private static Typeface NSB;
    private static Typeface NSR;
    private static Typeface fontAwesomeS;

    private ImageView detailAvatar;
    private TextView detailNickname, detailID, detailInfo, detailDMBtnText, detailDeleteBtnText;
    private LinearLayout detailDMBtn, detailDeleteBtn;
    private Friend friend;


    public FriendDetailFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend_detail, container, false);
        getDialog().setCanceledOnTouchOutside(true);

        return v;
    }

    private void init() {
        if(NSEB == null) {
            NSEB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumSquareEB.ttf");
        }
        if(NSB == null) {
            NSB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumSquareB.ttf");
        }
        if(NSR == null) {
            NSR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumSquareR.ttf");
        }
        if(fontAwesomeS == null) {
            fontAwesomeS = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fa-solid-900.ttf");
        }

        detailAvatar = (ImageView) getView().findViewById(R.id.detailAvatar);
        detailNickname = (TextView) getView().findViewById(R.id.detailNickname);
        detailID = (TextView) getView().findViewById(R.id.detailID);
        detailInfo = (TextView) getView().findViewById(R.id.detailInfo);
        detailDMBtnText = (TextView) getView().findViewById(R.id.detailDMBtnText);
        detailDeleteBtnText = (TextView) getView().findViewById(R.id.detailDeleteBtnText);
        detailDMBtn = (LinearLayout) getView().findViewById(R.id.detailDMBtn);
        detailDeleteBtn = (LinearLayout) getView().findViewById(R.id.detailDeleteBtn);

        detailDMBtn.setOnClickListener(this);
        detailDeleteBtn.setOnClickListener(this);

        if (friend != null) {
            if (friend.getAvatar() != null && friend.getAvatar() != "") {
                Picasso.get().load(friend.getAvatar()).into(detailAvatar);
            }
            detailNickname.setText(friend.getNickname());
            detailNickname.setTypeface(NSEB);
            detailID.setText(friend.getID());
            detailID.setTypeface(NSB);
            if (friend.getInfo() != "" && friend.getInfo() != null) {
                detailInfo.setText(friend.getInfo());
            } else {
                detailInfo.setText("해당 친구는 작성한 상태 메시지가 없습니다.");
            }
        }
        detailDMBtnText.setTypeface(NSEB);
        detailDeleteBtnText.setTypeface(NSEB);
    }

    public void setData(Friend friend) {
        this.friend = friend;
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setBackgroundDrawableResource(R.drawable.layout_friend_popup);
        window.setLayout((int) (width * 0.75), (int) (height * 0.6));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detailDMBtn:  // DM 보내기 버튼 클릭
                Intent intent = new Intent (getActivity(), DMActivity.class);
                // TODO 해당 친구와의 채팅방 정보를 서버에서 가져와서 세팅해줘야 합니다.
                DMRoom room = new DMRoom(0, 1, "3457soso", "https://pbs.twimg.com/media/DbYfg2IWkAENdiS.jpg", "내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용", "Message", "2018-01-24");
                intent.putExtra("roomIdx", room.getIdx());
                intent.putExtra("roomUpdated", room.getUpdateDate());
                startActivity(intent);
                break;

            case R.id.detailDeleteBtn:  // 친구 삭제 버튼 클릭
                DialogSimple();
                break;
        }
    }

    private void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());
        alt_bld.setMessage("해당 친구를 삭제하시겠습니까? 친구와 더 이상 DM을 주고 받을 수 없습니다.").setCancelable(
                false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       // TODO 삭제 버튼 클릭 이벤트 처
                        dialog.cancel();
                    }
                }).setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
}
