package com.hi.servicePkg;

/**
 * Created by a.akhondian on 6/21/2015.
 */
public class Response {

    public Response(String responseLang, String isLast, String responseText) {
        this.responseLang = responseLang;
        this.isLast = isLast;
        this.responseText = responseText;
    }

    public Response() {
    }

    String responseText;
    String responseLang;
    String isLast;

    public String getIsLast() {
        return isLast;
    }

    public String getResponseLang() {
        return responseLang;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setIsLast(String isLast) {
        this.isLast = isLast;
    }

    public void setResponseLang(String responseLang) {
        this.responseLang = responseLang;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}
