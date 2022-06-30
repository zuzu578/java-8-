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
                   
                    // integer 타입과 int 타입 불일치를 방지 
                    Class<? extends Object> entryType = entry.getValue().getClass(); // integer type 으로 전달됨 
                    Class<? extends Object> fieldType = field.getType(); // 실제 field 는 int type 
                     //target type 이 string 으로 넘어올경우 string 일 경우 해당 조건을 실행시키지 않기위함 
                    if(!fieldType.equals(java.lang.Integer.class) &&!fieldType.equals(java.lang.String.class) ) {
                    		fieldType = java.lang.Integer.class;
                       }
                   
                    boolean isSameType = entryType.equals(fieldType);
                    boolean isSameName = entry.getKey().equals(name);

                    if (isSameType && isSameName) {
                        field.set(instance, map.get(name));
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
