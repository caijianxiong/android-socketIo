1.  app module是client demo,serverdemo即为服务端demo
2.  使用时client和server两台机器必须在同一wifi下（局域网）
3.  server端IP自动获取，client连接IP以服务端为准修改如下

<!---->

     private void initSocket() {
            try {

                prepareOkHttpClient();

    //            String url = "http://192.168.101.165:9423";
                String url = "https://192.168.3.69:9423"; // IP，端口通server


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



依赖库

    client：

        // socket.io-client
        implementation('io.socket:socket.io-client:1.0.1') {
            // excluding org.json which is provided by Android
            exclude group: 'org.json', module: 'json'
        }

    server：

        implementation("com.corundumstudio.socketio:netty-socketio:2.0.3") {// jackson-core-2.15.0
            // excluding org.json which is provided by Android
            exclude group: 'org.json', module: 'json'
        }

[![image.png](https://note.youdao.com/yws/res/2129/WEBRESOURCEc5e986b328355e8042654954261af395)](https://github.com/caijianxiong/android-socketIo/blob/master/readimg01.jpg)https://github.com/caijianxiong/android-socketIo/blob/master/readimg01.jpg
