package com.example.mynetmusicplayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by shush on 2017/5/25.
 */

public class GetSongUrl {
    private static String sendGet(String strUrl) {

        try {
            // 1. 创建URL对象
            URL url = new URL(strUrl);
            // 2. 创建URL连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 3. 设置请求信息
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept","*/*");
            urlConnection.setRequestProperty("Connection","keep-alive");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

            // 4. 获取返回信息(输入流)
            int code = urlConnection.getResponseCode();
            if (code == 200){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String str = null;
                while ((str = bufferedReader.readLine()) !=null){
                    stringBuffer.append(str);
                }
                Map<String, List<String>> map = urlConnection.getHeaderFields();

                return GetCookie(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String GetCookie(Map<String, List<String>> map) {
        Set<String> sets = map.keySet();
        Iterator iter = sets.iterator();
        while (iter.hasNext()){
            String key = (String) iter.next();
            System.out.println("key="+key);
            if (key != null && key.equals("Set-Cookie")){
                List<String> cookieinfos = map.get(key);
                for (int i = 0; i <cookieinfos.size() ; i++) {
                    System.out.println("Set-Cookie="+cookieinfos.get(i));
                    String cookieContent = cookieinfos.get(i);
                    if(cookieContent.contains("_xsrf")){
                        int start = cookieContent.indexOf("_xsrf");
                        int end = cookieContent.indexOf(";");
                        String cookie = cookieContent.substring(start,end);
                        System.out.println("cookie="+cookie);
                        return cookie;
                    }
                }
            }
        }

        return null;
    }
}
