package com.konkuk.dna.chat;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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

public class ChatUserDetailFragment extends DialogFragment implements View.OnClickListener{
    private static Typeface NSEB;
    private static Typeface NSB;
    private static Typeface NSR;
    private static Typeface fontAwesomeS;

    private ImageView detailAvatar;
    private TextView detailNickname, detailID, detailInfo, detailDMBtnText, detailDeleteBtnText,
            detailDMBtnIcon, detailDeleteBtnIcon;
    private LinearLayout detailAddBtn, detailBanBtn;
    private ChatUser user;


    public ChatUserDetailFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_user_detail, container, false);
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
        detailDMBtnIcon = (TextView) getView().findViewById(R.id.detailDMBtnIcon);
        detailDeleteBtnText = (TextView) getView().findViewById(R.id.detailDeleteBtnText);
        detailDeleteBtnIcon = (TextView) getView().findViewById(R.id.detailDeleteBtnIcon);
        detailAddBtn = (LinearLayout) getView().findViewById(R.id.detailAddBtn);
        detailBanBtn = (LinearLayout) getView().findViewById(R.id.detailBanBtn);

        detailAddBtn.setOnClickListener(this);
        detailBanBtn.setOnClickListener(this);

        // TODO 해당 유저의 idx 값으로 User서버에 요청해 전체 프로필 정보를 가져와야 합니다!

        if (user != null) {
            if (user.getAvatar() != null && user.getAvatar() != "") {
                Picasso.get().load(user.getAvatar()).into(detailAvatar);
            }
            detailNickname.setText(user.getNickname());
            detailNickname.setTypeface(NSEB);
            detailID.setText(user.getID());
            detailID.setTypeface(NSB);
            if (user.getInfo() != "" && user.getInfo() != null) {
                detailInfo.setText(user.getInfo());
            } else {
                detailInfo.setText("해당 유저는 작성한 상태 메시지가 없습니다.");
            }
        }
        detailDMBtnText.setTypeface(NSB);
        detailDMBtnIcon.setTypeface(fontAwesomeS);
        detailDeleteBtnText.setTypeface(NSB);
        detailDeleteBtnIcon.setTypeface(fontAwesomeS);

        // TODO 해당 유저가 나와 친구 관계인지 아닌지 확인하고, 친구 추가 버튼을 활성화/비활성화 합니다.
    }

    public void setData(ChatUser user) {
        this.user = user;
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
            case R.id.detailAddBtn:  // 친구 요청하기 버튼 클릭
                Log.d("ChatUserDetail", "친구 요청 버튼 클릭");
                break;

            case R.id.detailBanBtn:  // 차단 버튼 클릭
                DialogSimple();
                break;
        }
    }

    private void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());
        alt_bld.setMessage("해당 사용자를 차단하시겠습니까? 해당 사용자의 메시지가 더 이상 보이지 않습니다.").setCancelable(
                false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO 삭제 버튼 클릭 이벤트 처리
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
