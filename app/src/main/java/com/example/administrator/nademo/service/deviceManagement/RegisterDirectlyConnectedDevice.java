package com.example.administrator.nademo.service.deviceManagement;

import com.example.administrator.nademo.utils.Constant;
import com.example.administrator.nademo.utils.HttpsUtil;
import com.example.administrator.nademo.utils.JsonUtil;
import com.example.administrator.nademo.utils.StreamClosedHttpResponse;

import java.util.HashMap;
import java.util.Map;
/**
 * Register Directly Connected Device :
 * This interface is used by NAs to register devices on the IoT platform.
 * After the registration is successful,
 * the IoT platform allocates a device ID for the device,which is used as the unique identifier of the device.
 * Unregistered devices are not allowed to access the IoT platform.
 * In NB-IoT scenarios, the Set device info interface needs to be invoked to set device information after the registration is successful.
 */
public class RegisterDirectlyConnectedDevice {

	public static void main(String args[]) throws Exception {

        // Two-Way Authentication
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        // Authentication，get token
        String accessToken = login(httpsUtil);

        //Please make sure that the following parameter values have been modified in the Constant file.
		String appId = Constant.APPID;
		String urlReg = Constant.REGISTER_DEVICE;

        //please replace the verifyCode and nodeId and timeout, when you use the demo.
        String verifyCode = "999";
		String nodeId = verifyCode;
        Integer timeout = 0;

        Map<String, Object> paramReg = new HashMap<>();
        paramReg.put("verifyCode", verifyCode.toUpperCase());
        paramReg.put("nodeId", nodeId.toUpperCase());
        paramReg.put("timeout", timeout);

        String jsonRequest = JsonUtil.jsonObj2Sting(paramReg);

        Map<String, String> header = new HashMap<>();
        header.put(Constant.HEADER_APP_KEY, appId);
        header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);

        StreamClosedHttpResponse responseReg = httpsUtil.doPostJsonGetStatusLine(urlReg, header, jsonRequest);

        System.out.println("RegisterDirectlyConnectedDevice, response content:");
        System.out.print(responseReg.getStatusLine());
        System.out.println(responseReg.getContent());
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
