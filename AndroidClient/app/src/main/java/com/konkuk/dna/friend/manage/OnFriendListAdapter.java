package com.konkuk.dna.friend.manage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.konkuk.dna.utils.helpers.AnimHelpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OnFriendListAdapter extends RecyclerView.Adapter<OnFriendListAdapter.MyViewHolder> {
    Context context;
    ArrayList<Friend> friends;

    public OnFriendListAdapter(Context context, ArrayList<Friend> friends) {
        Log.d("_test", friends.toString());
        this.context = context;
        this.friends = friends;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        protected LinearLayout ofWrapper;
        protected ImageView ofAvatar;
        protected TextView ofNickname;

        private MyViewHolder(View v) {
            super(v);
            this.ofWrapper = (LinearLayout) v.findViewById(R.id.ofWrapper);
            this.ofAvatar = (ImageView) v.findViewById(R.id.ofAvatar);
            this.ofNickname = (TextView) v.findViewById(R.id.ofNickname);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.friend_item_on_user, null);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend friend = friends.get(position);

        holder.ofWrapper.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ofWrapper.getLayoutParams();
        if (params != null) {
            params.width = AnimHelpers.dpToPx(this.context, 80);
            params.height = AnimHelpers.dpToPx(this.context, 100);
        }
        FrameLayout.LayoutParams avatarParams = (FrameLayout.LayoutParams) holder.ofAvatar.getLayoutParams();
        avatarParams.gravity = Gravity.CENTER;
        holder.ofWrapper.requestLayout();
        if (friend.getAvatar() != null && friend.getAvatar() != "") {
            Picasso.get().load(friend.getAvatar()).into(holder.ofAvatar);
        }
        holder.ofNickname.setText(friend.getNickname());
    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }
}

