package org.example;

public enum API {
    STEAM("https://store.steampowered.com/api/salepage/?id=", "franchise name", "(separate words using \"-\")"),
    SPACEX("https://api.spacexdata.com/v3/history?id=", "history event id", "(write a number)"),
    IMDB("https://imdb.iamidiotareyoutoo.com/search?q=", "movie name", "");
    private final String url;
    private final String paramName;
    private final String comment;
    API(String url, String parameter, String comment) {
        this.url = url;
        this.paramName = parameter;
        this.comment = comment;
    }
    public String getUrl() {
        return url;
    }
    public String getParamName() {
        return paramName;
    }
    public String getComment() {
        return comment;
    }
}
