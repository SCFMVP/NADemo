package com.example.administrator.nademo.service.dataCollection;

import android.util.Log;

import com.example.administrator.nademo.utils.Constant;
import com.example.administrator.nademo.utils.HttpsUtil;
import com.example.administrator.nademo.utils.JsonUtil;
import com.example.administrator.nademo.utils.StreamClosedHttpResponse;

import java.util.HashMap;
import java.util.Map;
/**
 * Query Device Data :
 * This interface is used by NAs to query information for specify device.
 */
public class QueryDeviceData {

    public String hello() throws Exception {

        // Two-Way Authentication
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        // Authentication，get token
        Log.d("111","开始获取token");
        String accessToken = login(httpsUtil);
        Log.d("111","获取token结束: "+accessToken);
        //Please make sure that the following parameter values have been modified in the Constant file.
        String appId = Constant.APPID;

        //please replace the deviceId, when you use the demo.//device_01
        String deviceId =Constant.Device_02;
        String urlQueryDeviceData = Constant.QUERY_DEVICE_DATA + "/" + deviceId;
        Log.d("111","开始paramQueryDeviceData");
        Map<String, String> paramQueryDeviceData = new HashMap<>();
        paramQueryDeviceData.put("appId", appId);
        Log.d("111", "paramQueryDeviceData结束->开始header.put");
        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_APP_KEY, appId);
        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
        Log.d("111","header.put结束->开始bodyQueryDeviceData");
        final StreamClosedHttpResponse bodyQueryDeviceData = httpsUtil.doGetWithParasGetStatusLine(urlQueryDeviceData,
                paramQueryDeviceData, header);
        Log.d("111","bodyQueryDeviceData结束->开始return");
        System.out.println("QueryDeviceData, response content:");
        System.out.print(bodyQueryDeviceData.getStatusLine());
        System.out.println(bodyQueryDeviceData.getContent());
        System.out.println();
        return bodyQueryDeviceData.getContent();
    }

    /**
     * Authentication，get token
     * */
    @SuppressWarnings("unchecked")
    public  String login(HttpsUtil httpsUtil) throws Exception {

        String appId = Constant.APPID;
        String secret = Constant.SECRET;
        String urlLogin = Constant.APP_AUTH;

        Map<String, String> paramLogin = new HashMap<>();
        paramLogin.put("appId", appId);
        paramLogin.put("secret", secret);
        Log.d("111", "开始获取responseLogin");
        StreamClosedHttpResponse responseLogin = httpsUtil.doPostFormUrlEncodedGetStatusLine(urlLogin, paramLogin);
        Log.d("111", "获取responseLogin成功:"+responseLogin);
        System.out.println("app auth success,return accessToken:");
        System.out.print(responseLogin.getStatusLine());
        System.out.println(responseLogin.getContent());
        System.out.println();

        Map<String, String> data = new HashMap<>();
        data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
        Log.d("111","return 开始");
        return data.get("accessToken");
    }

}
