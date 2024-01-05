package com.example.serverdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.PingListener;
import com.corundumstudio.socketio.listener.PongListener;

public class ServerActivity extends AppCompatActivity {

    private String TAG = "server_" + this.getClass().getSimpleName();
    private String hostName = "192.168.3.2";
    private final int port = 9423;
    private SocketIOServer mServer;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_address);
        init();
    }

    private void init() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {

        try {

            hostName = NetUtils.getLocalIpAddress();
            Log.i(TAG, "init: ip:" + hostName);

            Configuration configuration = new Configuration();
            configuration.setHostname(hostName);
            configuration.setPort(port);
            SocketConfig socketConfig = new SocketConfig();
            socketConfig.setReuseAddress(true);
            configuration.setSocketConfig(socketConfig);
            configuration.setTransports(Transport.WEBSOCKET, Transport.POLLING);
            mServer = new SocketIOServer(configuration);
//            }
//        }, "server").start();

            mServer.addConnectListener(new ConnectListener() {
                @Override
                public void onConnect(SocketIOClient client) {
                    Log.i(TAG, "onConnect: ");
                }
            });

            mServer.addDisconnectListener(new DisconnectListener() {
                @Override
                public void onDisconnect(SocketIOClient client) {
                    Log.i(TAG, "onDisconnect: ");
                }
            });


            mServer.addEventListener("main", String.class, new DataListener<String>() {
                @Override
                public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
                    Log.i(TAG, "onData: ");
                }
            });

            mServer.addPingListener(new PingListener() {
                @Override
                public void onPing(SocketIOClient client) {
                    Log.i(TAG, "onPing: ");
                }
            });

            mServer.addPongListener(new PongListener() {
                @Override
                public void onPong(SocketIOClient client) {
                    Log.i(TAG, "onPong: ");
                }
            });

            mServer.start();

            Log.i(TAG, "init: server start success");
            textView.setText(hostName + ":" + port);

        } catch (Exception e) {
            Log.e(TAG, "init: ", e);
        }
    }
}