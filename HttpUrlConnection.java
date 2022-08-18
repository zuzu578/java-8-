package egovframework.system.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpUrlConnection {
private static final Logger	logger	= LoggerFactory.getLogger(HttpUrlConnection.class);
	
	/**
	 *
	 * @Method Name : xmlSend
	 * @Method 설명 : 일반적인 xmlSend 기능.  결과값 인코딩 설정 추가
	 *
	 * @param paramMap
	 * @param url
	 * @param parseCharsetName
	 * @return
	 */
	public static String xmlSend(HashMap paramMap, String url, String parseCharsetName) {
		String baseUrl = url;
		String parameters = "";
		String result = "";

		Set<String> keySet = paramMap.keySet();
		Iterator<String> keys = keySet.iterator();

		while (keys.hasNext()) {
			String paramKey = keys.next();
			String paramValue = (String)paramMap.get(paramKey);

			if (parameters.equals("")) {
				parameters += paramKey + "=" + paramValue;
			} else {
				parameters += "&" + paramKey + "=" + paramValue;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("url[%s]", baseUrl));
			logger.debug(String.format("params[%s]", parameters));
		}

		OutputStream out = null;
		BufferedReader reader = null;
		StringBuffer buf 	  = new StringBuffer();

		try {
			URL targetURL = new URL(baseUrl);
			URLConnection urlConn = targetURL.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) urlConn;

			httpConnection.setConnectTimeout(30000);		//서버통신 timeout 설정.
			httpConnection.setReadTimeout(30000);			//스트림읽기 timeout 설정.
			httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setUseCaches(false);
			httpConnection.setDefaultUseCaches(false);

			out = httpConnection.getOutputStream();
			out.write(parameters.getBytes("utf-8"));
			out.flush();
			out.close();

			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), parseCharsetName));
