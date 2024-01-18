package com.example.serverdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;

public class ServerActivity extends AppCompatActivity {

    private String TAG = "server_" + this.getClass().getSimpleName();
    private String hostName = "192.168.3.2";
    private final int port = 9423;
    private SocketIOServer mServer;
    private TextView textView;
    private Button bt_connect, bt_send, bt_disconnect, bt_server;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_address);
        bt_connect = findViewById(R.id.bt_connect);
        bt_send = findViewById(R.id.bt_send);
        bt_disconnect = findViewById(R.id.bt_disconnect);
        init();
        initListener();

    }

    private void initListener() {
        bt_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServer.stop();
            }
        });
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
            //
            configuration.setKeyStorePassword("kandao");
            InputStream inputStream = getResources().getAssets().open("keystore.bks");
            Log.i(TAG, "init -----type: " + KeyStore.getDefaultType());
            configuration.setKeyStoreFormat(KeyStore.getDefaultType());
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(inputStream, "kandao".toCharArray());
//            KeyManagerFactory KeyManagerFactory = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
//            KeyManagerFactory.init(keyStore, "kandao".toCharArray());
            configuration.setKeyStore(inputStream);

            //
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
                    client.sendEvent("disconnect from server close");
                    client.disconnect();
                }
            });


            mServer.addEventListener("main", String.class, new DataListener<String>() {
                @Override
                public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
                    Log.i(TAG, "onData: " + data);
                    client.sendEvent("main", "yes I rev client msg ,this server callback");
                }
            });

            mServer.addPingListener(new PingListener() {
                @Override
                public void onPing(SocketIOClient client) {
                    Log.i(TAG, "rev onPing: ");
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