package com.example.socketiodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

public class ClientActivity extends AppCompatActivity {

    private static OkHttpClient mOkHttpClient;
    private io.socket.client.Socket socket;
    private Button bt_connect, bt_send, bt_disconnect, bt_server;
    private TextView tv_msg;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        bt_connect = findViewById(R.id.bt_connect);
        bt_send = findViewById(R.id.bt_send);
        bt_disconnect = findViewById(R.id.bt_disconnect);
        tv_msg = findViewById(R.id.tv_text);
        bt_server = findViewById(R.id.bt_server);
        initEvent();
    }

    private void initEvent() {

        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSocket();
            }
        });
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("main", "form client msg 1111 ");
            }
        });

        bt_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity2.this, SocketConnectServer.class.java)
//                startService(intent);
            }
        });
        bt_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.close();
            }
        });
    }


    private void initSocket() {
        try {

            prepareOkHttpClient();

//            String url = "http://192.168.101.165:9423";
            String url = "https://192.168.3.69:9423";


            IO.Options option = new IO.Options();
//            option.path = "/socket"
            option.transports = new String[]{"websocket", "xhr-polling", "jsonp-polling"};
            option.reconnectionAttempts = 3;
            option.reconnectionDelay = 3000;
//            option.timeout = 1000;

            //
            option.secure = true;
            option.forceNew = true;
            option.callFactory = mOkHttpClient;
            option.webSocketFactory = mOkHttpClient;


            //

//            var  sllContext:SSLContext=SSLContext.getInstance("TLSv1.2")

            socket = IO.socket(url, option);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "第一次连接成功1");
                }
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    for (Object o : args) {
                        Log.e(TAG, "活动连接错误2:", (Throwable) o);
                    }
                }
            });

            socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    for (Object o : args) {
                        Log.e(TAG, "活动连接超时3$" + o);
                    }
                }

            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    for (Object o : args) {
                        Log.w(TAG, "断开连接4:" + o);
                    }
                }
            });

            socket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    for (Object o : args) {
                        Log.e(TAG, "正在连接5$" + o);
                    }
                }

            });

            socket.on("main", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    tv_msg.setText(args[0].toString());
                    Log.w(TAG, "服务器发来msg：" + args[0]);
                }
            });

            socket.on("disconnect", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.w(TAG, "服务器发来disconnect：" + args[0]);
                }
            });

            socket.connect();
            tv_msg.setText(url);

        } catch (Exception e) {
            Log.e("TAG", "initSocket: ", e);
        }
    }


    private void prepareOkHttpClient() throws GeneralSecurityException, IOException {

        InputStream inputStream = getResources().getAssets().open("keystore.bks");
        Log.i(TAG, "init -----type: " + KeyStore.getDefaultType());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(inputStream, "kandao".toCharArray());
//        configuration.setKeyStoreFormat(KeyStore.getDefaultType());

//        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//        kmf.init(ks, "kandao".toCharArray());
//
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        Log.i(TAG, "prepareOkHttpClient01: " + TrustManagerFactory.getDefaultAlgorithm());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        Log.i(TAG, "prepareOkHttpClient02: " + javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, "kandao".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        mOkHttpClient = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession sslSession) {
//                        return hostname.equals("localhost");
                        return true;
                    }
                })
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(sslContext.getSocketFactory(),
                        (X509TrustManager) tmf.getTrustManagers()[0])
                .build();
    }


}