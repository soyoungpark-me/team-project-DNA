package com.konkuk.dna.chat;


import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListAdapter extends ArrayAdapter<ChatMessage> {
    Context context;
    ArrayList<ChatMessage> messages;
    String currentUserId;
    Boolean isMyMessage = false;

    Integer myIdx = 0;

    private static Typeface NSEB;
    private static Typeface NSB;
    private static Typeface NSR;
    private static Typeface fontAwesomeR;
    private static Typeface fontAwesomeS;

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";          // 일반 메시지 전송
    private final String TYPE_LOUDSPEAKER = "LoudSpeaker";  // 확성기 전송
    private final String TYPE_LOCATION = "Location";        // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";              // 이미지 전송
    private final String TYPE_SHARE = "Share";              // 포스팅 공유

    private final int VTYPE_MINE = 0;
    private final int VTYPE_OTHER_AVATAR = 1;
    private final int VTYPE_OTHER_NONE = 2;

    public ChatListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ChatMessage> objects) {
        super(context, resource, objects);

        this.context = context;
        this.messages = objects;

        init();
    }

    public void init() {
        if(NSEB == null) {
            NSEB = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareEB.ttf");
        }
        if(NSB == null) {
            NSB = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareB.ttf");
        }
        if(NSR == null) {
            NSR = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareR.ttf");
        }
        if(fontAwesomeR == null) {
            fontAwesomeR = Typeface.createFromAsset(context.getAssets(), "fonts/fa-regular-400.ttf");
        }
        if(fontAwesomeS == null) {
            fontAwesomeS = Typeface.createFromAsset(context.getAssets(), "fonts/fa-solid-900.ttf");
        }

        Dbhelper dbhelper = new Dbhelper(context);
        myIdx = dbhelper.getMyIdx();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
        ChatMessage message = messages.get(position);
        ChatMessage prev_message = null;
        if(position>0){
            prev_message = messages.get(position-1);
        }

        myIdx = 5;
        v = null;
        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // TODO 해당 메시지의 작성자가 현재 접속한 유저인지를 판별해 left, right를 정해줘야 합니다.
            // TODO 상대방이 작성했으면서 최초 메시지일 경우에는 프로필 이미지와 닉네임을 보여줘야 합니다.

            if(message.getViewType() == VTYPE_MINE){
                if (message.getType().equals(TYPE_LOUDSPEAKER)) { // 확성기 상태일 경우
                    v = layoutInflater.inflate(R.layout.chat_item_loudspeaker_right, null);
                } else {
                    v = layoutInflater.inflate(R.layout.chat_item_right, null);
                }
            }
            else if(message.getViewType() == VTYPE_OTHER_AVATAR){
                if (message.getType().equals(TYPE_LOUDSPEAKER)) { // 확성기 상태일 경우
                    v = layoutInflater.inflate(R.layout.chat_item_loudspeaker_with_profile, null);
                } else {
                    v = layoutInflater.inflate(R.layout.chat_item_with_profile, null);
                }

                TextView messageNickname = (TextView) v.findViewById(R.id.msgNickname);
                messageNickname.setText(message.getUserName());
                messageNickname.setTypeface(NSB);

                ImageView messageAvatar = (ImageView) v.findViewById(R.id.msgAvatar);

                if (message.getAvatar() != null) {
                    Picasso.get().load(message.getAvatar()).into(messageAvatar);
                }
            }
            else if(message.getViewType() == VTYPE_OTHER_NONE){
                if (message.getType().equals(TYPE_LOUDSPEAKER)) { // 확성기 상태일 경우
                    v = layoutInflater.inflate(R.layout.chat_item_loudspeaker_left, null);
                } else {
                    v = layoutInflater.inflate(R.layout.chat_item_left, null);
                }
            }
//
//            if(message.getIdx() == myIdx){
//                // 내 메세지, 프로필 없음
//                if (message.getType().equals(TYPE_LOUDSPEAKER)) { // 확성기 상태일 경우
//                    v = layoutInflater.inflate(R.layout.chat_item_loudspeaker_right, null);
//                } else {
//                    v = layoutInflater.inflate(R.layout.chat_item_right, null);
//                }
//            }else if(prev_message!=null && (prev_message.getIdx() == message.getIdx())){
//                // 남의 메세지, 프로필 없음
//                if (message.getType().equals(TYPE_LOUDSPEAKER)) { // 확성기 상태일 경우
//                    v = layoutInflater.inflate(R.layout.chat_item_loudspeaker_left, null);
//                } else {
//                    v = layoutInflater.inflate(R.layout.chat_item_left, null);
//                }
//            }else{
//                // 남의 메세지, 프로필 있음
//                if (message.getType().equals(TYPE_LOUDSPEAKER)) { // 확성기 상태일 경우
//                    v = layoutInflater.inflate(R.layout.chat_item_loudspeaker_with_profile, null);
//                } else {
//                    v = layoutInflater.inflate(R.layout.chat_item_with_profile, null);
//                }
//
//                TextView messageNickname = (TextView) v.findViewById(R.id.msgNickname);
//                messageNickname.setText(message.getUserName());
//                messageNickname.setTypeface(NSB);
//
//                ImageView messageAvatar = (ImageView) v.findViewById(R.id.msgAvatar);
//
//                if (message.getAvatar() != null) {
//                    Picasso.get().load(message.getAvatar()).into(messageAvatar);
//                }
//            }
        }

        LinearLayout messageLikeWrapper = (LinearLayout) v.findViewById(R.id.likeWrapper);
        RelativeLayout msgLocationWrapper = (RelativeLayout) v.findViewById(R.id.msgLocationWrapper);
        ImageView msgImage = (ImageView) v.findViewById(R.id.msgImage);
        TextView msgText = (TextView) v.findViewById(R.id.msgText);
        TextView msgShare = (TextView) v.findViewById(R.id.msgShare);
        TextView likeCount = (TextView) v.findViewById(R.id.likeCount);
        TextView dateText = (TextView) v.findViewById(R.id.dateText);
        TextView likeStar = (TextView) v.findViewById(R.id.likeStar);


        switch(message.getType()) {
            case TYPE_LOUDSPEAKER:
            case TYPE_MESSAGE:
                if (msgText != null) {
                    msgText.setVisibility(View.VISIBLE);
                    msgText.setText(message.getContents());
                }
                break;
            case TYPE_IMAGE:
                if (msgImage != null) {
                    msgImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(message.getContents()).into(msgImage);
                }
                break;
            case TYPE_LOCATION:
//                if (msgLocationWrapper != null) {
//                    msgLocationWrapper.setVisibility(View.VISIBLE);
//                    msgLocationWrapper.setId(message.getIdx());
//                    FragmentManager manager = ((Activity) context).getFragmentManager();
//                    FragmentTransaction fragTransaction = manager.beginTransaction();
//                    ChatListMapFragment newFragment =
//                            ChatListMapFragment.newInstance(message.getLng(), message.getLat());
//                    Log.d("test", newFragment.toString());
//                    fragTransaction.add(msgLocationWrapper.getId(), (Fragment) newFragment, "mapFragment" + message.getIdx());
//                    fragTransaction.commit();
//                }

                break;
            case TYPE_SHARE:
                if (msgShare != null) {
                    msgShare.setVisibility(View.VISIBLE);
                    msgShare.setText("[공유] " + message.getContents());
                }
                break;
        }

        likeCount.setText(message.getLike());
        dateText.setText(message.getDate());

        dateText.setTypeface(NSB);
        likeStar.setTypeface(fontAwesomeS);

        isMyMessage = false;
        for(int i=0; i<message.getWhoLikes().size(); i++){
            if(message.getWhoLikes().get(i) == myIdx){
                isMyMessage = true;
            }else{
                isMyMessage = false;
            }
        }


//        messageLikeWrapper.setClickable(true);
//
//        messageLikeWrapper.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //소켓에 눌렀다고 보내기
//                isMyMessage = true;
//            }
//        });

        // TODO 내가 좋아요를 클릭했을 경우와 클릭하지 않았을 경우 다른 뷰를 보여줘야 합니다.
        if (isMyMessage) { // 클릭했을 경우
            likeCount.setTextColor(context.getResources().getColor(R.color.yellow));
            likeStar.setTextColor(context.getResources().getColor(R.color.yellow));
            messageLikeWrapper.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_like_clicked));

        } else {
            likeCount.setTextColor(context.getResources().getColor(R.color.grayDark));
            likeStar.setTextColor(context.getResources().getColor(R.color.grayLighter));
            messageLikeWrapper.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_like_default));
        }

        return v;
    }
}
