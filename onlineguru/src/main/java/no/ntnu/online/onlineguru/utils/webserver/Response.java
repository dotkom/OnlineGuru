package no.ntnu.online.onlineguru.utils.webserver;

/**
 "Copyright (C) 2001,2005-2011 by Jarno Elonen <elonen@iki.fi>\n" +
 "and Copyright (C) 2010 by Konstantinos Togias <info@ktogias.gr>\n" +
 "\n" +
 "Redistribution and use in source and binary forms, with or without\n" +
 "modification, are permitted provided that the following conditions\n" +
 "are met:\n" +
 "\n" +
 "Redistributions of source code must retain the above copyright notice,\n" +
 "this list of conditions and the following disclaimer. Redistributions in\n" +
 "binary form must reproduce the above copyright notice, this list of\n" +
 "conditions and the following disclaimer in the documentation and/or other\n" +
 "materials provided with the distribution. The name of the author may not\n" +
 "be used to endorse or promote products derived from this software without\n" +
 "specific prior written permission. \n" +
 " \n" +
 "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n" +
 "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n" +
 "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n" +
 "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n" +
 "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n" +
 "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n" +
 "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n" +
 "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n" +
 "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n" +
 "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * HTTP response.
 * Return one of these from serve().
 */
public class Response {
    /**
     * Default constructor: response = HTTP_OK, data = mime = 'null'
     */
    public Response() {
        this.status = NanoHTTPD.HTTP_OK;
    }

    /**
     * Basic constructor.
     */
    public Response(String status, String mimeType, InputStream data) {
        this.status = status;
        this.mimeType = mimeType;
        this.data = data;
    }

    /**
     * Convenience method that makes an InputStream out of
     * given text.
     */
    public Response(String status, String mimeType, String txt) {
        this.status = status;
        this.mimeType = mimeType;
        try {
            this.data = new ByteArrayInputStream(txt.getBytes("UTF-8"));
        } catch (java.io.UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    /**
     * Adds given line to the header.
     */
    public void addHeader(String name, String value) {
        header.put(name, value);
    }

    /**
     * HTTP status code after processing, e.g. "200 OK", HTTP_OK
     */
    public String status;

    /**
     * MIME type of content, e.g. "text/html"
     */
    public String mimeType;

    /**
     * Data of the response, may be null.
     */
    public InputStream data;

    /**
     * Headers for the HTTP response. Use addHeader()
     * to add lines.
     */
    public Properties header = new Properties();
}
