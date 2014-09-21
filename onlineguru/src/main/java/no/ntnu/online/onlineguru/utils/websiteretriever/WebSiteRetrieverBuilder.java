package no.ntnu.online.onlineguru.utils.websiteretriever;

import no.ntnu.online.onlineguru.utils.websiteretriever.model.*;

public class WebSiteRetrieverBuilder implements ISetUrl, ISetCaller, ISetMethodName, IFetchAndSetParameterTypes, ISetReturnObjects, IFetchWebSite {

    private String url;
    private Object caller;
    private String methodName;
    private Class[] parameterTypes;
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

    public IFetchAndSetParameterTypes setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ISetReturnObjects setParameterTypes(Class... parameterTypes) {
        this.parameterTypes = parameterTypes;
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
            WebSiteRetriever retriever = new WebSiteRetriever(url, caller, methodName, parameterTypes,returnObjects);
            new Thread(retriever).start();
        }
    }
}