//			result = reader.readLine();

			int c;

			while ((c = reader.read()) != -1) {
				buf.append((char)c);
			}

			result = buf.toString();
			reader.close();

			//logger.debug("result = " + result);

		} catch (IOException e) {
			System.out.println("xmlSend fail");
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("xmlSend fail");
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("xmlSend fail");
				}
			}
		}

		return result;
	}
	
	public static String requestHttpGetJson(String url, String authkey) {

		String baseUrl = url;
		String result = "";

		OutputStream out = null;
		BufferedReader reader = null;
		StringBuffer buf = new StringBuffer();
		HttpURLConnection httpConnection = null;
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("url[%s]", baseUrl));
		}

		try {
			URL targetURL = new URL(baseUrl);
			URLConnection urlConn = targetURL.openConnection();
			httpConnection = (HttpURLConnection) urlConn;

			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.setRequestProperty("Content-Type", "application/json");
			
			// 일부 API 사용 시 인증 필요없음.
			if (StringUtil.hasText(authkey)) {
				httpConnection.setRequestProperty("Authorization", "Basic " + authkey);
			}
			
			httpConnection.setRequestMethod("GET");

			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "utf-8"));

			int c;
			while ((c = reader.read()) != -1) {
				buf.append((char)c);
			}

			result = buf.toString();
			reader.close();

		} catch (IOException e) {
			//e.printStackTrace();
			try {
				if ( httpConnection.getErrorStream() != null ) {
					BufferedReader error = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream(), "utf-8"));
					int c;
					while ((c = error.read()) != -1) {
						buf.append((char)c);
					}
					error.close();
					
					logger.info("### requestHttpGetJson Exception : " + buf.toString());
				}
				
			} catch (IOException ee) {
				System.out.println("requestHttpGetJson fail");
			}
			
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("requestHttpGetJson fail");
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("requestHttpGetJson fail");
				}
			}
		}

		return result;
	}

	public String requestHttpPostJson(String url, ParamMap headerparams, JSONObject jsonparams) throws Exception{
		String logPrefix = "REQ_HTTP_POST_JSON";
		logger.info("{} into method.", logPrefix);
		logger.debug("{} url = '{}'", logPrefix, url);
		logger.debug("{} headerparams = '{}'", logPrefix, headerparams);
		logger.debug("{} jsonparams = '{}'", logPrefix, jsonparams);
		
		String baseUrl = url;
		String result = "";
		
		OutputStream out = null;
		BufferedReader reader = null;
		StringBuffer buf = new StringBuffer();
		HttpURLConnection httpConnection = null;
		
		
		try {
			URL targetURL = new URL(baseUrl);
			ignoreSsl();
			URLConnection urlConn = targetURL.openConnection();
			httpConnection = (HttpURLConnection) urlConn;
			
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.setRequestProperty("Content-Type", "application/json");
			
			if (StringUtil.hasText(headerparams.getStr("authkey"))) {
				httpConnection.setRequestProperty("Authorization", headerparams.getStr("authkey"));
			}
			if (StringUtil.hasText(headerparams.getStr("clientId"))) {
				httpConnection.setRequestProperty("X-IB-Client-Id", headerparams.getStr("clientId"));
			}
			if (StringUtil.hasText(headerparams.getStr("clientPw"))) {
				httpConnection.setRequestProperty("X-IB-Client-Passwd", headerparams.getStr("clientPw"));
			}
			
			httpConnection.setRequestMethod("POST");
			
			out = httpConnection.getOutputStream();
			out.write(jsonparams.toJSONString().getBytes("utf-8"));
			out.flush();
			
			InputStream inputStream = httpConnection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			reader = new BufferedReader(inputStreamReader);
			
			int c;
			while ((c = reader.read()) != -1) {
				buf.append((char)c);
			}
			
			result = buf.toString();
			reader.close();
		} catch (MalformedURLException e) {
			System.out.println("requestHttpPostJson fail");
		} catch (ProtocolException e) {
			System.out.println("requestHttpPostJson fail");
		} catch (UnsupportedEncodingException e) {
			System.out.println("requestHttpPostJson fail");
		} catch (IOException e) {
			logger.error("{} Exception Handled. message = {}", logPrefix, e.getMessage());
			
			try {
				BufferedReader error = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream(), "utf-8"));
				int c;
				while ((c = error.read()) != -1) {
					buf.append((char)c);
				}
				error.close();
				
				logger.info("### requestHttpPostJson Exception : " + buf.toString());
				
			} catch (IOException ee) {
				System.out.println("requestHttpPostJson fail");
			}
		}finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("requestHttpPostJson fail");
				}
			}
			
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("requestHttpPostJson fail");
				}
			}
		}
		
		return result;
	}

	public String requestHttpPutJson(String url, ParamMap headerparams, JSONObject jsonparams) {

		String baseUrl = url;
		String result = "";

		OutputStream out = null;
		BufferedReader reader = null;
		StringBuffer buf = new StringBuffer();
		HttpURLConnection httpConnection = null;

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("url[%s]", baseUrl));
			logger.debug(String.format("header[%s]", headerparams));
			logger.debug(String.format("params[%s]", jsonparams.toJSONString()));
		}

		
		try {
			URL targetURL = new URL(baseUrl);
			URLConnection urlConn = targetURL.openConnection();
			httpConnection = (HttpURLConnection) urlConn;

			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.setRequestProperty("Content-Type", "application/json");
			httpConnection.setRequestProperty("Authorization", headerparams.getStr("authkey"));
			httpConnection.setRequestMethod("PUT");

			out = httpConnection.getOutputStream();
			out.write(jsonparams.toJSONString().getBytes("utf-8"));
			out.flush();

			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "utf-8"));

			int c;
			while ((c = reader.read()) != -1) {
				buf.append((char)c);
			}

			result = buf.toString();
			reader.close();
		} catch (MalformedURLException e) {
			System.out.println("requestHttpPutJson fail");
		} catch (ProtocolException e) {
			System.out.println("requestHttpPutJson fail");
		} catch (UnsupportedEncodingException e) {
			System.out.println("requestHttpPutJson fail");
		} catch (IOException e) {
			try {
				BufferedReader error = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream(), "utf-8"));
				int c;
				if(error != null) {
					while ((c = error.read()) != -1) {
						buf.append((char)c);
					}
					error.close();
				}
				logger.info("### requestHttpPutJson Exception : " + buf.toString());
				
			} catch (IOException ee) {
				System.out.println("requestHttpPutJson fail");
			}
		}finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.out.println("requestHttpPutJson fail");
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("requestHttpPutJson fail");
				}
			}
		}

		return result;
	}	
		
   public void ignoreSsl() throws Exception{
       HostnameVerifier hv = new HostnameVerifier() {
       public boolean verify(String urlHostName, SSLSession session) {
               return true;
           }
       };
       trustAllHttpsCertificates();
       HttpsURLConnection.setDefaultHostnameVerifier(hv);
   }

	
   private void trustAllHttpsCertificates() throws Exception {
       TrustManager[] trustAllCerts = new TrustManager[1];
       TrustManager tm = new miTM();
       trustAllCerts[0] = tm;
       SSLContext sc = SSLContext.getInstance("SSL");
       sc.init(null, trustAllCerts, null);
       HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
   }

	
	class miTM implements TrustManager,X509TrustManager {
       public X509Certificate[] getAcceptedIssuers() {
           return null;
       }

       public boolean isServerTrusted(X509Certificate[] certs) {
           return true;
       }

       public boolean isClientTrusted(X509Certificate[] certs) {
           return true;
       }

       public void checkServerTrusted(X509Certificate[] certs, String authType)
               throws CertificateException {
           return;
       }

       public void checkClientTrusted(X509Certificate[] certs, String authType)
               throws CertificateException {
           return;
       }
   }
}
