package com.konkuk.dna.post;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.konkuk.dna.post.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {
    Context context;
    ArrayList<Comment> comments;

    private static Typeface NSEB;
    private static Typeface NSB;
    private static Typeface NSR;

    private static Typeface NSREB;
    private static Typeface NSRB;
    private static Typeface NSRR;

    private static Typeface fontAwesomeR;
    private static Typeface fontAwesomeS;

    public CommentAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Comment> objects) {
        super(context, resource, objects);

        this.context = context;
        this.comments = objects;

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
        if(NSREB == null) {
            NSREB = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareRoundEB.ttf");
        }
        if(NSRB == null) {
            NSRB = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareRoundB.ttf");
        }
        if(NSRR == null) {
            NSRR = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareRoundR.ttf");
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
        Comment comment = comments.get(position);
//        if(comments.get(position)==comments.get())
        Log.v("commentadapter", "comment idx?pos?:" + comment.getIdx());

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.post_comment_item, null);
        }

        ImageView avatar = v.findViewById(R.id.commentAvatar);
        TextView nickname = v.findViewById(R.id.commentNickname);
        TextView content = v.findViewById(R.id.commentContent);
        TextView date = v.findViewById(R.id.commentDate);

        if (comment.getAvatar() != null) {
            Picasso.get().load(comment.getAvatar()).into(avatar);
        }

        nickname.setTypeface(NSB);
        nickname.setText(comment.getNickname());
        Log.v("commentadapter", "comment nick : " + comment.getNickname());
        content.setText(comment.getContent());
        date.setText(comment.getDate());

        return v;
    }
}

