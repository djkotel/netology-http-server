package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, List<String>> queryParams = new HashMap<>();
    private final Map<String, List<String>> postParams = new HashMap<>();

    public Request(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = in.readLine();
        if (requestLine == null) throw new IOException("Empty request");
        String[] parts = requestLine.split(" ");
        method = parts[0];
        String uriStr = parts[1];
        if (uriStr.contains("?")) {
            path = uriStr.substring(0, uriStr.indexOf("?"));
            var query = uriStr.substring(uriStr.indexOf("?") + 1);
            parseParams(queryParams, query);
        } else {
            path = uriStr;
        }

        String line;
        int contentLength = 0;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        if (method.equalsIgnoreCase("POST") && contentLength > 0) {
            char[] body = new char[contentLength];
            in.read(body);
            parseParams(postParams, new String(body));
        }
    }

    private void parseParams(Map<String, List<String>> target, String query) {
        List<NameValuePair> params = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
        for (var p : params) {
            target.computeIfAbsent(p.getName(), k -> new ArrayList<>()).add(p.getValue());
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getQueryParam(String name) {
        return queryParams.getOrDefault(name, List.of());
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public List<String> getPostParam(String name) {
        return postParams.getOrDefault(name, List.of());
    }

    public Map<String, List<String>> getPostParams() {
        return postParams;
    }
}
