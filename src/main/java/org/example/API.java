package org.example;

public enum API {
    STEAM("https://store.steampowered.com/api/salepage/?id=", "franchise name","gta", "(separate words using \"-\")"),
    SPACEX("https://api.spacexdata.com/v3/history?id=", "history event id", "1","(write a number)"),
    IMDB("https://imdb.iamidiotareyoutoo.com/search?q=", "movie name", "star+wars", "" );

    private String url;
    private final String parameterName;
    private final String defaultParameterValue;
    private final String comment;

    API(String url, String parameterName, String defaultParameterValue, String comment) {
        this.url = url;
        this.parameterName = parameterName;
        this.defaultParameterValue = defaultParameterValue;
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }
    public String getParameterName() {
        return parameterName;
    }
    public String getComment() {
        return comment;
    }
    public void createUrlWithDefaultParameter() {
        url += defaultParameterValue;
    }
    public void createUrlWithParameter(String parameterValue) {
        if(parameterValue != null) {
            url += parameterValue;
        }
    }
}
