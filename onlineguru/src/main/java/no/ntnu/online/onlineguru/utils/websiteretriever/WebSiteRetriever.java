package no.ntnu.online.onlineguru.utils.websiteretriever;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WebSiteRetriever implements Runnable {

    private Logger logger = Logger.getLogger(WebSiteRetriever.class);

    private String url;
    private Object caller;
    private String methodName;
    private Class[] parameterTypes;
    private Object[] returnObjects;

    protected WebSiteRetriever() { }

    protected WebSiteRetriever(String url, Object caller, String methodName) {
        this.url = url;
        this.caller = caller;
        this.methodName = methodName;
    }

    protected WebSiteRetriever(String url, Object caller, String methodName, Class[] parameterTypes, Object... returnObjects) {
        this(url, caller, methodName);
        this.parameterTypes = parameterTypes;
        this.returnObjects = returnObjects;
    }

    public void run() {

        try {
            Method method = getMethod();
            Response response = Jsoup.connect(url).timeout(10000).execute();

            returnObjects = getReturnObjects(response);
            method.invoke(caller, returnObjects);

        } catch (IllegalAccessException e) {
            logger.error("Illegal Access Exception", e);
        } catch (InvocationTargetException e) {
            logger.error("Invocation Target Exception", e);
        } catch (IOException e) {
            logger.error("IO Exception", e);
        } catch (NoSuchMethodException e) {
            logger.error("No Such Method Exception", e);
        }
    }

    private Method getMethod() throws NoSuchMethodException {

        if(parameterTypes == null)
            return caller.getClass().getMethod(methodName, Response.class);
        else
            return caller.getClass().getMethod(methodName, parameterTypes);
    }

    private Object[] getReturnObjects(Response response) throws IOException {

        List<Object> newReturnObjects = new ArrayList<Object>();

        if (parameterTypes == null || parameterTypes.length == 0) {
            // First parameter of callback method without specified parameter types MUST be the Response object
            // Otherwise, what's the point.
            newReturnObjects.add(response);
            return newReturnObjects.toArray();
        }


        // Put the passed return objects into order
        // A user can ask for the Response and Document objects, as well as pass objects it wants returned
        // Callback is not required to have Response and/or Document as parameters, but must then have assigned return objects
        // THE RETURN OBJECTS MUST BE IN THE SAME ORDER AS DEFINED IN PARAMETER TYPES

        ArrayIterator returnObjectsIterator = new ArrayIterator(returnObjects);

        for(Class clazz : parameterTypes) {

            if(clazz == Response.class) {
                newReturnObjects.add(response);
            }
            else if(clazz == Document.class) {
                newReturnObjects.add(response.parse());
            }
            else {
                // An object passed to us that the callback wants in return
                Object returnObject = null;

                if(returnObjectsIterator.hasNext()) {
                    returnObject = returnObjectsIterator.next();
                }

                if(returnObject != null && clazz == returnObject.getClass()) {
                    newReturnObjects.add(returnObject);
                }
            }
        }

        return newReturnObjects.toArray();
    }
}
