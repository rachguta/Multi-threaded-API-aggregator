package org.example;

public enum API {
    STEAM("https://store.steampowered.com/api/salepage/?id=", "franchise name","gta", "(separate words using \"-\")"),
    SPACEX("https://api.spacexdata.com/v3/history?id=", "history event id", "1","(write a number)"),
    IMDB("https://imdb.iamidiotareyoutoo.com/search?q=", "movie name", "star+wars", "" ),
    OPEN_METEO("https://api.open-meteo.com/v1/forecast?latitude=55.7558&longitude=37.6173&current_weather=true", "", "", "");
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
