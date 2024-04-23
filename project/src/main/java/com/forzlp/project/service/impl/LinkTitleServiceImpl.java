package com.forzlp.project.service.impl;

import com.forzlp.project.service.LinkTitleService;

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
