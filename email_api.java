public EmailVO sendMail(org.json.JSONObject jsonObject , int status) {
		URL url = null;
		EmailVO vo = new EmailVO();
		HttpURLConnection conn = null;
		HashMap<String , Object> returnMap = new HashMap<String,Object>();
		String responseData = "";	    	   
		BufferedReader br = null;
		StringBuffer sb = null;	   
	    String returnData = "";

			try {
				
				url = new URL(api_url);	
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; utf-8");
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoOutput(true);
				try (OutputStream os = conn.getOutputStream()){
					byte request_data[] = ((JSONAware) jsonObject).toJSONString().getBytes("utf-8");
					os.write(request_data);
					os.close();
					returnMap.put("transferTime", LocalDate.now());
				}
				catch(Exception e) {
					e.printStackTrace();
				}		
				conn.connect();
				System.out.println("http 요청 방식 : "+"POST BODY JSON");
				System.out.println("http 요청 타입 : "+"application/json");
		
				br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));	
				sb = new StringBuffer();	       
				while ((responseData = br.readLine()) != null) {
					sb.append(responseData); 
				}
				returnData = sb.toString(); 
				String responseCode = String.valueOf(conn.getResponseCode());
				System.out.println("http 응답 코드 : "+responseCode);
				System.out.println("http 응답 데이터 : "+returnData);
				vo.setApprovalStatus(0);
				vo.setResultJson(returnData);
				vo.setRecieveStatus("N");	
				vo.setSendStatus("N");
				vo.setResultStatus("N");
				vo.setPostGubun("A");
				
				return vo;
			}catch(Exception Error) {
				Error.printStackTrace();
				vo.setApprovalStatus(0);
				vo.setResultJson(returnData);
				vo.setRecieveStatus("N");	
				vo.setSendStatus("N");
				vo.setResultStatus("N");
				vo.setPostGubun("A");
				
				return vo;
			}
		
		
		
	}
