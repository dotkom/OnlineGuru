package no.ntnu.online.onlineguru.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import no.ntnu.online.onlineguru.utils.history.History;
import no.ntnu.online.onlineguru.utils.webserver.Webserver;
import no.ntnu.online.onlineguru.utils.xmlrpcserver.StatefulXmlRpcServer;
import no.ntnu.online.onlineguru.utils.xmlrpcserver.XmlRpcServer;
import org.apache.log4j.Logger;

public class OnlineGuruDependencyModule extends AbstractModule implements Module {
    static Logger logger = Logger.getLogger(OnlineGuruDependencyModule.class);

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
