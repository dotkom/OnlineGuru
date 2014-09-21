package no.ntnu.online.onlineguru.utils;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtil {


    private static Logger logger = Logger.getLogger(UrlUtil.class);


    public static String encodeUrl(String url, String encoding) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return url;
    }
}
