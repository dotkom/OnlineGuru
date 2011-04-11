package no.ntnu.online.onlineguru.utils.xmlrpcserver;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Apr 11, 2011
 * Time: 12:54:46 AM
 */
public class StatefulXmlRpcServer implements XmlRpcServer {
    private static final int PORT = 9876;
    private WebServer webServer = null;
    private PropertyHandlerMapping propertyHandlerMapping = null;
    private StatefulXmlRpcRequestHandlerFactory handlerFactory = null;
    private org.apache.xmlrpc.server.XmlRpcServer xmlRpcServer = null;

    public StatefulXmlRpcServer() throws Exception {
        webServer = new WebServer(PORT);
        xmlRpcServer = this.webServer.getXmlRpcServer();
        handlerFactory = new StatefulXmlRpcRequestHandlerFactory();

        propertyHandlerMapping = new PropertyHandlerMapping();
        propertyHandlerMapping.setRequestProcessorFactoryFactory(this.handlerFactory);

        xmlRpcServer.setHandlerMapping(propertyHandlerMapping);
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);

        webServer.start();
    }


    @Override
    public void addHandler(String name, Object requestHandler) throws Exception {
        handlerFactory.setHandler(name, requestHandler);
        propertyHandlerMapping.addHandler(name, requestHandler.getClass());

        restartServer();
    }

    private void restartServer() throws IOException {
        webServer.shutdown();
        webServer.start();
    }
}

