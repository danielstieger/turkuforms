package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.router.QueryParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParamInfo {
    private String[] urlParts;
    private Map<String, List<String>> params;

    public ParamInfo(String wildCardParam, QueryParameters queryParameters) {
        params = queryParameters.getParameters();

        String trimmed = trimStringByString(wildCardParam, "/");
        if (trimmed.isEmpty()) {
            urlParts = new String[0];
        } else {
            urlParts = trimmed.split("/");
        }
    }

    public int getNumUrlParts() {
        return urlParts.length;
    }

    public String getUrlPart(int num) {
        return urlParts[num];
    }

    public String getFirstQueryParam(String key) {
        if (params.containsKey(key)) {
            return params.get(key).get(0);
        }
        return null;
    }


    public List<String> getQueryParams(String key) {
        if (params.containsKey(key)) {
            return params.get(key);
        } else {
            return new ArrayList<>();
        }
    }


    private String trimStringByString(String text, String trimBy) {
        int beginIndex = 0;
        int endIndex = text.length();

        while (text.substring(beginIndex, endIndex).startsWith(trimBy)) {
            beginIndex += trimBy.length();
        }

        while (text.substring(beginIndex, endIndex).endsWith(trimBy)) {
            endIndex -= trimBy.length();
        }

        return text.substring(beginIndex, endIndex);
    }
}
