package com.example.socketiodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ClientActivity extends AppCompatActivity {
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
    }


    private void initSocket() {
        try {

            String url = "http://192.168.101.54:9423";
//            val url = "http://192.168.0.10:9423"


            IO.Options option = new IO.Options();
//            option.path = "/socket"
            option.transports = new String[]{"websocket", "xhr-polling", "jsonp-polling"};
            option.reconnectionAttempts = 3;
            option.reconnectionDelay = 3000;
            option.timeout = 500;

//            var  sllContext:SSLContext=SSLContext.getInstance("TLSv1.2")

            socket = IO.socket(url, option);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("AAA", "第一次连接成功1");
                }
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    for (Object o : args) {
                        Log.e("AAA", "活动连接错误2:", (Throwable) o);
                    }
                }
            });

//            socket.on(Socket.EVENT_CONNECT_TIMEOUT, Emitter.Listener { args ->
//                for (o in args) {
//                    Log.e("AAA", "活动连接超时3$o")
//                }
//            })

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    for (Object o : args) {
                        Log.e("AAA", "断开连接4:$o");
                    }
                }
            });

//            socket.on(Socket.EVENT_CONNECTING, Emitter.Listener { args ->
//                for (o in args) {
//                    Log.e("AAA", "正在连接5$o")
//                }
//            })

            socket.on("main", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    tv_msg.setText(args[0].toString());
                    Log.e("AAA", "服务器发来msg：" + args[0]);
                }
            });

            socket.connect();
            tv_msg.setText(url);

        } catch (Exception e) {
            Log.e("TAG", "initSocket: ", e);
        }
    }

}