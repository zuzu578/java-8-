package egovframework.api.admin.utils;

import java.lang.reflect.Field;
import java.util.*;

public class ConvertUtils {
    public ConvertUtils() {}

    public static Map<String, Object> convertToMap(Object obj) {
        try {
            if (Objects.isNull(obj)) {
                return Collections.emptyMap();
            }
            Map<String, Object> convertMap = new HashMap<>();

            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                convertMap.put(field.getName(), field.get(obj));
            }
            return convertMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertToValueObject(Map<String, Object> map, Class<T> type) {
        try {
            Objects.requireNonNull(type, "Class cannot be null");
            T instance = type.getConstructor().newInstance();

            if (map == null || map.isEmpty()) {
                return instance;
            }

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Field[] fields = type.getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);
                    String name = field.getName();
                      // parameter 로 넘어온 문자열이 숫자인지 판별 
                    boolean isNumeric =  ((String) entry.getValue()).matches("[+-]?\\d*(\\.\\d+)?");
                    Object parseIntValue = null;
                    if(isNumeric) {
                    	parseIntValue = (int)Integer.parseInt((String) entry.getValue());
                       }
                    // integer 타입과 int 타입 불일치를 방지 
                    Class<? extends Object> entryType = entry.getValue().getClass();
                    Class<? extends Object> fieldType = field.getType(); // 실제 field 는 int type 
                     
                      // parameter 값이 숫자일때 
                    if(!fieldType.equals(entryType) &&!fieldType.equals(java.lang.String.class) && isNumeric) {
                    		entryType = int.class;
                       }
                   
                    
                    boolean isSameType = entryType.equals(fieldType);
                    boolean isSameName = entry.getKey().equals(name);

                      
                    if (isSameType && isSameName) {
                    	// 숫자일 경우 int 로 casting 한 값을 field 에 setting 해준다.
                    	if(isNumeric) {
                    		field.set(instance, parseIntValue);                    				
                    	}else {
                    		field.set(instance, map.get(name));
                    	}
                    	
                        break;
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> convertToMaps(List<?> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> convertList = new ArrayList<>(list.size());

        for (Object obj : list) {
            convertList.add(ConvertUtils.convertToMap(obj));
        }
        return convertList;
    }

    public static <T> List<T> convertToValueObjects(List<HashMap<String, Object>> list, Class<T> type) {
        Objects.requireNonNull(type, "Class cannot be null");

        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> convertList = new ArrayList<>(list.size());

        for (Map<String, Object> map : list) {
            convertList.add(ConvertUtils.convertToValueObject(map, type));
        }
        return convertList;
    }
}
