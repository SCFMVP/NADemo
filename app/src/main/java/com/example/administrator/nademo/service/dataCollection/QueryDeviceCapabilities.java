package com.example.administrator.nademo.service.dataCollection;

import com.example.administrator.nademo.utils.Constant;
import com.example.administrator.nademo.utils.HttpsUtil;
import com.example.administrator.nademo.utils.JsonUtil;
import com.example.administrator.nademo.utils.StreamClosedHttpResponse;

import java.util.HashMap;
import java.util.Map;
/**
 * Query Device Capability :
 * This interface is used to query the service capability of a device.
 */
public class QueryDeviceCapabilities {

	public static void main(String args[]) throws Exception {

        // Two-Way Authentication
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        // Authentication，get token
        String accessToken = login(httpsUtil);

        //Please make sure that the following parameter values have been modified in the Constant file.
        String appId = Constant.APPID;
        String urlQueryDeviceCapabilities = Constant.QUERY_DEVICE_CAPABILITIES;

        //please replace the deviceId and gatewayId, when you use the demo.
        String deviceId = "9a445dda-f62e-4c78-be05-ef0f0c1b447a";
        String gatewayId = "9a445dda-f62e-4c78-be05-ef0f0c1b447a";

        Map<String, String> paramQueryDeviceCapabilities = new HashMap<>();
        paramQueryDeviceCapabilities.put("deviceId", deviceId);
        paramQueryDeviceCapabilities.put("gatewayId", gatewayId);

        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_APP_KEY, appId);
        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
        
        StreamClosedHttpResponse bodyQueryDeviceCapabilities = httpsUtil.doGetWithParasGetStatusLine(
                urlQueryDeviceCapabilities, paramQueryDeviceCapabilities, header);

        System.out.println("QueryDeviceCapabilities, response content:");
        System.out.print(bodyQueryDeviceCapabilities.getStatusLine());
        System.out.println(bodyQueryDeviceCapabilities.getContent());
        System.out.println();
    }

    /**
     * Authentication，get token
     * */
    @SuppressWarnings("unchecked")
    public static String login(HttpsUtil httpsUtil) throws Exception {

        String appId = Constant.APPID;
        String secret = Constant.SECRET;
        String urlLogin = Constant.APP_AUTH;

        Map<String, String> paramLogin = new HashMap<>();
        paramLogin.put("appId", appId);
        paramLogin.put("secret", secret);

        StreamClosedHttpResponse responseLogin = httpsUtil.doPostFormUrlEncodedGetStatusLine(urlLogin, paramLogin);

        System.out.println("app auth success,return accessToken:");
        System.out.print(responseLogin.getStatusLine());
        System.out.println(responseLogin.getContent());
        System.out.println();

        Map<String, String> data = new HashMap<>();
        data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
        return data.get("accessToken");
    }

}
