package com.smile.MyIO.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * @author smi1e
 * Date 2019/7/18 13:11
 * Description
 */
public class MyNIO {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService executorService = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    public MyNIO(int port) throws IOException {
        //打开服务器channel
        serverSocketChannel = ServerSocketChannel.open();
        //chanel绑定8888端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8888));
        //设置为非阻塞的
        serverSocketChannel.configureBlocking(false);
        //创建selector
        selector = Selector.open();
        //注册channel,等待接入
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器开启成功");
    }


    public void go() throws IOException {
        //开始轮询
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                synchronized (this) {
                    if (selectionKey.isAcceptable()) {
                    /*
                     * 表示可以接收，注册一个可以读的通道
                     */
                    ServerSocketChannel serverSocketChannel =(ServerSocketChannel) selectionKey.channel();
                    //连接
                    SocketChannel accept = serverSocketChannel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        //表示可以读取(创建新线程去读)
                        //关闭可读权限，否则会有很多线程去反复读取
                        System.out.println("关闭读权限");
                        selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_READ));
                        executorService.execute(new MyThread(selectionKey, 0));

                    } else if (selectionKey.isWritable()) {
                        //表示可以写
                        //关闭写权限，否则会有很多线程去反复读取
                        System.out.println("关闭写权限");
                        selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_WRITE));
                        executorService.execute(new MyThread(selectionKey, 1));
                    }
                }
            }
        }
    }

    /**
     * 新的处理线程
     */
    class MyThread extends Thread {
        private SelectionKey selectionKey;
        /**
         * 0 read
         * 1 write
         */
        private int type;

        public MyThread(SelectionKey selectionKey, int type) {
            this.selectionKey = selectionKey;
            this.type = type;
        }

        @Override
        public void run() {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            if (type == 0) {
                System.out.println("开始读取");
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                StringBuffer stringBuffer = new StringBuffer();
                try {
                    while (channel.read(readBuffer) > 0) {
                        stringBuffer.append(new String(readBuffer.array(), 0, readBuffer.array().length));
                        readBuffer.clear();
                    }
                    System.out.println("接收成功，来自客户端的信息为：" + stringBuffer.toString());
                    //添加写权限
                    selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
                    System.out.println("添加写权限");
                    //唤醒selector，继续轮询
                    selectionKey.selector().wakeup();
                    System.out.println("唤醒");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("开始写回");
                ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                try {
                    writeBuffer.put("服务器已经收到你的信息".getBytes());
                    writeBuffer.flip();
                    channel.write(writeBuffer);
                    System.out.println("发送成功！");
                    //添加读权限
                    System.out.println("添加读权限");
                    selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
                    //唤醒selector，继续轮询
                    selectionKey.selector().wakeup();
                    System.out.println("唤醒");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MyNIO myNIO = new MyNIO(8888);
        myNIO.go();
    }

}
