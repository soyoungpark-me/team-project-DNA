package com.konkuk.dna.utils;
import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

import static com.konkuk.dna.utils.ObjToJson.StoreObjToJson;

public class SocketConnection {

    private static Socket socket;
    private static SocketConnection instance;
    private static Activity act;

    private SocketConnection(){
    }

    /*
    * 인스턴스 시작하기
    * */
    public static void initInstance(){
        Log.e("SocketConnection", "initInstance");
        if(instance == null){
            instance = new SocketConnection();
        }
        connectSocket();
        //return instance;
    }

    public static void connectSocket(){
        if(SocketConnection.getSocket()==null){
            try{
//                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                };
//                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//
//                    }
//
//                    @Override
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//
//                    }
//
//                    @Override
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return new java.security.cert.X509Certificate[0];
//                    }
//                }};
//                X509TrustManager trustManager = (X509TrustManager) trustAllCerts[0];
//
//                SSLContext sslContext = SSLContext.getInstance("SSL");
//                sslContext.init(null, trustAllCerts, null);
//                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//                OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                        .hostnameVerifier(hostnameVerifier)
//                        .sslSocketFactory(sslSocketFactory, trustManager)
//                        .build();
//
                IO.Options opts = new IO.Options();
//                opts.callFactory = okHttpClient;
//                opts.webSocketFactory = okHttpClient;
                //String[] trans = {"websocket"};
                opts.forceNew = true;
                opts.reconnection = false;
                //opts.transports = trans;

                //socket = IO.socket("https://13.125.78.77:9014", opts);
                SocketConnection.setSocket(IO.socket(ServerURL.DNA_SERVER+ServerURL.PORT_SOCKET, opts));

                //연결!
                SocketConnection.getSocket().connect();
                Log.e("SocketConnection", "Try connect");
                Log.e("SocketConnection", SocketConnection.getSocket().connected()+"");

//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (KeyManagementException e) {
//                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static void emit(String event, Object args){
        if(SocketConnection.getSocket().connected() == false){
            Log.e("Socket is Connected", SocketConnection.getSocket().connected()+"");
            SocketConnection.getSocket().connect();
        }
        //Log.e("Socket Emit", event);
        SocketConnection.getSocket().emit(event, args);
    }
    public static void emit(String event, Object arg1, Object arg2){
        if(SocketConnection.getSocket().connected() == false){
            Log.e("Socket is Connected", SocketConnection.getSocket().connected()+"");
            SocketConnection.getSocket().connect();
        }
        //Log.e("Socket Emit", event);
        SocketConnection.getSocket().emit(event, arg1, arg2);
    }

    public static void disconnect() {
        if(SocketConnection.getSocket()!=null){
            SocketConnection.getSocket().disconnect();
        }
        SocketConnection.setSocket(null);
        SocketConnection.setInstance(null);
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        SocketConnection.socket = socket;
    }

    public static SocketConnection getInstance() {
        return instance;
    }

    public static void setInstance(SocketConnection instance) {
        SocketConnection.instance = instance;
    }

    public static Activity getAct() {
        return act;
    }

    public static void setAct(Activity act) {
        SocketConnection.act = act;
    }

//
//    private Socket mSocket;
//    {
//        try {
//            //mSocket = IO.socket("https://dna.soyoungpark.me:9014");
//
//            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            };
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//
//                }
//
//                @Override
//                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//
//                }
//
//                @Override
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new java.security.cert.X509Certificate[0];
//                }
//            }};
//            X509TrustManager trustManager = (X509TrustManager) trustAllCerts[0];
//
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, null);
//            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .hostnameVerifier(hostnameVerifier)
//                    .sslSocketFactory(sslSocketFactory, trustManager)
//                    .build();
//
//            IO.Options opts = new IO.Options();
//            opts.callFactory = okHttpClient;
//            opts.webSocketFactory = okHttpClient;
//
//            //mSocket = IO.socket("https://13.125.78.77:9014", opts);
//            mSocket = IO.socket(ServerURL.LOCAL_HOST+ServerURL.PORT_SOCKET, opts);
//
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//    }

}
