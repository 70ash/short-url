package com.forzlp.project.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class URLParser {

    public static String extractProtocol(String urlString) {
        try {
            // 将字符串转换为URL对象
            URL url = new URL(urlString);
            // 获取URL对象的协议部分
            String protocol = url.getProtocol();
            return protocol;
        } catch (MalformedURLException e) {
            // 如果链接格式不正确，则捕获异常并打印错误信息
            System.err.println("URL格式不正确：" + e.getMessage());
            return null;
        }
    }

    public static String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString.toString());
            String host = url.getHost();
            if (host != null) {
                return host;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public static void main(String[] args) {
        String url = "https://blog.csdn.net/JackTian_/article/details/131699365?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522170936314916800222866656%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=170936314916800222866656&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-1-131699365-null-null.142^v99^pc_search_result_base7&utm_term=%E5%89%8D%E7%AB%AF%E6%80%8E%E4%B9%88%E4%BC%A0date%20%E5%88%B0%E5%90%8E%E7%AB%AF&spm=1018.2226.3001.4187";
        System.out.println("Domain: " + extractDomain(url));
        System.out.println("protocol: " + extractProtocol(url));
    }
}
