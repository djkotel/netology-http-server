package server;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, List<String>> queryParams;

    public Request(String rawRequest) {
        String[] lines = rawRequest.split("\r\n");
        String[] firstLine = lines[0].split(" ");
        this.method = firstLine[0];

        String uri = firstLine[1];
        int queryIndex = uri.indexOf('?');
        if (queryIndex != -1) {
            this.path = uri.substring(0, queryIndex);
            String queryString = uri.substring(queryIndex + 1);
            this.queryParams = parseQueryParams(queryString);
        } else {
            this.path = uri;
            this.queryParams = new HashMap<>();
        }
    }

    private Map<String, List<String>> parseQueryParams(String queryString) {
        Map<String, List<String>> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            List<NameValuePair> pairs = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
            for (NameValuePair pair : pairs) {
                params.computeIfAbsent(pair.getName(), k -> new ArrayList<>()).add(pair.getValue());
            }
        }
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public List<String> getQueryParams() {
        List<String> allParams = new ArrayList<>();
        queryParams.forEach((key, values) -> values.forEach(value -> allParams.add(key + "=" + value)));
        return allParams;
    }
}