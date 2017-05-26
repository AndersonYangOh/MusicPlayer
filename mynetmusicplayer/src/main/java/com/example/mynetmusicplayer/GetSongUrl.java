package com.example.mynetmusicplayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shush on 2017/5/25.
 */

public class GetSongUrl {

    public static String sendGet(String strUrl,String cookie) {

        try {
            // 1. 创建URL对象
            URL url = new URL(strUrl);
            // 2. 创建URL连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 3. 设置请求信息
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            urlConnection.setRequestProperty("Cookie",cookie);
            urlConnection.setRequestProperty("Referer","http://h.xiami.com/");
            // 4. 获取返回信息(输入流)
            int code = urlConnection.getResponseCode();
            if (code == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuffer.append(str);
                }

                return new String(stringBuffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
