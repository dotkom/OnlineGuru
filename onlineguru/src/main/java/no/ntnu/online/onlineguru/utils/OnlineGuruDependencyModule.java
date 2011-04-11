package no.ntnu.online.onlineguru.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import no.ntnu.online.onlineguru.utils.xmlrpcserver.StatefulXmlRpcServer;
import no.ntnu.online.onlineguru.utils.xmlrpcserver.XmlRpcServer;

public class OnlineGuruDependencyModule extends AbstractModule implements Module {
    @Override
    protected void configure() {
        bind(XmlRpcServer.class).to(StatefulXmlRpcServer.class);
    }
}
