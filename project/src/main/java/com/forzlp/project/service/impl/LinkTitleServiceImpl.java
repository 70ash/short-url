package com.forzlp.project.service.impl;

import com.forzlp.project.service.LinkTitleService;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author 70ash
 * Date 2024/4/23 下午8:04
 * Description:
 */
@Service
public class LinkTitleServiceImpl implements LinkTitleService {

    public String extractTitle(String urlString) {
        try {
            String content = getString(urlString);
            // 使用正则表达式从 HTML 内容中提取标题
            Pattern pattern = Pattern.compile("<title>(.*?)</title>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return "No title found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }
    @SneakyThrows
    public String extractIconUrl(String urlString) {
         URL targetUrl = new URL(urlString);
         HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
         connection.setRequestMethod("GET");
         connection.connect();
         int responseCode = connection.getResponseCode();
         if (HttpURLConnection.HTTP_OK == responseCode) {
             Document document = Jsoup.connect(urlString).get();
             Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
             if (faviconLink != null) {
                 return faviconLink.attr("abs:href");
             }
         }
         return null;
    }

    private String getString(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();

        return stringBuilder.toString();
    }
}
