package com.smile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author  smi1e
 * Date 2019/6/18 16:55
 * Description
 */
public class UrlDemo {
    public static void main(String[] args) throws IOException {
        URL url = new URL("http://open.iciba.com/dsapi/");
        InputStream inputStream = url.openStream();
        OutputStream outputStream  = new FileOutputStream(new File("res.txt"));
        byte[] res = new byte[1024];
        StringBuffer stringBuffer = new StringBuffer();
        while (inputStream.read(res)!=-1){
            stringBuffer.append(new String(res));
            outputStream.write(res);
        }
        System.out.println(stringBuffer.toString());
        inputStream.close();

    }
}
