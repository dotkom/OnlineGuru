package no.ntnu.online.onlineguru.utils.websiteretriever;

import no.ntnu.online.onlineguru.utils.websiteretriever.model.*;

public class WebSiteRetrieverBuilder implements ISetUrl, ISetCaller, ISetMethodName, IFetchAndSetReturnObjects, IFetchWebSite {

    private String url;
    private Object caller;
    private String methodName;
    private Object[] returnObjects;

    protected WebSiteRetrieverBuilder() { }

    public ISetCaller setUrl(String url) {
        this.url = url;
        return this;
    }

    public ISetMethodName setCaller(Object caller) {
        this.caller = caller;
        return this;
    }

    public IFetchAndSetReturnObjects setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public IFetchWebSite setReturnObjects(Object... returnObjects) {
        this.returnObjects = returnObjects;
        return this;
    }

    public void fetch() {

        if(returnObjects == null) {
            WebSiteRetriever retriever = new WebSiteRetriever(url, caller, methodName);
            new Thread(retriever).start();
        }
        else {
            WebSiteRetriever retriever = new WebSiteRetriever(url, caller, methodName, returnObjects);
            new Thread(retriever).start();
        }
    }
}
