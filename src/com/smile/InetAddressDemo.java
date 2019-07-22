package com.smile;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author  by smi1e
 * Date 2019/6/18 16:50
 * Description
 */
public class InetAddressDemo {

    public static void main(String[] args) throws UnknownHostException {
        /**
         * InetAddress类（ip+dns）
         */
        InetAddress inetAddress = InetAddress.getByName("www.baidu.com");
        System.out.println(inetAddress.getHostAddress());

        /**
         * InetSocketAddress类：包含端口号
         */
        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress,80);
        System.out.println(inetSocketAddress.getAddress());
    }

}
