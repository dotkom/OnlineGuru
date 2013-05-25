package no.ntnu.online.onlineguru.service.services.xmlrpcserver;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory.RequestProcessorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Apr 11, 2011
 * Time: 1:01:21 AM
 */
public class StatefulXmlRpcRequestHandlerFactory implements RequestProcessorFactoryFactory, RequestProcessorFactory {
    private Map<String, Object> handlerMap = new HashMap<String, Object>();

    public void setHandler(String name, Object handler) {
        this.handlerMap.put(name, handler);
    }

    public Object getHandler(String name) {
        return this.handlerMap.get(name);
    }

    public RequestProcessorFactory getRequestProcessorFactory(Class arg0) throws XmlRpcException {
        return this;
    }

    public Object getRequestProcessor(XmlRpcRequest request) throws XmlRpcException {
        String handlerName = request.getMethodName().substring(0, request.getMethodName().lastIndexOf("."));
        if (!handlerMap.containsKey(handlerName)) throw new XmlRpcException("Unknown handler: " + handlerName);
        return handlerMap.get(handlerName);
    }
}
