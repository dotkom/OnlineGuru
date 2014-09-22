package no.ntnu.online.onlineguru.utils.websiteretriever.model;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public class WebSiteRetrieverResult {

    private String url;
    private Object caller;
    private String methodName;
    private Object[] passedObjects;
    private Response webSiteResponse;
    private Document document;


    public WebSiteRetrieverResult(String url, Object caller, String methodName, Response webSiteResponse, Document document) {
        this.url = url;
        this.caller = caller;
        this.methodName = methodName;
        this.webSiteResponse = webSiteResponse;
        this.document = document;
    }

    public WebSiteRetrieverResult(String url, Object caller, String methodName, Response webSiteResponse, Document document, Object[] passedObjects) {
        this(url, caller, methodName, webSiteResponse, document);
        this.passedObjects = passedObjects;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public Object getCaller() {
        return caller;
    }


    public void setCaller(Object caller) {
        this.caller = caller;
    }


    public String getMethodName() {
        return methodName;
    }


    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    public Object[] getPassedObjects() {
        return passedObjects;
    }


    public void setPassedObjects(Object[] passedObjects) {
        this.passedObjects = passedObjects;
    }


    public Response getWebSiteResponse() {
        return webSiteResponse;
    }

    public void setWebSiteResponse(Response webSiteResponse) {
        this.webSiteResponse = webSiteResponse;
    }


    public Document getDocument() {
        return document;
    }


    public void setDocument(Document document) {
        this.document = document;
    }
}
