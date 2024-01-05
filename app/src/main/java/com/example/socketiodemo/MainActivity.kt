package com.example.socketiodemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import javax.net.ssl.SSLContext


class MainActivity : AppCompatActivity() {

    private lateinit var socket: io.socket.client.Socket
    private lateinit var bt_connect: Button
    private lateinit var bt_send: Button
    private lateinit var bt_disconnect: Button
    private lateinit var bt_server: Button
    private lateinit var tv_msg: TextView;
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        Log.i(TAG, "onCreate: ")

        bt_connect = findViewById(R.id.bt_connect)
        bt_send = findViewById(R.id.bt_send)
        bt_disconnect = findViewById(R.id.bt_disconnect)
        tv_msg = findViewById(R.id.tv_text)
        bt_server = findViewById(R.id.bt_server)
        initEvent()
    }


    private fun initEvent() {

        bt_connect.setOnClickListener(View.OnClickListener {
            initSocket()
        })
        bt_send.setOnClickListener {
            socket.emit("main", "form client msg 1111 ")
        }
        bt_disconnect.setOnClickListener {
            socket.close()
        }
        bt_server.setOnClickListener {
//            var intent = Intent(applicationContext, SocketConnectServer::class.java)
//            startService(intent)
        }
    }


    private fun initSocket() {
        try {

            val url = "https://192.168.101.54:9423"
//            val url = "http://192.168.0.10:9423"


            val option = IO.Options()
//            option.path = "/socket"
            option.transports = arrayOf("websocket", "xhr-polling", "jsonp-polling")
            option.reconnectionAttempts = 3
            option.reconnectionDelay = 3000;
            option.timeout = 500

            var  sllContext:SSLContext=SSLContext.getInstance("TLSv1.2")

            socket = IO.socket(url, option)
            socket.on(Socket.EVENT_CONNECT, Emitter.Listener {
                Log.e("AAA", "第一次连接成功1");
            })
            socket.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener { args ->
                for (o in args) {
                    Log.e("AAA", "活动连接错误2:", o as Throwable?)
                }
            })

//            socket.on(Socket.EVENT_CONNECT_TIMEOUT, Emitter.Listener { args ->
//                for (o in args) {
//                    Log.e("AAA", "活动连接超时3$o")
//                }
//            })

            socket.on(Socket.EVENT_DISCONNECT, Emitter.Listener { args ->
                for (o in args) {
                    Log.e("AAA", "断开连接4:$o")
                }
            })

//            socket.on(Socket.EVENT_CONNECTING, Emitter.Listener { args ->
//                for (o in args) {
//                    Log.e("AAA", "正在连接5$o")
//                }
//            })

            socket.on("main", Emitter.Listener { args ->
                tv_msg.setText(args[0].toString())
                Log.e("AAA", "服务器发来msg：" + args[0]);
            })

            socket.connect()
            tv_msg.setText(url)

        } catch (e: Exception) {
            Log.e("TAG", "initSocket: ", e)
        }
    }


}