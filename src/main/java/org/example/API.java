package org.example;

public enum API {
    STEAM("https://store.steampowered.com/api/salepage/?id=", "franchise name","gta", "(separate words using \"-\")"),
    SPACEX("https://api.spacexdata.com/v3/history?id=", "history event id", "1","(write a number)"),
    IMDB("https://imdb.iamidiotareyoutoo.com/search?q=", "movie name", "star+wars", "" );
    private final String url;
    private final String parameter;
    private final String defaultParameterValue;
    private final String comment;
    API(String url, String parameter, String defaultParameterValue, String comment) {
        this.url = url;
        this.parameter = parameter;
        this.defaultParameterValue =defaultParameterValue;
        this.comment = comment;
    }
    public String getUrl() {
        return url;
    }
    public String getParameter() {
        return parameter;
    }
    public String getComment() {
        return comment;
    }
    public String createUrlWithDefaultParameter() {
        if(!defaultParameterValue.isEmpty()) {
            return url + defaultParameterValue;
        }
        return url;
    }
    public String createUrlWithParameter(String parameterValue) {
        if(parameterValue == null || parameterValue.isEmpty()) {
            return url;
        }
        return url + parameterValue;
    }
}
