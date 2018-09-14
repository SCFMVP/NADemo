package com.example.administrator.nademo.utils;


import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

@SuppressWarnings("deprecation")
public class HttpsUtil extends DefaultHttpClient {
	public final static String HTTPGET = "GET";

	public final static String HTTPPUT = "PUT";

	public final static String HTTPPOST = "POST";

	public final static String HTTPDELETE = "DELETE";

	public final static String HTTPACCEPT = "Accept";

	public final static String CONTENT_LENGTH = "Content-Length";

	public final static String CHARSET_UTF8 = "UTF-8";

	private static HttpClient httpClient;
//TODO : url替换
	/**
	 * Two-Way Authentication In the two-way authentication, the client needs: 1
	 * Import your own certificate for server verification; 2 Import the CA
	 * certificate of the server, and use the CA certificate to verify the
	 * certificate sent by the server; 3 Set the domain name to not verify
	 * (Non-commercial IoT platform, no use domain name access.)
	 * */
	public void initSSLConfigForTwoWay() throws Exception {
		// 1 Import your own certificate
		String demo_base_Path = System.getProperty("user.dir");
		String selfcertpath = Constant.SELFCERTPATH; //demo_base_Path + Constant.SELFCERTPATH;   //   69-73
		String trustcapath = Constant.TRUSTCAPATH; // demo_base_Path + Constant.TRUSTCAPATH;     //

		KeyStore selfCert = KeyStore.getInstance("pkcs12");
		String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath();  //把文件放在SD根目录
		//File file = new File(sdCardDir + "/a.txt");
		selfCert.load(new FileInputStream(sdCardDir+"/outgoing.CertwithKey.pkcs12"),//-------wenjian
				Constant.SELFCERTPWD.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");    //sunx509
		kmf.init(selfCert, Constant.SELFCERTPWD.toCharArray());

		// 2 Import the CA certificate of the server,
		KeyStore caCert = KeyStore.getInstance("bks");   //jks
		caCert.load(new FileInputStream(sdCardDir+"/ca.bks"), Constant.TRUSTCAPWD.toCharArray());//----------wenjian
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(caCert);

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		// 3 Set the domain name to not verify
		// (Non-commercial IoT platform, no use domain name access generally.)
		//SSLSocketFactory ssf = new SSLSocketFactory(selfCert,Constant.SELFCERTPWD, caCert);
		SSLSocketFactory ssl = new SSLSocketFactory(selfCert, Constant.SELFCERTPWD, caCert);
		ssl.setHostnameVerifier(new AllowAllHostnameVerifier());
		//SSLConnectionSocketFactory scsf=new SSLConnectionSocketFactory(sc, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		// If the platform has already applied for a domain name which matches
		// the domain name in the certificate information, the certificate
		// domain name check can be enabled (open by default)
		//SSLSocketFactory ssf = new SSLSocketFactory(sc);

		//ClientConnectionManager ccm = this.getConnectionManager();
		//SchemeRegistry sr = ccm.getSchemeRegistry();
		//sr.register(new  Scheme("https", ssl, 8743));//有修改

		httpClient = new DefaultHttpClient();
		if (ssl != null) {
			Scheme sch = new Scheme("https", ssl, 8743);
			httpClient.getConnectionManager().getSchemeRegistry().register(sch);
		}
	}

	/**
	 * One-Way Authentication In the One-way authentication, the client needs: 1
	 * Import the CA certificate of the server, and use the CA certificate to
	 * verify the certificate sent by the server; 2 Set the domain name to not
	 * verify (Non-commercial IoT platform, no use domain name access.)
	 * */
	/*
	 * public void initSSLConfigForOneWay() throws Exception {
	 * 
	 * // 1 Import the CA certificate of the server, KeyStore caCert =
	 * KeyStore.getInstance("jks"); caCert.load(new
	 * FileInputStream(TRUSTCAPATH), TRUSTCAPWD.toCharArray());
	 * TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
	 * tmf.init(caCert);
	 * 
	 * SSLContext sc = SSLContext.getInstance("TLS"); sc.init(null,
	 * tmf.getTrustManagers(), null);
	 * 
	 * // 2 Set the domain name to not verify // (Non-commercial IoT platform,
	 * no use domain name access generally.) SSLSocketFactory ssf = new
	 * SSLSocketFactory(sc, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	 * 
	 * //If the platform has already applied for a domain name which matches the
	 * domain name in the certificate information, the certificate //domain name
	 * check can be enabled (open by default) // SSLSocketFactory ssf = new
	 * SSLSocketFactory(sc);
	 * 
	 * ClientConnectionManager ccm = this.getConnectionManager(); SchemeRegistry
	 * sr = ccm.getSchemeRegistry(); sr.register(new Scheme("https", 8743,
	 * ssf));
	 * 
	 * httpClient = new DefaultHttpClient(ccm); }
	 */

	public HttpResponse doPostJson(String url, Map<String, String> headerMap,
			String content) {
		HttpPost request = new HttpPost(url);
		addRequestHeader(request, headerMap);

		request.setEntity(new StringEntity(content,
				ContentType.APPLICATION_JSON));

		return executeHttpRequest(request);
	}
	
	   public StreamClosedHttpResponse doPostMultipartFile(String url, Map<String, String> headerMap,
	           File file) {
	        HttpPost request = new HttpPost(url);
	        addRequestHeader(request, headerMap);
	        
	        FileBody fileBody = new FileBody(file);
	        // Content-Type:multipart/form-data; boundary=----WebKitFormBoundarypJTQXMOZ3dLEzJ4b
	        HttpEntity reqEntity = (HttpEntity) MultipartEntityBuilder.create().addPart("file", fileBody).build();
	        request.setEntity(reqEntity);
	        
	        return (StreamClosedHttpResponse) executeHttpRequest(request);
	    }

	public StreamClosedHttpResponse doPostJsonGetStatusLine(
			String url, Map<String, String> headerMap, String content) {
		HttpPost request = new HttpPost(url);
		addRequestHeader(request, headerMap);

		request.setEntity(new StringEntity(content,
				ContentType.APPLICATION_JSON));

		HttpResponse response = executeHttpRequest(request);
		if (null == response) {
			System.out.println("The response body is null.");
		}

		return (StreamClosedHttpResponse) response;
	}

	public StreamClosedHttpResponse doPostJsonGetStatusLine(String url, String content) {
		HttpPost request = new HttpPost(url);

		request.setEntity(new StringEntity(content,
				ContentType.APPLICATION_JSON));

		HttpResponse response = executeHttpRequest(request);
		if (null == response) {
			System.out.println("The response body is null.");
		}

		return (StreamClosedHttpResponse) response;
	}
	
	private List<NameValuePair> paramsConverter(Map<String, String> params) {
		List<NameValuePair> nvps = new LinkedList<NameValuePair>();
		Set<Map.Entry<String, String>> paramsSet = params.entrySet();
		for (Map.Entry<String, String> paramEntry : paramsSet) {
			nvps.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry
					.getValue()));
		}

