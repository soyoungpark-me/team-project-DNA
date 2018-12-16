package com.konkuk.dna.chat;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konkuk.dna.R;
import com.konkuk.dna.utils.SocketConnection;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.NameHelpers;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.konkuk.dna.utils.JsonToObj.getAddressContents;
import static com.konkuk.dna.utils.JsonToObj.getLocationContents;
import static java.lang.Integer.parseInt;

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

    private final int VTYPE_SYSTEM = -1;
    private final int VTYPE_MINE = 0;
    private final int VTYPE_OTHER_AVATAR = 1;
    private final int VTYPE_OTHER_NONE = 2;

    private static final String CLIENT_ID = "rp1dh7ukaj"; // 애플리케이션 클라이언트 아이디 값
    private static final String CLIENT_SECRET = "EO4SWXVVVYQhoJ33jpsTO4Mteil5GEamBnDn1qfM"; // 애플리케이션 클라이언트 시크릿 값

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
        dbhelper.close();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
        final ChatMessage message = messages.get(position);

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

                String nickname = "";
                if (message.getAnonymity() == 1) {
                    nickname = NameHelpers.makeName(message.getIdx());
                } else {
                    nickname = message.getUserName();
                }
                messageNickname.setText(nickname);
                messageNickname.setTypeface(NSB);

                ImageView messageAvatar = (ImageView) v.findViewById(R.id.msgAvatar);

                if (message.getAvatar() != null && message.getAnonymity() != 1) {
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

           final LinearLayout messageLikeWrapper = (LinearLayout) v.findViewById(R.id.likeWrapper);
            RelativeLayout msgLocationWrapper = (RelativeLayout) v.findViewById(R.id.msgLocationWrapper);
            ImageView msgImage = (ImageView) v.findViewById(R.id.msgImage);
            TextView msgText = (TextView) v.findViewById(R.id.msgText);
            TextView msgShare = (TextView) v.findViewById(R.id.msgShare);
            final TextView likeCount = (TextView) v.findViewById(R.id.likeCount);
            TextView dateText = (TextView) v.findViewById(R.id.dateText);
            final TextView likeStar = (TextView) v.findViewById(R.id.likeStar);


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
                    if (msgShare != null) {
                        msgShare.setVisibility(View.VISIBLE);

                        //TODO : 지도 위치 가져오기!
                        ArrayList<Double> loc = getLocationContents(message.getContents());
                        if(loc!=null){
                            Double lat = loc.get(0);
                            Double lng = loc.get(1);

                            getLocationAsync glc = new getLocationAsync(CLIENT_ID, CLIENT_SECRET);
                            try {
                                String address = glc.execute(lat, lng).get();
                                msgShare.setText("[위치] " + address);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            msgShare.setText(message.getContents());
                        }

                    }

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

            //좋아요 누름 처리
            messageLikeWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //좋아요 처리
                    Dbhelper dbhelper = new Dbhelper(context);
                    SocketConnection.emit("like", dbhelper.getAccessToken(), message.getMsg_idx());
                    dbhelper.close();

                    int likenow = Integer.parseInt(message.getLike());
                    if (message.isAmILike()) { // 내가 눌렀던 것일 경우
                        likeCount.setTextColor(context.getResources().getColor(R.color.grayDark));
                        likeStar.setTextColor(context.getResources().getColor(R.color.grayLighter));
                        messageLikeWrapper.setBackgroundResource(R.drawable.button_like_default);
                        message.setAmILike(false);
                        likenow--;
                    }else{
                        likeCount.setTextColor(context.getResources().getColor(R.color.yellow));
                        likeStar.setTextColor(context.getResources().getColor(R.color.yellow));
                        messageLikeWrapper.setBackgroundResource(R.drawable.button_like_clicked);
                        message.setAmILike(true);
                        likenow++;
                    }

                    message.setLike(String.valueOf(likenow));
                    likeCount.setText(String.valueOf(likenow));
                }
            });

            // TODO 내가 좋아요를 클릭했을 경우와 클릭하지 않았을 경우 다른 뷰를 보여줘야 합니다.
            if (messages.get(position).isAmILike()) { // 클릭했을 경우
                likeCount.setTextColor(context.getResources().getColor(R.color.yellow));
                likeStar.setTextColor(context.getResources().getColor(R.color.yellow));
                //messageLikeWrapper.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_like_clicked));
                messageLikeWrapper.setBackgroundResource(R.drawable.button_like_clicked);

            } else {
                likeCount.setTextColor(context.getResources().getColor(R.color.grayDark));
                likeStar.setTextColor(context.getResources().getColor(R.color.grayLighter));
                //messageLikeWrapper.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_like_default));
                messageLikeWrapper.setBackgroundResource(R.drawable.button_like_default);
            }


        }

        return v;
    }
}


class getLocationAsync extends AsyncTask<Double, Void, String> {

    private String clientId;
    private String clientSecret;

    public getLocationAsync(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    protected String doInBackground(Double... doubles) {
        Double lat = doubles[0];
        Double lng = doubles[1];
        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&output=json&coords="+String.valueOf(lng)+","+String.valueOf(lat);

        Log.e("URL", apiURL);
        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            Log.e("Where", response.toString());
            String result = getAddressContents(response.toString());

            return result;

        }catch (Exception e){

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}