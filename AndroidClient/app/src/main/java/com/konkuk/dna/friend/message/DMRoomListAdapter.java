package com.konkuk.dna.friend.message;


import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DMRoomListAdapter extends ArrayAdapter<DMRoom> {
    Context context;
    ArrayList<DMRoom> rooms;
    String currentUserId;
    Boolean isMyMessage = false;

    private static Typeface NSEB;
    private static Typeface NSB;
    private static Typeface NSR;
    private static Typeface fontAwesomeR;
    private static Typeface fontAwesomeS;

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";     // 일반 메시지 전송
    private final String TYPE_LOCATION = "Location";    // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";       // 이미지 전송

    public DMRoomListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DMRoom> objects) {
        super(context, resource, objects);

        this.context = context;
        this.rooms = objects;

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
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
        DMRoom room = rooms.get(position);

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = layoutInflater.inflate(R.layout.chat_item_room, null);

            ImageView roomAvatar = (ImageView) v.findViewById(R.id.roomAvatar);
            if (room.getAvatar() != null && room.getAvatar() != "") {
                Picasso.get().load(room.getAvatar()).into(roomAvatar);
            }


            TextView roomNickname = (TextView) v.findViewById(R.id.roomNickname);
            roomNickname.setTypeface(NSEB);
            roomNickname.setText(room.getNickname());
            TextView roomDate = (TextView) v.findViewById(R.id.roomDate);
            roomDate.setText(room.getUpdateDate());
            TextView lastMessage = v.findViewById(R.id.lastMessage);

            switch (room.getLastType()) {
                case TYPE_MESSAGE:
                    lastMessage.setText(room.getLastMessage());
                    break;
                case TYPE_IMAGE:
                    lastMessage.setText("[사진]");
                    break;
                case TYPE_LOCATION:
                    lastMessage.setText("[위치]");
                    break;
            }
        }
        return v;
    }
}
