package com.smile.MyIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author smi1e
 * Date 2019/7/18 11:31
 * Description
 */
public class MyClient {
    public static void main(String[] args) throws IOException {
        System.out.println("启动客户端");
        //创建一个网络连接
        Socket socket= new Socket("127.0.0.1",8888);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("你好，服务器！".getBytes());
        System.out.println("发送成功！！！");
        outputStream.flush();
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        StringBuffer stringBuffer = new StringBuffer();
        long s = System.currentTimeMillis();
        int read = inputStream.read(bytes);
//        System.out.println(read);
        stringBuffer.append(new String(bytes,0,bytes.length));
        long e = System.currentTimeMillis();
        System.out.println("从服务器接收信息："+stringBuffer.toString());
        System.out.println("耗时: "+(e-s));
        socket.close();
    }
}
