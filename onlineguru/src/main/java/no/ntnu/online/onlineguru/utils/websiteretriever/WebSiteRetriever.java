package no.ntnu.online.onlineguru.utils.websiteretriever;

import no.ntnu.online.onlineguru.utils.websiteretriever.model.WebSiteRetrieverResult;
import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WebSiteRetriever implements Runnable {

    private Logger logger = Logger.getLogger(WebSiteRetriever.class);

    private String url;
    private Object caller;
    private String methodName;
    private Object[] returnObjects;

    protected WebSiteRetriever() { }

    protected WebSiteRetriever(String url, Object caller, String methodName) {
        this.url = url;
        this.caller = caller;
        this.methodName = methodName;
    }

    protected WebSiteRetriever(String url, Object caller, String methodName, Object... returnObjects) {
        this(url, caller, methodName);
        this.returnObjects = returnObjects;
    }

    public void run() {

        try {
            Method method = caller.getClass().getMethod(methodName, WebSiteRetrieverResult.class);
            Response response = Jsoup.connect(url).timeout(10000).execute();

            WebSiteRetrieverResult result = null;

            if(returnObjects == null)
                result = new WebSiteRetrieverResult(url, caller, methodName, response, response.parse());
            else
                result = new WebSiteRetrieverResult(url, caller, methodName, response, response.parse(), returnObjects);

            method.invoke(caller, result);

        } catch (IllegalAccessException e) {
            logger.error("Illegal Access Exception", e);
        } catch (InvocationTargetException e) {
            logger.error("Invocation Target Exception", e);
        } catch (IOException e) {
            logger.error("IO Exception", e);
        } catch (NoSuchMethodException e) {
            logger.error("No Such Method Exception", e);
        } catch (ClassCastException e) {
            logger.error("Casting objects failed", e);
        }
    }
}
