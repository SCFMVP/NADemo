package com.example.administrator.nademo.service.signalingDelivery;

import com.example.administrator.nademo.utils.Constant;
import com.example.administrator.nademo.utils.HttpsUtil;
import com.example.administrator.nademo.utils.JsonUtil;
import com.example.administrator.nademo.utils.StreamClosedHttpResponse;

import java.util.HashMap;
import java.util.Map;
/**
 *This interface is used by an NA to modify the command information.
 * Currently only supports modify such device Command that the status is PENDING.
 */
public class UpdateAsynCommandV4 {

	public static void main(String args[]) throws Exception {

        // Two-Way Authentication
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        // Authentication，get token
        String accessToken = login(httpsUtil);

        //Please make sure that the following parameter values have been modified in the Constant file.
		String appId = Constant.APPID;

        // please replace the commandId, when you use the demo.
        String commandId = "cdb68dcfc4614ffe914f5605c55a4828";
        String urlUpdateAsynCommand = Constant.UPDATE_ASYN_COMMAND;
        urlUpdateAsynCommand = String.format(urlUpdateAsynCommand, commandId);

        //Currently only supports Modify the status of device command from PENDING to CANCELED.
        String status = "CANCELED";
        
        Map<String, Object> paraUpdateDeviceCommandReq = new HashMap<>();
        paraUpdateDeviceCommandReq.put("status", status);
        
        String jsonRequest = JsonUtil.jsonObj2Sting(paraUpdateDeviceCommandReq);
                
        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_APP_KEY, appId);
        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);
        
        StreamClosedHttpResponse bodyUpdateAsynCommand = httpsUtil.doPutJsonGetStatusLine(urlUpdateAsynCommand, header, jsonRequest);
        
        System.out.println("UpdateAsynCommand, response content:");
		System.out.print(bodyUpdateAsynCommand.getStatusLine());
		System.out.println(bodyUpdateAsynCommand.getContent());
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