		return nvps;
	}


	public StreamClosedHttpResponse doPostFormUrlEncodedGetStatusLine(
			String url, Map<String, String> formParams) throws Exception {
		Log.d("111", "进入doPostFormUrlEncodedGetStatusLine方法");
		HttpPost request = new HttpPost(url);
		Log.d("111","request新建结束->开始setEntity");
		request.setEntity(new UrlEncodedFormEntity(paramsConverter(formParams)));
		Log.d("111","request.setEntity成功->开始新建response");  //----出现在这截止
		HttpResponse response = executeHttpRequest(request);
		Log.d("111", "response成功");
		if (null == response) {
			System.out.println("The response body is null.");
			Log.d("111", "返回体为空!");
			throw new Exception();
		}
		Log.d("111", "返回体bu为空!");

		return (StreamClosedHttpResponse) response;
	}

	public HttpResponse doPutJson(String url, Map<String, String> headerMap,
			String content) {
		HttpPut request = new HttpPut(url);
		addRequestHeader(request, headerMap);

		request.setEntity(new StringEntity(content,
				ContentType.APPLICATION_JSON));

		return executeHttpRequest(request);
	}
    
	public HttpResponse doPut(String url, Map<String, String> headerMap) {
		HttpPut request = new HttpPut(url);
		addRequestHeader(request, headerMap);
		
		return executeHttpRequest(request);
	}
	
	public StreamClosedHttpResponse doPutJsonGetStatusLine(String url, Map<String, String> headerMap,
			String content) {
		HttpResponse response = doPutJson(url, headerMap, content);
		if (null == response) {
			System.out.println("The response body is null.");
		}

		return (StreamClosedHttpResponse) response;
	}
	
	public StreamClosedHttpResponse doPutGetStatusLine(String url, Map<String, String> headerMap) {
		HttpResponse response = doPut(url, headerMap);
		if (null == response) {
			System.out.println("The response body is null.");
		}

		return (StreamClosedHttpResponse) response;
	}

	public HttpResponse doGetWithParas(String url,
			Map<String, String> queryParams, Map<String, String> headerMap)
			throws Exception {
		Log.d("111","进入doGetWithParas方法->new request");
		HttpGet request = new HttpGet(url);
		Log.d("111","request新建结束->addRequestHeader方法");
		addRequestHeader(request, headerMap);
		Log.d("111","addRequestHeader结束->URIBuilder");
		URIBuilder builder;
		try {
			builder = new URIBuilder(url);
		} catch (URISyntaxException e) {
			System.out.printf("URISyntaxException: {}", e);
			throw new Exception(e);
			//Log.d("111","q"+e);
		}

		if (queryParams != null && !queryParams.isEmpty()) {
			builder.setParameters(paramsConverter(queryParams));
		}
		Log.d("111", "开始setURI");
		//request.;
		//request.setURI(builder.build());//------------error
		Log.d("111", "setURI结束->return开始");
		return executeHttpRequest(request);
	}

	public StreamClosedHttpResponse doGetWithParasGetStatusLine(String url,
			Map<String, String> queryParams, Map<String, String> headerMap)
			throws Exception {
		Log.d("111","进入doGetWithParasGetStatusLine方法->新建response");
		HttpResponse response=null;
		try {
			response = doGetWithParas(url, queryParams, headerMap);
		}catch(Exception e){Log.d("111",e.toString());}

		Log.d("111","新建response结束->开始return");
		if (null == response) {
			System.out.println("The response body is null.");
			Log.d("111","返回体为空!");
		}
		Log.d("111","返回体bu为空!");
		return (StreamClosedHttpResponse) response;
	}

	public HttpResponse doDelete(String url, Map<String, String> headerMap) {
		HttpDelete request = new HttpDelete(url);
		addRequestHeader(request, headerMap);

		return executeHttpRequest(request);
	}

	public StreamClosedHttpResponse doDeleteGetStatusLine(String url,
			Map<String, String> headerMap) {
		HttpResponse response = doDelete(url, headerMap);
		if (null == response) {
			System.out.println("The response body is null.");
		}

		return (StreamClosedHttpResponse) response;
	}

	private static void addRequestHeader(HttpUriRequest request,
			Map<String, String> headerMap) {
		if (headerMap == null) {
			return;
		}

		for (String headerName : headerMap.keySet()) {
			if (CONTENT_LENGTH.equalsIgnoreCase(headerName)) {
				continue;
			}

			String headerValue = headerMap.get(headerName);
			request.addHeader(headerName, headerValue);
		}
	}

	private HttpResponse executeHttpRequest(HttpUriRequest request) {
		HttpResponse response = null;
		Log.d("111","进入executeHttpRequest方法");
		try {
			response = httpClient.execute(request);
		} catch (Exception e) {
			Log.d("111","executeHttpRequest failed."+e.toString());
			System.out.println("executeHttpRequest failed.");
		} finally {
			try {
				response = new StreamClosedHttpResponse(response);
			} catch (IOException e) {
				Log.d("111","IOException: " + e.getMessage());
				System.out.println("IOException: " + e.getMessage());
			}
		}

		return response;
	}

	public String getHttpResponseBody(HttpResponse response)
			throws UnsupportedOperationException, IOException {
		if (response == null) {
			return null;
		}

		String body = null;

		if (response instanceof StreamClosedHttpResponse) {
			body = ((StreamClosedHttpResponse) response).getContent();
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null && entity.isStreaming()) {
				String encoding = entity.getContentEncoding() != null ? entity
						.getContentEncoding().getValue() : null;
				body = StreamUtil.inputStream2String(entity.getContent(),
						encoding);
			}
		}

		return body;
	}
}
