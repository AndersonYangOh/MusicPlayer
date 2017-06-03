package com.example.mynetmusicplayer.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by shush on 2017/6/2.
 */

public class GetWeather {

    static String ref = "";

    public static String getWeather(final String latitude, final String longitude) {

        try {
            String url = "http://e.weather.com.cn/d/town/index?lat=" + latitude + "&lon=" + longitude;
            Document doc = null;
            doc = Jsoup.connect(url).get();
            Elements newsHeadlines = doc.select(".weather-two");
            for (Element element : newsHeadlines) {
                ref = element.text();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ref;

    }


}



