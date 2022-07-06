if(paramMap.get("data") != null && paramMap.get("data").getClass() == java.lang.String.class) {
  // paramMap.get("data") 가 string 형태의 hashMap으로 넘어올경우 
  // ex) data: {key,value}
	    		try {
	        		String s = paramMap.get("data").toString();
	        		String[] pairs = s.split(","); // 여러개의 쌍으로 올경우 split 한다. 
	        		String pair = "";
	        		for (int i=0;i<pairs.length;i++) {
	        		    pair = pairs[i];
	        		    if(pair.contains("{") || pair.contains("}")) {
	        		    	pair = pair.replace("{","");
	        		    	pair = pair.replace("}","");
	        		    }
	        		    String[] keyValue = pair.split("=");
	        		    System.out.println("key Value ===> " + keyValue[0].trim()  + "     " + keyValue[1].trim());
	        		    //String splitKeyValue [] = keyValue[0].split("=");
	        		    stringToMap.put(keyValue[0].trim(), keyValue[1].trim());
	        		}
	    		}catch(Exception e) {
	    			resultMap.put("message",Message.invalidDataForm);
					resultMap.put("code",MessageCode.invalidDataForm);
					return resultMap;
	    		}
	    		// paramMap.get("data") 가 string 형식 => "{k,v}" 일경우 
	    		convertValueObjects = convertUtils.convertToValueObject(stringToMap,GoodsVO.class);
	    	}else {
	    		convertValueObjects = convertUtils.convertToValueObject(paramMap,GoodsVO.class);
	    	}
