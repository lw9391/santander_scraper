package scraper.util;

public class DataBuilder {
    public String buildQueryParams(String... keyValuePairs) {
        return "?" + buildX_www_form_urlencoded(keyValuePairs);
    }

    public String buildX_www_form_urlencoded(String... keyValuePairs) {
        if (keyValuePairs.length == 0 || keyValuePairs.length %2 != 0) {
            throw new IllegalArgumentException("Arguments must be provided as key - value pairs.");
        }
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < keyValuePairs.length - 3; i+=2) {
            params.append(keyValuePairs[i]);
            params.append("=");
            params.append(keyValuePairs[i + 1]);
            params.append("&");
        }
        int lastKeyIndex = keyValuePairs.length - 2;
        int lastValueIndex = keyValuePairs.length - 1;
        params.append(keyValuePairs[lastKeyIndex]);
        params.append("=");
        params.append(keyValuePairs[lastValueIndex]);
        return params.toString();
    }
}
