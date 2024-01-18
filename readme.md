## 这是一个安卓环境下使用socket io库客户端和服务端通信的demo
> 基于https通信搞了好久，安卓环境下要自己生成bks证书
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

服务端效果：

![image](https://github.com/caijianxiong/android-socketIo/blob/master/readimg01.jpg)



使用https url通信建立连接，以下时生成安卓环境证书的过程：

1，工具

 使用KeyStore Explorer创建一个BKS[密钥库](https://keystore-explorer.org/index.html)，并将其作为“keystore.bks”保存到android的raw文件夹。或者，您也可以使用以下代码来创建密钥库文件，然后只需使用[密钥库资源管理器](https://keystore-explorer.org/index.html)将其打开，并将其类型更改为 BKS 

 [密钥库资源管理器 - 下载 (keystore-explorer.org)](https://keystore-explorer.org/downloads.html) 

2，windows打开cmd终端，粘贴下面这一段命令，其中的-storepass后面的值可以自己修改成自己storepass。

`keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.bks -storepass myKeyStorePass -validity 360 -keysize 2048 -ext SAN=DNS:localhost,IP:127.0.0.1  -validity 9999`

上面的命令应该不难理解，就是你给密钥文件设置的生成位置，key alias、key password和key store、validity ，回车后，需要继续按照提示输入相关信息，依次填写好信息后，输入 Y 确认信息即可。上面一行的storepass为myKeyStorePass，别名为selfsigned，有效期为360天。

3，然后用上面安装好的密钥库资源管理器KeyStore Explorer去找到这个keystore.bks文件打开，打开的时候输入上面的storepass密码就行(ps:我被这里坑了好久，记住要输入storepass的这个密码，不是keypass的密码)，打开后，找到Tools--->change KeyStore Type--->BKS保存就行。
4， 将上一步中生成的keystore.bks做了type转换后，将文件放入到项目的main->res->raw目录下，如果没这个文件夹，新建一个就行

5，具体使用见demo源码

