package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;


import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

/**
 * RequestProcessorFactoryFactory dealing with making sure that our XML-RPC-Server receives the same Email object for every call.
 *
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class EmailRequestProcessorFactoryFactory implements RequestProcessorFactoryFactory {
    private final RequestProcessorFactory factory = new EmailRequestProcessorFactory();
    private final Email email;

    public EmailRequestProcessorFactoryFactory(Email email) {
        this.email = email;
    }

    public RequestProcessorFactory getRequestProcessorFactory(Class aClass)
            throws XmlRpcException {
        return factory;
    }

    private class EmailRequestProcessorFactory implements RequestProcessorFactory {
        public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest)
                throws XmlRpcException {
            return email;
        }
    }
}



