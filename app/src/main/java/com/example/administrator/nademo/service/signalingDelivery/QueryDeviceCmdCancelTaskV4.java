/*
 * File Name: com.huawei.service.signalingDelivery.CreateDeviceCmdCancelTaskV4.java
 *
 * Copyright Notice:
 *      Copyright  1998-2008, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.example.administrator.nademo.service.signalingDelivery;

import com.example.administrator.nademo.utils.Constant;
import com.example.administrator.nademo.utils.HttpsUtil;
import com.example.administrator.nademo.utils.JsonUtil;
import com.example.administrator.nademo.utils.StreamClosedHttpResponse;

import java.util.HashMap;
import java.util.Map;
/**
 * This interface is used by NAs to query batch task,
 * which is used to Cancel all device commands under the specified device ID.
 */
public class QueryDeviceCmdCancelTaskV4 {

    public static void main(String args[]) throws Exception {

        // Two-Way Authentication
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        // Authentication，get token
        String accessToken = login(httpsUtil);

        //Please make sure that the following parameter values have been modified in the Constant file.
        String appId = Constant.APPID;
        String urlQueryDeviceCMD = Constant.QUERY_DEVICECMD_CANCEL_TASK;
        
        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_APP_KEY, appId);
        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
        
        StreamClosedHttpResponse responseQueryDeviceCMD = httpsUtil.doGetWithParasGetStatusLine(urlQueryDeviceCMD, null, header);
        
        System.out.println("QueryAsynCommands, response content:");
        System.out.print(responseQueryDeviceCMD.getStatusLine());
        System.out.println(responseQueryDeviceCMD.getContent());
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

