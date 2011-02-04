package no.ntnu.online.onlineguru.utils.urlreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.ntnu.online.onlineguru.utils.Functions;

import org.apache.log4j.Logger;

public class URLReader implements Runnable {

	static Logger logger = Logger.getLogger(URLReader.class);
	private URLReaderUser caller;
	private String urltext;
	private Object[] callbackParameters = null;
	
	private String charset = "";
	private URL u;
	private HttpURLConnection uc;
	private boolean connected = false;
	private ArrayList<String> page = new ArrayList<String>();
	
	public URLReader(Object caller, String urltext) {
		if (caller instanceof URLReaderUser) {
			this.caller = (URLReaderUser)caller;
			this.urltext = urltext;
			
			new Thread(this).start();
		}
		else {
			logger.error("(URLReader) The calling object needs to implement the URLReaderUser interface.");
		}
	}	
	
	public URLReader(Object caller, String urltext, Object[] callbackParameters) {
		if (caller instanceof URLReaderUser) {
			this.caller = (URLReaderUser)caller;
			this.urltext = urltext;
			this.callbackParameters = callbackParameters;
			
			new Thread(this).start();
		}
		else {
			logger.error("(URLReader) The calling object needs to implement the URLReaderUser interface.");
		}
	}
	
	public void run() {
		connect();
		charset = getCharSet();
		getPage();	
		callback();		
	}
	private void connect() {
		
		try {
			u = new URL(urltext);
			uc = (HttpURLConnection) u.openConnection();
			
			int code = uc.getResponseCode();
			switch (code) {
				case HttpURLConnection.HTTP_OK:
					connected = true;
					return;
				case HttpURLConnection.HTTP_ACCEPTED:
					connected = true;
					return;
				case HttpURLConnection.HTTP_BAD_REQUEST:
					logger.error("Error: 400 Bad Request");
					return;
				case HttpURLConnection.HTTP_UNAUTHORIZED:
					logger.error("Error: 401 Unauthorized");
					return;
				case HttpURLConnection.HTTP_PAYMENT_REQUIRED:
					logger.error("Error: 402 Payment Required");
					return;
				case HttpURLConnection.HTTP_FORBIDDEN:
					logger.error("Error: 403 Forbidden");
					return;
				case HttpURLConnection.HTTP_NOT_FOUND:
					logger.error("Error: 404 Not found");
					return;
				case HttpURLConnection.HTTP_INTERNAL_ERROR:
					logger.error("Error: 500 Internal error");
					return;
				case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
					logger.error("Error: 501 Not implemented");
					return;
				case HttpURLConnection.HTTP_BAD_GATEWAY:
					logger.error("Error: 502 Bad Gateway");
					return;
				case HttpURLConnection.HTTP_UNAVAILABLE:
					logger.error("Error: 503 Unavailable");
					return;
				case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
					logger.error("Error: 504 Gateway timeout");
					return;
				case HttpURLConnection.HTTP_VERSION:
					logger.error("Error: 505 HTTP Version Not Supported");
					return;
				default:
					logger.error("Unknown HTML code: "+code);
					return;
			}
			
		} catch (MalformedURLException e) {
			logger.error("Error: malformed URL", e.getCause());
		} catch (IOException e) {
			logger.error("Error: IOException", e.getCause());
		}		
		
	}
	
	private String getCharSet() {
		if (connected) {
			
			String cs = "";
			int i = 1;
			String header = uc.getHeaderFieldKey(i);
			String value = uc.getHeaderField(i);
			
			while (header != null && value != null) {
				// Small print to show headers and their values. For debugging.
				//System.out.println("Header: "+header+" Value: "+value);
				if (header.equals("Content-Type")) {
					if (value.contains("=")) {
						cs = value.split("=")[1];
					}
				}
				
				header = uc.getHeaderFieldKey(i);
				value = uc.getHeaderField(i);
				i++;
			}
			
			try {
				Charset temp = Charset.forName(cs);
			} catch (IllegalCharsetNameException e) {
				logger.warn("Headers contained an Illegal charset, looking in source.");
				cs = "";
			} catch (UnsupportedCharsetException e) {
				logger.warn("Headers contained an Unsupported charset, looking in source.");
				cs = "";
			}
			
			if (cs.isEmpty()) {
				return getCharSetFromSource().toUpperCase();			
			}
			else {
				return cs.toUpperCase();
			}
		}
		else {
			return "";
		}
	}
	
	private String getCharSetFromSource() {
		String cs = "";
		try {
			Pattern pattern = Pattern.compile("xml\\sversion=\".*?\"\\sencoding=\"([^\"]+)\"\\s|<meta\\s+http-equiv=\"Content-Type\"\\s+content=\"text/html;\\s+charset=([^\"]+)\"\\s+/>", Pattern.CASE_INSENSITIVE);
			Matcher matcher;
			BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
			
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				matcher = pattern.matcher(inputLine);
				if (matcher.find()) {
					if (matcher.group(1) == null) {
						cs = matcher.group(2);
					}
					else {
						cs = matcher.group(1);
					}
				}
				if (inputLine.contains("</head>")) {
					break;
				}
			}
		} catch (IOException e) {
        	logger.error("URLReader.getPage() ", e.getCause());
        }
		
		return cs;
	}
	
	private void getPage() {
		if (connected && !charset.isEmpty()) {
			try  {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						u.openStream(), Charset.forName(charset)));
				
				String inputLine;
	            while ((inputLine = in.readLine()) != null) {
	            	inputLine = Functions.stripHTMLEntities(inputLine);
	                page.add(inputLine);
	            }
	            in.close();
	        } catch (IOException e) {
	        	logger.error("URLReader.getPage() ", e.getCause());
	        }
		}
	}
	
	private void callback() {
        if (callbackParameters != null) {
        	caller.urlReaderCallback(this, callbackParameters);
        }
        else {
        	caller.urlReaderCallback(this);
        }
	}
	
	public String getInString() {
        StringBuilder pageStringBuilder = new StringBuilder();
 
        for (String line : page) {
            pageStringBuilder.append(line);
        }
 
        String returnString = pageStringBuilder.toString();
   		returnString = returnString.replaceAll("\\s+", " ");
   		//System.out.println("Source: "+returnString);
        return returnString;
    }
    
    public String[] getInArray() {
    	return (String[])page.toArray();
    }
    
    public ArrayList<String> getInArrayList() {
    	return page;
    }

}