package no.ntnu.online.onlineguru.service;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import no.ntnu.online.onlineguru.service.services.history.History;
import no.ntnu.online.onlineguru.service.services.webserver.Webserver;
import no.ntnu.online.onlineguru.service.services.xmlrpcserver.StatefulXmlRpcServer;
import no.ntnu.online.onlineguru.service.services.xmlrpcserver.XmlRpcServer;
import org.apache.log4j.Logger;

public class OnlineGuruServices extends AbstractModule implements Module {
    static Logger logger = Logger.getLogger(OnlineGuruServices.class);

    @Override
    protected void configure() {
        try {
            bind(XmlRpcServer.class).toInstance(new StatefulXmlRpcServer());
            bind(Webserver.class).toInstance(new Webserver());
            bind(History.class).toInstance(new History());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
