package com.konkuk.dna.friend.manage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendListAdapter extends ArrayAdapter<Friend> {
    Context context;
    ArrayList<Friend> users;

    private static Typeface fontAwesomeR;
    private static Typeface fontAwesomeS;

    public FriendListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Friend> objects) {
        super(context, resource, objects);

        this.context = context;
        this.users = objects;

        init();
    }

    public void init() {
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
        Friend user = users.get(position);

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.friend_item_user, null);
        }

        ImageView avatar = v.findViewById(R.id.ccuAvatar);
        TextView nickname = v.findViewById(R.id.ccuNickname);
        TextView status = v.findViewById(R.id.ccuStatus);
        TextView caret = v.findViewById(R.id.ccuCaret);
        TextView info = v.findViewById(R.id.ccuInfo);
        LinearLayout infoWraper = v.findViewById(R.id.infoWraper);

        if (user.getAvatar() != null) {
            Picasso.get().load(user.getAvatar()).into(avatar);
        }

        if (user.getInfo() == "" || user.getInfo() == null) {
            infoWraper.setVisibility(View.INVISIBLE);
        } else {
            info.setText(user.getInfo());
        }

        nickname.setText(user.getNickname());
        status.setTypeface(fontAwesomeS);
        caret.setTypeface(fontAwesomeS);
        if (user.getStatus()) { // 초록불 켜기!
            status.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            status.setTextColor(context.getResources().getColor(R.color.red));
        }


        return v;
    }

    private void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getContext());
        alt_bld.setMessage("해당 친구를 삭제하시겠습니까?").setCancelable(
                false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO 친구 삭제 버튼 클릭 이벤트 처리해야 합니다.

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

