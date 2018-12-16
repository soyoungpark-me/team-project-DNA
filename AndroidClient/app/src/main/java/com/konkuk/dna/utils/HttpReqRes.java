package com.konkuk.dna.utils;

import android.icu.util.Output;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konkuk.dna.post.Comment;
import com.konkuk.dna.post.Post;
import com.konkuk.dna.utils.dbmanage.Dbhelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;
import static com.konkuk.dna.utils.JsonToObj.PostingJsonToObj;
import static junit.framework.Assert.assertEquals;


public class HttpReqRes {

    /*
     * getAuthToken - GET
     * */
    public String requestHttpGETAuth(String url, String refreshToken) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpGet get = new HttpGet(postURL);
            get.setHeader("token", refreshToken);
            //HttpPost post = new HttpPost(postURL);
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("id", uid));
//            params.add(new BasicNameValuePair("password", upassword));
//            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//            post.setEntity(ent);

            HttpResponse responseGET = client.execute(get);
            HttpEntity resEntity = responseGET.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * Signup, Check ID - POST
     * */
    public String requestHttpPostSignup(String url, String id, String password, String confirm_password, String email, String nickname, String description, String avatar){


        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("confirm_password", confirm_password));
            params.add(new BasicNameValuePair("email", email));
//            if(nickname==null || nickname==""){
//                nickname = id;
//            }
            params.add(new BasicNameValuePair("nickname", nickname));
            params.add(new BasicNameValuePair("description", description));
            //params.add(new BasicNameValuePair("avatar", password));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);

            Log.e("status", responsePOST.getStatusLine().toString());
            HttpEntity resEntity = responsePOST.getEntity();

            result = EntityUtils.toString(resEntity);
            Log.e("RESPONSE", result);

//            if(responsePOST.getStatusLine().getStatusCode()==200){
//                HttpEntity resEntity = responsePOST.getEntity();
//                if (resEntity != null) {
//                    result = EntityUtils.toString(resEntity);
//                    Log.i("RESPONSE", result);
//                }
//            }else{
//                result = null;
//            }



