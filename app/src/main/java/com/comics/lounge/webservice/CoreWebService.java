package com.comics.lounge.webservice;


import android.util.Log;

import com.comics.lounge.conf.Constant;
import com.comics.lounge.servicecallback.ServiceCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Gaurav
 */
public class CoreWebService implements Runnable {
    protected ServiceCallback serviceCallback = null;
    protected RequestType requestType = RequestType.GET_REQUEST;
    private URL urlInstance = null;
    private HttpURLConnection httpConn = null;
    private Map<String, String> paramCollection = null;
    private String lastResponse = null;
    private CountDownLatch countDownLatch = null;
    private String stringValue = null;
    private String testUrl = "";

    public CoreWebService(String url) {
        try {
            testUrl = url;
            urlInstance = new URL(url);
        } catch (MalformedURLException ex) {

        }
        paramCollection = new HashMap<String, String>();
    }

    protected String getLastResponse() {
        return lastResponse;
    }

    public void addParamWithoutKey(String value) {
        this.stringValue = value;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public void addParam(String key, String value) {
        paramCollection.put(key, value);
    }

    public String getParam(String key) {
        return paramCollection.get(key);
    }

    protected HttpURLConnection sendGetRequest()
            throws IOException {
        if (paramCollection.size() > 0) {
            try {
                urlInstance = new URL(urlInstance.toString() + "?" + convertMapToKV(paramCollection));
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (UnsupportedEncodingException uex) {
                uex.printStackTrace();
            }
        }
        try {
            httpConn = (HttpURLConnection) urlInstance.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(Constant.REQUEST_TIME_OUT);
            httpConn.setReadTimeout(Constant.REQUEST_TIME_OUT);
            httpConn.setUseCaches(false);
            httpConn.setDoInput(true); // true if we want to read server's response
            httpConn.setDoOutput(false); // false indicates this is a GET request
            httpConn.setRequestProperty("Expect", "100-continue");
        } catch (SocketTimeoutException ste) {
            ste.printStackTrace();
            Constant.isBreak = true;
        }

        return httpConn;
    }

    private String convertMapToKV(Map<String, String> kvMap) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : kvMap.entrySet()) {
            if (entry.getValue() == null || entry.getKey() == null) {
                continue;
            }
            if (first)
                first = false;
            else
                result.append("&");
            result.append(entry.getKey());
            //result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(entry.getValue());
            //result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private String convertMapToKVFBLogin(Map<String, String> kvMap) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : kvMap.entrySet()) {
            if (entry.getValue() == null || entry.getKey() == null) {
                continue;
            }
            if (first)
                first = false;
            else
                //result.append("&");
                result.append(entry.getKey());
            //result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(entry.getValue());
            //result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    protected HttpURLConnection sendPostRequest() throws IOException {
        httpConn = (HttpURLConnection) urlInstance.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod("POST");
        httpConn.setDoInput(true); // true indicates the server returns response
        // httpConn.setRequestProperty("Content-Type", "application/json");
        // httpConn.setRequestProperty("Accept","application/json");
        httpConn.setDoOutput(true); // true indicates POST request
        if (stringValue != null) {
            OutputStreamWriter writer = new OutputStreamWriter(
                    httpConn.getOutputStream());
            writer.write(stringValue);
            writer.flush();

        } else {
            if (paramCollection != null && paramCollection.size() > 0) {
                // sends POST data
                OutputStreamWriter writer = new OutputStreamWriter(
                        httpConn.getOutputStream());
                // writer.write(convertMapToKV(paramCollection));
                if (stringValue != null) {
                    writer.write(stringValue);
                } else {
                    writer.write(convertMapToKV(paramCollection));
                }
                writer.flush();
            }
        }
        return httpConn;
    }

    /*protected HttpURLConnection sendPostRequest() throws IOException {
        try{
            httpConn = (HttpURLConnection) urlInstance.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true); // true indicates the server returns response
            httpConn.setConnectTimeout(Constant.REQUEST_TIME_OUT);
            httpConn.setReadTimeout(Constant.REQUEST_TIME_OUT);
            if (paramCollection != null && paramCollection.size() > 0) {
                httpConn.setDoOutput(true); // true indicates POST request
                String paramsToString= null;
            try {
                paramsToString=GlobalUtil.convertMapToJSON(paramCollection).toString();
            }catch(JSONException jex){
                //some exception exists in param collection
            }
                // sends POST data
                if(paramCollection.size()>0) {
                    OutputStreamWriter writer = new OutputStreamWriter(
                            httpConn.getOutputStream());
                    writer.write(convertMapToKV(paramCollection));
                    writer.flush();
                }
            }
        }catch (SocketTimeoutException ste){
            ste.printStackTrace();
            Constant.isBreak = true;
        }

        return httpConn;
    }*/

    protected void closeConnection() {
        if (httpConn != null) {
            httpConn.disconnect();
        }
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    protected String getResponse() throws IOException {
        StringBuilder accumulatedResponse = new StringBuilder();
        httpConn.getErrorStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String decodedString;


        while ((decodedString = in.readLine()) != null) {
            accumulatedResponse.append(decodedString);
        }
        Log.e("API_URL", testUrl);
        Log.e("Request_key_value", paramCollection != null ? convertMapToKV(paramCollection) : "");
        Log.e("Request_JSON_for", stringValue != null ? stringValue : "");
        Log.e("API_Response", accumulatedResponse.toString());
        in.close();

        return (lastResponse = accumulatedResponse.toString());
    }

    protected String convertParamsToString(Map<String, String> params) throws IOException {
        StringBuilder sbuffer = new StringBuilder();
        Iterator<String> paramIterator = params.keySet().iterator();
        while (paramIterator.hasNext()) {
            String key = paramIterator.next();
            String value = params.get(key);
            sbuffer.append(URLEncoder.encode(key, "UTF-8"));
            sbuffer.append("=").append(URLEncoder.encode(value, "UTF-8"));
            sbuffer.append("&");
        }
        return sbuffer.toString();
    }

    @Override
    public void run() {
        try {
            //  sendGetRequest();
            // sendPostRequest();
            sendGetRequest();
            getResponse();


            serviceCallback.serviceEnd(getLastResponse(), "");
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), "");
        } finally {
        }
    }

    public enum RequestType {
        GET_REQUEST(1000, "GET"),
        POST_REQUEST(1001, "POST"),
        PUT_REQUEST(1002, "PUT"),
        DELETE_REQUEST(1003, "DELETE");
        private int statusCode;
        private String statusMessage;

        RequestType(int statusCode, String statusMessage) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
        }

        public String getStatusMessage() {
            return this.statusMessage;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}