
public class JsonUtils {

	/**
	 * hashMap 에있는 숫자가 포함된 문자열 value 를 int 로 변형 util
	 * @param paramMap
	 * @return
	 */
	public static Map<String, Object> hashMapStringConvertToInt(Map<String , Object> paramMap){
		HashMap<String , Object> resultMap = new HashMap<String , Object>();	

		if(paramMap.get("decryptedParams") != null && paramMap.get("decryptedParams").getClass() == java.lang.String.class) {
			String s = paramMap.get("data").toString();
			String[] pairs = s.split(",");
			String pair = "";
			for (int i=0;i<pairs.length;i++) {
			    pair = pairs[i];
			    if(pair.contains("{") || pair.contains("}")) {
			    	pair = pair.replace("{","");
			    	pair = pair.replace("}","");
			    }
			    String[] keyValue = pair.split("=");
			     if(StringUtils.isNumeric(keyValue[1].trim())) {
			    	 resultMap.put(keyValue[0].trim(),Integer.parseInt(keyValue[1].trim()));	        		    	 
			     }else {
			    	 resultMap.put(keyValue[0].trim(),keyValue[1].trim());	        		    	 
			     }
			} 	
		}else {
			for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			    String key = entry.getKey();
			    String value = entry.getValue().toString();
			    if(StringUtils.isNumeric(value)) {
			    	resultMap.put(key.trim(), Integer.parseInt(value.trim()));
			    }else {
			    	resultMap.put(key.trim(),value.trim());
			    }
			}
		}
		
		
		return resultMap;
	}
}