//            HttpClient client = new DefaultHttpClient();
//            String postURL = url;
//            HttpPost httpPost = new HttpPost(postURL);
//            Charset chars = Charset.forName("UTF-8");
//
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//            builder.addTextBody("id", id, ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"));
//            builder.addTextBody("password", password, ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"));
//            builder.addTextBody("confirm_password", confirm_password, ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"));
//            builder.addTextBody("email", email, ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"));
//            builder.addTextBody("description", description, ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"));
//            builder.addTextBody("nickname", nickname, ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"));
//
//            if(avatar!=null) {
//                builder.addBinaryBody("avatar", new File("test.txt"),
//                        ContentType.APPLICATION_OCTET_STREAM, "file.ext");
//            }
//            HttpEntity multipart = builder.build();
//            httpPost.setEntity(multipart);
//
//            HttpResponse response = client.execute(httpPost);
//            //assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
//            HttpEntity resEntity = response.getEntity();
//            result = EntityUtils.toString(resEntity);
//
//            Log.e("RESPONSE", result);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    /*
     * Login - POST
     * */
    public String requestHttpPostLogin(String url, String uid, String upassword) {

        HttpsURLConnection urlConn = null;
        BufferedReader reader = null;

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", uid));
            params.add(new BasicNameValuePair("password", upassword));
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            if(responsePOST.getStatusLine().getStatusCode()==200){
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                    Log.i("RESPONSE", result);
                }
            }else{
                result = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * 전체 메세지 받아오기 - post
     * */
    public String requestHttpPostMsgAll(String url, String token, Double lng, Double lat, Integer radius) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.protocol.expect-continue", false);
            client.getParams().setParameter("http.connection.timeout", 2000);
            client.getParams().setParameter("http.socket.timeout", 2000);

            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            post.setHeader("token", token);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lng", lng.toString()));
            params.add(new BasicNameValuePair("lat", lat.toString()));
            params.add(new BasicNameValuePair("radius", radius.toString()));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("requestHttpPostMsgAll", "res: " + result);
        return result;
    }


    /*
     * 베스트챗 받아오기 - post
     * */
    public String requestHttpPostBestChat(String url, String token, Double lng, Double lat, Integer radius) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.protocol.expect-continue", false);
            client.getParams().setParameter("http.connection.timeout", 2000);
            client.getParams().setParameter("http.socket.timeout", 2000);

            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            post.setHeader("token", token);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lng", lng.toString()));
            params.add(new BasicNameValuePair("lat", lat.toString()));
            params.add(new BasicNameValuePair("radius", radius.toString()));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.e("requestHttpPostBestChat", "res: "+result);
        return result;
    }

    /*
     * DM방 개설하기 - post
     * */
    public String requestHttpPostCreateDM(String url, String token, String nickname, int idx, String avatar) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.protocol.expect-continue", false);
            client.getParams().setParameter("http.connection.timeout", 2000);
            client.getParams().setParameter("http.socket.timeout", 2000);

            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            post.setHeader("token", token);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nickname", nickname));
            params.add(new BasicNameValuePair("idx", String.valueOf(idx)));
            params.add(new BasicNameValuePair("avatar", avatar));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.e("requestHttpPostBestChat", "res: "+result);
        return result;
    }


    /*
     * get DM Rooms- GET
     * */
    public String requestHttpGETDMRooms(String url, String accessToken) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpGet get = new HttpGet(postURL);
            get.setHeader("token", accessToken);

            HttpResponse responseGET = client.execute(get);
            HttpEntity resEntity = responseGET.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * get DM Messages- GET
     * */
    public String requestHttpGETDmMsgs(String url, String accessToken) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpGet get = new HttpGet(postURL);
            get.setHeader("token", accessToken);

            HttpResponse responseGET = client.execute(get);
            HttpEntity resEntity = responseGET.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * get DM Messages- GET
     * */
    public static String requestHttpGETUserInfo(String url, String accessToken) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpGet get = new HttpGet(postURL);
            get.setHeader("token", accessToken);

            HttpResponse responseGET = client.execute(get);
            HttpEntity resEntity = responseGET.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
                Log.v("httpreqres", "getuserinfo res : " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /*
     * 채팅 환경설정 하기 - put
     * */
    public static String requestHttpPutSetting(String url, String token, int radius, int anonymity, int searchable) {

        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            String putURL = url;
            HttpPut put = new HttpPut(putURL);
            put.setHeader("token", token);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("radius", String.valueOf(radius)));
            params.add(new BasicNameValuePair("anonymity", String.valueOf(anonymity)));
            params.add(new BasicNameValuePair("searchable", String.valueOf(searchable)));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            put.setEntity(ent);

            HttpResponse responsePUT = client.execute(put);
            HttpEntity resEntity = responsePUT.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * 사진 람다업로드 후 주소받아오기 - post
     * */
    public static String requestHttpPostLambda(String requrl, String imgURL) {
        /*
         await axios.post(`${AWS_LAMBDA_API_URL}?type=${type}`, formData,
    { headers: { 'Content-Type': 'multipart/form-data' }})
    .then((response) => {result = response});
         */

        //URLConnection.guessContentTypeFromName(fileName) : image/png
        //String fileName = new File(imgURL).getName();
        //TODO: js에서 file으로 넘기는게 무슨 객체인지, 어떤 형식인지 알아야 풀 수 있을 것 같다.

        try{
            File sourceFile = new File(imgURL);
            String filename = sourceFile.getName();
            //Log.d(TAG, "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE = imgURL.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"image\""),
                            RequestBody.create(MEDIA_TYPE, new File(imgURL)))
                    .build();

            Request request = new Request.Builder()
                    .header("Content-Type", "multipart/form-data")
                    .url(requrl+"?type=image")
                    .post(requestBody)
                    .build();

            Log.e("request", request.body().toString());

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            return response.body().string();

        }  catch(Exception e) {
            Log.e("MultipartRequest","Multipart Form Upload Error");
            e.printStackTrace();
            return "error";
        }

    }

    /*
     * get WAS APIs = GET
     */
    public String requestHttpGetWASPIwToken(String url, String token){

        String result = null;

        try{
            HttpClient client = new DefaultHttpClient();

            String getURL = url;
            HttpGet get = new HttpGet(getURL);
            get.setHeader("token", token);

            HttpResponse responseGET = client.execute(get);
            HttpEntity resEntity = responseGET.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /*
     * get Posts = GET
     */
    public String requestHttpGetPosting(String url){

        String result = null;

        try{
            HttpClient client = new DefaultHttpClient();
//            HttpParams params = client.getParams();
//            HttpConnectionParams.setConnectionTimeout(params, 5000);
//            HttpConnectionParams.setSoTimeout(params, 5000);
            Log.v("httpreqres", "url when get posting : " + url);
            String getURL = url;
            HttpGet get = new HttpGet(getURL);

            HttpResponse responseGET = client.execute(get);
            HttpEntity resEntity = responseGET.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /*
     * write Posts = Post
     */
    public String requestHttpPostWritePosting(String url, Dbhelper dbhelper, Post posting) {
        String result = null;
        JSONObject json = null;

        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            post.setHeader("token", dbhelper.getAccessToken());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            String lng = String.valueOf(posting.getLongitude());
            String lat = String.valueOf(posting.getLatitude());
            String om = String.valueOf(posting.getOnlyme());

            nameValuePairs.add(new BasicNameValuePair("date", posting.getDate()));
            nameValuePairs.add(new BasicNameValuePair("title", posting.getTitle()));
            nameValuePairs.add(new BasicNameValuePair("contents", posting.getContent()));
            nameValuePairs.add(new BasicNameValuePair("latitude", lat));
            nameValuePairs.add(new BasicNameValuePair("longitude", lng));
            nameValuePairs.add(new BasicNameValuePair("onlyme", om));
            nameValuePairs.add(new BasicNameValuePair("nickname", dbhelper.getMyNickname()));
            nameValuePairs.add(new BasicNameValuePair("avatar", dbhelper.getMyAvatar()));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            post.setEntity(ent);
            //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    /*
     * Post DETAILS = Post
     */
    public String requestHttpPosting(String url, String token, int postCase) {
        String result = null;
//        JSONObject json = null;
//        Post posting;

        switch(postCase) {
            case 1:        // post
                try {
                    HttpClient client = new DefaultHttpClient();
                    String postURL = url;
                    HttpPost post = new HttpPost(postURL);

                    post.setHeader("token", token);

                    HttpResponse response = client.execute(post);
                    HttpEntity resEntity = response.getEntity();
                    result = EntityUtils.toString(resEntity);

                } catch (Exception e) {
                    e.printStackTrace();
                    return result;
                }

                break;

            case 2:        // del
                try {
                    Log.v("httpreqres", "url : " + url);
                    HttpClient client = new DefaultHttpClient();
                    String deleteURL = url;
                    HttpDelete del = new HttpDelete(deleteURL);

                    del.setHeader("token", token);

                    HttpResponse response = client.execute(del);
                    HttpEntity resEntity = response.getEntity();
                    result = EntityUtils.toString(resEntity);

                } catch (Exception e) {
                    e.printStackTrace();
                    return result;
                }

                break;
            }
        Log.v("posting httpreqres", "get server result : " + result);

        return result;
    }

    /*
     * write comments = Post
     */
    public String requestHttpPostWriteComment(String url, Dbhelper dbhelper, String content) {
        Log.v("httpreqrs", "url : "  + url);
        String result = null;
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        JSONObject json = null;

        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            post.setHeader("token", dbhelper.getAccessToken());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("rcontents", content));
            nameValuePairs.add(new BasicNameValuePair("userNick", dbhelper.getMyNickname()));
            nameValuePairs.add(new BasicNameValuePair("userAvatar", dbhelper.getMyAvatar()));
            nameValuePairs.add(new BasicNameValuePair("rdate", sdf.format(dt).toString()));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse response = client.execute(post);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        Log.v("httpreqres", "result of reply : " + result);
        return result;
    }

    /*
     * Friend Accepts = post
     */
    public String requestHttpPostAddFriend(String url, Dbhelper dbhelper, int ridx){
        String result = null;
        JSONObject json = null;

        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = url;
            HttpPost post = new HttpPost(postURL);
            post.setHeader("token", dbhelper.getAccessToken());
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            String recidx = String.valueOf(ridx);

            nameValuePairs.add(new BasicNameValuePair("receiverIdx", recidx));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            post.setEntity(ent);

            HttpResponse response = client.execute(post);
            HttpEntity resEntity = response.getEntity();
            result = EntityUtils.toString(resEntity);
            Log.v("httpreq", "status : " + response.getStatusLine());
            Log.v("httpreqres", "result after parsing : " + result);

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        Log.v("httpreqres", "result of reply : " + result);
        return result;

    }

    /*
     * delete friends = DELETE
     */
    public String requestHttpFriendDelete(String url, String token){

        String result = null;

        try{
            HttpClient client = new DefaultHttpClient();
            Log.v("httpreqres", "url when delete friends : " + url);
            String delURL = url;
            HttpDelete del = new HttpDelete(delURL);

            del.setHeader("token", token);

            HttpResponse responseDelete = client.execute(del);
            HttpEntity resEntity = responseDelete.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /*
     * Friend Requests = put/delete
     */
    public String requestHttpDoRequests(String url, String token) {
        String result = null;
//        JSONObject json = null;
//        Post posting;

        try {
            HttpClient client = new DefaultHttpClient();
            Log.v("httpreqres", "url when get req lists : " + url);
            String getURL = url;
            HttpGet get = new HttpGet(getURL);

            get.setHeader("token", token);

            HttpResponse responseGET = client.execute(get);
            HttpEntity resEntity = responseGET.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * Post DETAILS = Post
     */
    public String requestHttpNotifyFriend(String url, String token, int fCase) {
        String result = null;
//        JSONObject json = null;
//        Post posting;

        switch(fCase) {
            case 1:        // accept
                try {
                    HttpClient client = new DefaultHttpClient();
                    String putURL = url;
                    HttpPut put = new HttpPut(putURL);

                    put.setHeader("token", token);

                    HttpResponse response = client.execute(put);
                    HttpEntity resEntity = response.getEntity();
                    result = EntityUtils.toString(resEntity);

                } catch (Exception e) {
                    e.printStackTrace();
                    return result;
                }

                break;

            case 2:        // deny, delete
                try {
                    Log.v("httpreqres", "url : " + url);
                    HttpClient client = new DefaultHttpClient();
                    String deleteURL = url;
                    HttpDelete del = new HttpDelete(deleteURL);

                    del.setHeader("token", token);

                    HttpResponse response = client.execute(del);
                    HttpEntity resEntity = response.getEntity();
                    result = EntityUtils.toString(resEntity);

                } catch (Exception e) {
                    e.printStackTrace();
                    return result;
                }

                break;
        }
        Log.v("posting httpreqres", "get server result : " + result);

        return result;
    }
}
