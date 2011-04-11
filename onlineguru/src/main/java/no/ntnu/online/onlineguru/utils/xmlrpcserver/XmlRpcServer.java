package no.ntnu.online.onlineguru.utils.xmlrpcserver;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Apr 11, 2011
 * Time: 1:31:04 AM
 */
public interface XmlRpcServer {
    void addHandler(String name, Object requestHandler) throws Exception;
}
