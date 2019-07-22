package com.smile.MyIO.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author smi1e
 * Date 2019/7/18 14:58
 * Description
 */
public class MyAIO {

    public static void main(String[] args) throws IOException {
        final AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(8888));
        asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                asynchronousServerSocketChannel.accept(null,this);
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                client.read(readBuffer,readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();
                        System.out.println(new String(attachment.array(),0,result));
                        //写回
                        System.out.println("开始写回");
                        client.write(ByteBuffer.wrap("收到消息".getBytes()));
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {

                    }
                });
            }
            @Override
            public void failed(Throwable exc, Object attachment) {

            }
        });
        while (true){

        }
    }



}
