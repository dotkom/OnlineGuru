package no.ntnu.online.onlineguru.utils.websiteretriever;

import no.ntnu.online.onlineguru.utils.websiteretriever.model.ISetUrl;

public class WebSiteRetrieverFactory {

    public static ISetUrl start() {
        return new WebSiteRetrieverBuilder();
    }

}
