# json Array 를 List<VO> 로 변환 
```java
JSONObject obj = (JSONObject)result.getData();
JSONArray arr = (JSONArray) obj.get("contents");
try {
	List<CategoryVO> cateListObj = gson.fromJson(arr.toString(),  new TypeToken<ArrayList<CategoryVO>>(){}.getType());
	model.addAttribute("cateList", cateListObj);
     }catch(Exception e) {
	log.debug(e.getMessage());
     }
```

# spring security 에서 중복로그인 방지 기능 
```xml
<!--  중복로그인 방지 -->
<!-- <security:session-management invalid-session-url="/login/loginSessionCheck.do" session-fixation-protection="migrateSession">
	 <security:concurrency-control max-sessions="1" expired-url="/login/loginSessionCheck.do" error-if-maximum-exceeded="false" />
</security:session-management>  -->
```

# List<HashMap<String,Object>> 를 List<VO> 로 변환 

```java
List<HashMap<String , Object>> myRows = service.getData();
// list hashMap 을 list Object 로 받는다 
// 그런다음 list Object 를 list VO 로 캐스팅해주면된다. 

// 1) list hashMap => list Object => list VO 
```

# gson list type 추론 

Gson 에서 deserialize 를 하면 이런코드를 사용하게되는데
```java
new TypeToken<List<MyType>>(){}.getType()

```
사실 제네릭은 자바에서는 컴파일 하면 소거되기때문에 원래 타입을 알수없게되어 gson 에서는 TypeToken 을 사용한다.

참조 : https://namocom.tistory.com/671 

# jsonParse
```java
JSONParse parser = new JSONParse();
parser.parse(json문자열 target);
// json문자열에 \ 가붙은 경우가 있는데 이럴경우 parsing 해주면 됨 
```

간혹 objectMapper 를 이용하여 mapper.writeValueAsString() 을 사용할경우 json 문자열에 \ 가 생기는데 이런경우도 이런식으로 하면된다. 

```java
JSONObject data = new JSONObject();
data.put("contents", parser.parse(mapper.writeValueAsString(goodsList)));
data.put("pagination", pagination);

```
# JsonArray ( [{} , {} , {} , {} ] list Object) 형태를  list map 으로 변경하는 법 
```java
JSONObject contentsObj = (JSONObject) parser.parse(result.getData().toString());
Map<String, Object> jsonObject = gson.fromJson(contentsObj.toString(), new TypeToken<Map<String, Object>>(){}.getType());
List<Map<String, Object>> jsonList = (List) jsonObject.get("contents"); // jsonObject 에 contents 라는 key target => contents : [ {} , {} , {} ];


```

# list map iterate 
```java
for (Map<String, Object> map : jsonList) {
	for (Map.Entry<String, Object> entry : map.entrySet()) {
		String key = entry.getKey();
		Object value = entry.getValue();
		model.addAttribute(key,value);
	}
}

```
# getOrDefault()
java 8 에서 추가된 api 
찾는 key 가 존재한다면 , key 의 value 를 반환하고 그렇지 않으면 default 값을 반환한다 (StringUtil.nvl()) 과 비슷 

사용 예
``` java
public class Target {

	public static void main(String[] args) {
		Map<String , Integer> targetMap = new HashMap<String , Integer>();
		String target [] = {"top","top","top","top","kimtop"};
		for(int i = 0 ; i < target.length ; i ++) { 
			targetMap.put(target[i], targetMap.getOrDefault(target[i], 0) + 1);
		}
		System.out.println(targetMap);
	}	
}

```

위 코드는 target 이라는 배열을 순회하면서 , 해당 원소들의 갯수를 count 한다.

처음 targetMap 에는 아무런 값이 없기때문에 default 값인 0 을 반환 +1 이므로 

top = 1  

두번째 targetMap 에는 top 이라는 key 값이 존재하므로 , top에 해당하는 value 1 을 반환 +1 
top = 2 

계속 반복하면  top = 4 , top 은 4가된다. 


# getwriter() has already been called for this response / getWriter() 가 이미 호출되었습니다
참고 : https://stackoverflow.com/questions/34768341/spring-boot-interceptor-return-json (인터셉터에서 object 등을 return 하는방법)

printWriter 는 한번만 쓸수 있는데 여러번 쓰게 되면 나는 오류이다.

다음을 보자

```java
package egovframework.com.cmm.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egovframe.rte.fdl.security.userdetails.util.EgovUserDetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import egovframework.com.bo.util.ReturnMessage;
import egovframework.com.bo.util.StringUtil;
import egovframework.com.cmm.LoginVO;

import egovframework.com.uat.role.service.EgovAuthManageService;
import egovframework.com.uat.role.service.RoleAuthVO;
import egovframework.com.uat.role.service.RoleUrlPatternVO;
import lombok.extern.slf4j.Slf4j;
/**
 * TODO : 로그인한 유저 권한에 따라 메뉴 접근권한 분기 
 * @author dev002
 */
@Slf4j
public class MenuInterceptor extends HandlerInterceptorAdapter{
	
	private EgovAuthManageService egovAuthManageService;
	
	@Autowired
	public MenuInterceptor (EgovAuthManageService egovAuthManageService) {
		this.egovAuthManageService = egovAuthManageService;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean isAccess = false;
		boolean isPassed = false;
		
		PrintWriter writer = null;
		
		try {
			response.setContentType("charset=UTF-8");
			writer = response.getWriter();
			String servletPath = request.getServletPath();
			// 스프링 시큐리티 인가 후 세션에 저장된 유저정보 를 통해 유저 권한을 가져온다.
			SecurityContext context = SecurityContextHolder.getContext();
			Authentication authentication = context.getAuthentication();
			String id = authentication.getName(); // 시큐리티에 잡힘 
			
			LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
			RoleAuthVO auth = egovAuthManageService.getUserAuthority(loginVO);
			String authority = StringUtil.nvl(auth.getAuthority(), "");
	
			validate : {
				if(authority.equals("")) {
					//writer.println("CODE :"+ReturnMessage.NOAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOAUTH.getMessage()+"");			
					isPassed = false;
					break validate;
				}else {
					isPassed = true;
				}
			}
			if(isPassed) {
				// 가져온 해당 유저의 권한을 바탕으로 롤 접근 url 을 가져온다.
				List<RoleUrlPatternVO> roleAuthUrlList = (List<RoleUrlPatternVO>)(Object)egovAuthManageService.getRoleAuthUrlListByauthority(authority);	
				// 접근 권한을 판별한다.	
				List<Object> result = roleAuthUrlList
						.stream()
						.filter(item -> servletPath.contains(item.getRolePttrn().toString().trim()))
						.collect(Collectors.toList())
						;
				
				if(result.isEmpty()) {
					// TODO 이 응답에 대해 getWriter()가 이미 호출되었습니다. 해결하기 
					log.debug("CODE :"+ReturnMessage.NOACCESSAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOACCESSAUTH.getMessage()+"");
					writer.println("CODE :"+ReturnMessage.NOACCESSAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOACCESSAUTH.getMessage()+"");
				}else {
					isAccess = true;
				}
			}
			
		}catch(Exception e) {
			// TODO :Exception handle
			log.debug(e.getMessage());
		}finally {
//			writer.flush();
//			writer.close();
//			writer = null;
		}
		return isAccess;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		log.debug("postHandle");
		
	}
}



```

위 코드는 잘못되었고 

다음과같이 수정할수있다.

```java
package egovframework.com.cmm.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egovframe.rte.fdl.security.userdetails.util.EgovUserDetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import egovframework.com.bo.util.ReturnMessage;
import egovframework.com.bo.util.StringUtil;
import egovframework.com.cmm.LoginVO;

import egovframework.com.uat.role.service.EgovAuthManageService;
import egovframework.com.uat.role.service.RoleAuthVO;
import egovframework.com.uat.role.service.RoleUrlPatternVO;
import lombok.extern.slf4j.Slf4j;
/**
 * TODO : 로그인한 유저 권한에 따라 메뉴 접근권한 분기 
 *  스프링 시큐리티 인가 후 세션에 저장된 유저정보 를 통해 유저 권한을 가져온다.
	SecurityContext context = SecurityContextHolder.getContext();
	Authentication authentication = context.getAuthentication();
	String id = authentication.getName(); // 시큐리티에 잡힘 
 * @author dev002
 */


@Slf4j
public class MenuInterceptor extends HandlerInterceptorAdapter{
	
	private EgovAuthManageService egovAuthManageService;
	
	@Autowired
	public MenuInterceptor (EgovAuthManageService egovAuthManageService) {
		this.egovAuthManageService = egovAuthManageService;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean isAccess = false;
		boolean isPassed = false;
		
		String servletPath = request.getServletPath();
		
		try {
			
			LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
			RoleAuthVO auth = egovAuthManageService.getUserAuthority(loginVO);
			String authority = StringUtil.nvl(auth.getAuthority(), "");
	
			validate : {
				if(authority.equals("")) {
					response.getWriter().write("CODE :"+ReturnMessage.NOAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOAUTH.getMessage()+"");			
					isPassed = false;
					break validate;
				}else {
					isPassed = true;
				}
			}
			if(isPassed) {
				// 가져온 해당 유저의 권한을 바탕으로 롤 접근 url 을 가져온다.
				List<RoleUrlPatternVO> roleAuthUrlList = (List<RoleUrlPatternVO>)(Object)egovAuthManageService.getRoleAuthUrlListByauthority(authority);	
				// 접근 권한을 판별한다.	
				List<Object> result = roleAuthUrlList
						.stream()
						.filter(item -> servletPath.contains(item.getRolePttrn().toString().trim()))
						.collect(Collectors.toList())
						;
				
				if(result.isEmpty()) {
					log.debug("CODE :"+ReturnMessage.NOACCESSAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOACCESSAUTH.getMessage()+"");
					response.getWriter().write("CODE :"+ReturnMessage.NOACCESSAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOACCESSAUTH.getMessage()+"");
				}else {
					isAccess = true;
				}
			}
			
		}catch(Exception e) {
			log.debug(e.getMessage());
		}
		return isAccess;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		log.debug("access success!!");
		
	}
}



```
response.getWriter().write() 를 사용한다.


# List Object to List VO 
``` java
// 먼저 Object로 업 캐스팅하여 모든 객체를 모든 유형으로 캐스팅 할 수 있다.
// 예 )

// list object 
List<Object> row = myService.getRows();
// list VO 
// object 로 업캐스팅 후 , List VO 로 캐스팅 
List<RoleUrlPatternVO> roleAuthUrlList = (List<RoleUrlPatternVO>)(Object)egovAuthManageService.getRoleAuthUrlListByauthority(authority);

```
# pojo object to json Array 
// GSON 을 사용하는 방법이 있다.
# maven 
```xml
<!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.9.1</version>
</dependency>


```

``` java
String decryptedResponseData = new Gson().toJsonTree(resultBody.getPojo()).toString().replace("\u0000", "").replace("\\u0000", "").trim();
```
# jsonString escape 문자 제거 
```java
import org.apache.commons.lang.StringEscapeUtils;

String jsonStrings = StringEscapeUtils.unescapeJava(retJson.toJSONString());

```

# VO field 에 접근하여 type 알아내기
```java
SystemOrderVO.class.getDeclaredField(sKey).getType().getTypeName()

```

# 배열에서 중복되는 원소 자체를 삭제하고 싶을경우
```java
 public static void main(String[] args) {
        arr = Arrays.asList(1, 1, 7, 3, 7, 2, 2, 3, 4, 3, 0, 2, 2, 3, 1, 7, 6, 8, 4, 9);
        arr.sort((a, b) -> a - b);
        List<Integer> duplicatedList = new ArrayList<Integer>();
	
	// 중복되는 elements 를 저장 
        for (int i = 0; i < arr.size() - 1; i++) {
            if (arr.get(i) == arr.get(i + 1)) {
                duplicatedList.add(arr.get(i));
            }
        }
	// 중복 제거 후 요소 수집
        arr = arr.stream().distinct().collect(Collectors.toList());
	// 저장한 elements 를 target 으로 삭제
        duplicatedList.forEach(item -> arr.remove(item));
	
        arr.forEach(item -> System.out.println(item));

    }

```
# Object mapper 
``` java
GoodsInsertVO goods = mapper.convertValue(paramMap, AdminApiUserVO.class);
// 이렇게 하면 json.stringfy 된 json 문자열을 vo 에 맞게 매핑해준다.

```


# spring 에서 interceptor 설정 
1) interceptor class 생성 
```java

public class MenuInterceptor extends HandlerInterceptorAdapter{
	
	private final MenuService menuService;
	
	@Autowired
	MenuInterceptor(MenuService menuService){
		this.menuService = menuService;
	}
	
	
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String servletPath = request.getServletPath();
		
		boolean isAccess = false;
			String Auth = SecurityAuth.getAuth();
			//String Auth = "ROLE_USER";
			List<HashMap<String , Object>> authList = menuService.getAuthList(Auth); 
			authList.forEach(item->System.out.println(item));
			Optional<HashMap<String, Object>> findPath =
					authList
					.stream()
					.filter(item->item.get("rolePttrn").toString().contains(servletPath))
					.findAny();
			
			if(!findPath.isEmpty()) {
				isAccess = true;
			}
		
		
		return isAccess;
	}
	
	 @Override
	    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
	            ModelAndView modelAndView) throws Exception {
	        
	        System.out.println("postHandle1");
	        
	        
	    }

}


```
2) dispather-servlet.xml	
```xml
<mvc:interceptors>
	        <mvc:interceptor>
	            <mvc:mapping path="/system/**" />
	            <mvc:exclude-mapping path="/bo/uat/uia/actionLogin.do" />
	             <mvc:exclude-mapping path="/bo/login" />
	               <mvc:exclude-mapping path="/bo/selectMenuManageList" />
	         
	            <bean class="egovframework.system.menu.web.MenuInterceptor">
	            </bean>
	        </mvc:interceptor>
	    </mvc:interceptors>

```


# json String to Map or List...
```java
// string to map 
ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> mapFromString = new HashMap<>();
			try {
				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				mapFromString = mapper.readValue(paramMap.get("data").toString(), Map.class);
			} catch (IOException e) {
			    e.getMessage();
			    resultMap.put("isSuccess", false);
			    resultMap.put("message",Message.invalidDataForm);
			    resultMap.put("code",MessageCode.invalidDataForm);
			    return resultMap;
			}

```

# VO <-> MAP 

vo 객체를 map으로 변환 하거나 , map 을 vo 로 변환하고 싶을경우 . 

https://github.com/zuzu578/java-8-/blob/main/ConvertUtils.java

 참고 : https://kim-jong-hyun.tistory.com/47 
====> 
``` java
 // List hashMap to list vo
    		List<GoodsVO> convertValueObjects = convertUtils.convertToValueObjects(arr,GoodsVO.class);
 

```
1)  parameter : list hashMap , vo class  




# spring 에서 autowired 문제 

@Service, @Repository, @Component 를 붙이면 Spring Container가 관리하는 bean 객체라는 뜻.

@Autowired 를 붙여주어야 두 클래스의 의존관계가 형성된다.
new로 객체를 생성해서 쓰면 그 객체는 Spring Container가 관리하는게 아니어서 @Autowired를 통한 의존성 주입이 불가해진다.
때문에 
<img width="436" alt="스크린샷 2022-05-13 오후 4 39 17" src="https://user-images.githubusercontent.com/69393030/168235069-32208fb4-4b16-42dd-8291-ba83c766e67e.png">

이렇게 하도록 하자.

참조 : https://rimkongs.tistory.com/256


# subString 

```java

String test = "123456";
test.substring(5); // 0~4 제외한 값 가져옴 js 의 slice()와 비슷 

```

# list map get value 
```java
List CmmnCodeList = zipManageService.selectZipList(searchVO);
			System.out.println("test " + CmmnCodeList);
			List<Map<String, Object>> listMap = CmmnCodeList;  
			for(int i = 0 ; i < listMap.size() ; i++) {
				System.out.println(listMap.get(i).get("rnum"));
			}
```
# 문자열 뒤집기

String buffer 에서 reverse 를 사용하면된다.
```java
StringBuffer sb = new StringBuffer(String.join("", difficulty));
		String reverse = sb.reverse().toString();

```

# 뒤에서 문자열 자르기 

우연히 개인 프로젝트 개발중 이런문제가있었다. 곡 + 난이도 가있는 문자열에서 뒤에있는 난이도만  추출하고싶었을때 어찌하면좋을까? 

해결책은 이렇다. subString.

```java

String test2 = "カウントダウン 「名探偵コナン」より 〇 2 4 5 8 -";
System.out.println(test2.substring(test2.length() - 9, test2.length()));



```

# JSONObject 접근하기

간혹 

```
{
    "type": "champion",
    "format": "standAloneComplex",
    "version": "10.16.1",
    "data": {
        "Aatrox": {
            "version": "10.16.1",
            "id": "Aatrox",
            "key": "266",
            "name": "Aatrox",
            "title": "Die Klinge der Düsteren",
            "blurb": "Einst waren Aatrox und seine Brüder ehrenhafte Verteidiger Shurimas gegen die Leere, die zu einer noch größeren Bedrohung für Rune

}
```
이런 object 일경우 이렇게 접근하면 된다.
```java
String response = stringBuffer.toString();

            JSONParser parser = new JSONParser();

            JSONObject j1 = (JSONObject) parser.parse(response);

            Iterator iter = j1.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                if (j1.get(key) instanceof JSONObject) {
                    JSONObject value = (JSONObject) j1.get(key);

                    Iterator iter2 = value.keySet().iterator();
                    while (iter2.hasNext()) {
                        String key2 = (String) iter2.next();
                        if (value.get(key2) instanceof JSONObject) {
                            JSONObject value2 = (JSONObject) value.get(key2);
                            p1.put(value2.get("key"), value2.get("id"));

                        }
                    }

                }
            }



```

# @Cacheable
cache 란 , 한번 읽은 데이터를 임시로 저장하고 필요에따라 전송 , 갱신 , 삭제하는 기술로 보통 데이터의 보관장소로 서버의 메모리를 사용하는경우가 많다.
디스크에서 정보를 얻어오는것보다 훨씬 빠른 IO 성능을 얻을수 있으나 서버가 다운되거나 재부팅되는 경우 사라지는 성격의 휘발성을 가지고 있어서 , 영속적으로 보관할수없는,  임시적으로 보관하고 빠르게 그 정보에 접근하기 위한 용도로 사용한다.

Cache 를 쓰는 목적은

1) 서버간 불필요한 트래픽을 줄인다.

2) 웹어플리케이션의 서버의 부하를 감소시킨다.

3) 어플리케이션의 빠른 처리성능을 확보하여 궁극적으로 어플리케이션을 사용하는 클라이언트에게 최적의 서비스를 제공한다.

@Cacheable 은 캐시가 있으면 캐시의 정보를 가져오고 , 없으면 캐시를 등록한다(ex): Redis 등 )

# spring 에서의 @RequestBody와 @ModelAttribute


1) @RequestBody
```java
// controller
@PostMapping("/requestbody")
public ResponseEntity<RequestBodyDto> testRequestBody(@RequestBody RequestBodyDto requestBodyDto) {
    return ResponseEntity.ok(requestBodyDto);
}
public class RequestBodyDto {

    private String name;
    private long age;
    private String password;
    private String email;

    public RequestBodyDto() {
    }

    public RequestBodyDto(String name, long age, String password, String email) {
        this.name = name;
        this.age = age;
        this.password = password;
        this.email = email;
    }
	// getter , setter 
}



```

클라이언트가 보내는 http 요청 본문(json , xml) 을 java object 로 변환한다.

내부적으로 ObjectMapper를 통해 JSON 값을 Java 객체로 역직렬화하는 것을 알 수있다.

@RequestBody를 사용하면 요청 본문의 JSON, XML, Text 등의 데이터가 적합한 HttpMessageConverter를 통해 파싱되어 Java 객체로 변환 된다.
@RequestBody를 사용할 객체는 필드를 바인딩할 생성자나 setter 메서드가 필요없다.

다만 직렬화를 위해 기본 생성자는 필수다.
또한 데이터 바인딩을 위한 필드명을 알아내기 위해 getter나 setter 중 1가지는 정의되어 있어야 한다.

2) @ModelAttribute

@ModelAttribute 애너테이션의 역할은 클라이언트가 보내는 HTTP 파라미터들을 특정 Java Object에 바인딩(맵핑) 하는 것입니다. /modelattribute?name=req&age=1 같은 Query String 형태 혹은 요청 본문에 삽입되는 Form 형태의 데이터를 처리합니다.

@ModelAttribute를 사용하면 HTTP 파라미터 데이터를 Java 객체에 맵핑한다.

따라서 객체의 필드에 접근해 데이터를 바인딩할 수 있는 생성자 혹은 setter 메서드가 필요하다.
Query String 및 Form 형식이 아닌 데이터는 처리할 수 없다.


참조 :  https://tecoble.techcourse.co.kr/post/2021-05-11-requestbody-modelattribute/






# IntStream sorted() 하기 

IntStream 을 stream 에서 sorted() 하기 위해서는 boxed 를 사용한다.

ex) 
```java
int a = 3;
int b = 5;

IntStream stream3 = IntStream.rangeClosed(a, b);

stream3.boxed().sorted(Comparator.reverseOrder()).forEach(item->System.out.println(item));

```


# collections 에서 min max 값구하기 (arrayList)
```java
Collections.max(list);
Collections.min(list);
```


출처: https://hianna.tistory.com/571 [어제 오늘 내일]

# 자바 int 배열을 Integer 배열로 변환
// boxed => IntStream 같이 원시타입에 대한 스트림 지원을 클래스 타입 으로 전환하여 전용으로 실행 가능한 기능을 수행하기 위해 존재한다.

```java
import java.util.Arrays;
public class MyClass {
    public static void main(String args[]) {
        int a[] = {1,2,3,4};
	
        int a[] = {1,2,3,4};
        Integer b[] = Arrays.stream(a).boxed().toArray(Integer[]::new); 

        System.out.println( a.getClass() );        // class [I
        System.out.println( Arrays.toString(a) );  // [1, 2, 3, 4]
        
        System.out.println( b.getClass() );        // class [Ljava.lang.Integer;
        System.out.println( Arrays.toString(b) );  // [1, 2, 3, 4]
    }
}


```

# stream 으로 중복문자 제거 
```java
List<String> temp2 = new ArrayList<String>();
		//🎨🎍🎍🎍🎪🎪👜🎍🎨👜👜🎍
		String s = "🎨🎍🎍🎍🎪🎪👜🎍🎨👜👜🎍";
		String [] temp = s.split("");
		for(int i = 0 ; i < temp.length; i++) {
			temp2.add(temp[i]);
		}
		List<String> result = temp2.stream().distinct().collect(Collectors.toList());
		System.out.println(String.join("", result));

```

# 요소의 수집 (collect) 

연산처리후 , 요소를 수집하기 위해서 collect(Collectors.toList()) 를 사용한다 이렇게 하게되면 수집한 요소를 List 로 수집할수있다.
collect() 메소드는 인수로 전달되는 Collectors 객체에 구현된 방법대로 스트림의 요소를 수집합니다.

또한, Collectors 클래스에는 미리 정의된 다양한 방법이 클래스 메소드로 정의되어 있습니다.

이 외에도 사용자가 직접 Collector 인터페이스를 구현하여 자신만의 수집 방법을 정의할 수도 있습니다.

 

스트림 요소의 수집 용도별 사용할 수 있는 Collectors 메소드는 다음과 같습니다.

 

1. 스트림을 배열이나 컬렉션으로 변환 : toArray(), toCollection(), toList(), toSet(), toMap()

2. 요소의 통계와 연산 메소드와 같은 동작을 수행 : counting(), maxBy(), minBy(), summingInt(), averagingInt() 등

3. 요소의 소모와 같은 동작을 수행 : reducing(), joining()

4. 요소의 그룹화와 분할 : groupingBy(), partitioningBy()
```java
//요소에서 짝수 , 홀수 글자 를 filter 해서 수집하기 
	Stream<String> stream = Stream.of("HTML", "CSS", "JAVA", "PHP");
	
		List<String> evenWord = stream.filter(item -> item.length()%2 == 0).collect(Collectors.toList());
		List<String> oddWord = stream.filter(item -> item.length()%3 ==0).collect(Collectors.toList());
		evenWord.forEach(item->System.out.println(item));
		oddWord.forEach(item->System.out.println(item));


```


collect

```java
public static void main(String[] args) {
		Stream<String> stream = Stream.of("넷", "둘", "하나", "셋");
		List<String> myList = new ArrayList<String>(stream.collect(Collectors.toList()));
		Iterator<String> iter = myList.iterator();
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
	}


```

# 요소의 연산 : sum(), average()

IntStream이나 DoubleStream과 같은 기본 타입 스트림에는 해당 스트림의 모든 요소에 대해 합과 평균을 구할 수 있는 sum()과 average() 메소드가 각각 정의되어 있습니다.

이때 average() 메소드는 각 기본 타입으로 래핑 된 Optional 객체를 반환합니다.

```java

	IntStream num = IntStream.of(1,2,3,4,5,6,7);
		DoubleStream num2 = DoubleStream.of(0.1,2.3);
		OptionalDouble num2Average = num2.average();

```

# 요소의 통계 
연산후 Optional 객체를 return 한다

```java
		IntStream num = IntStream.of(1,2,3,4,5,6,7);
		int sum = num.sum();// 합 
		OptionalInt min = num.min();// 최소 
		OptionalInt max = num.max();// 최대 

```



# 요소의 검사
해당 스트림에서 특정 조건을 만족하는 요소가 있는지 없는지 (여부) (bool) 을 return 합니다.
 

1. anyMatch() : 해당 스트림의 일부 요소가 특정 조건을 만족할 경우에 true를 반환함.

2. allMatch() : 해당 스트림의 모든 요소가 특정 조건을 만족할 경우에 true를 반환함.

3. noneMatch() : 해당 스트림의 모든 요소가 특정 조건을 만족하지 않을 경우에 true를 반환함.


```java
public static void main(String[] args) {
		IntStream stream = IntStream.of(7, 5, 5, 2, 1, 2, 3, 5, 4, 6);
		// allMattch , 
//		boolean isMatched1 = stream.distinct().anyMatch(item -> item > 4);
//		System.out.println(isMatched1);
		boolean isMatched2 = stream.distinct().allMatch(item -> item > 4);
		System.out.println(isMatched2);
	}



```

# 요소의 검색 

findFirst()와 findAny() 메소드는 해당 스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환합니다.

두 메소드 모두 비어 있는 스트림에서는 비어있는 Optional 객체를 반환합니다. 때문에 int type 으로 참조변수로 사용할수 없다. 

```java
IntStream stream = IntStream.of(7, 5, 5, 2, 1, 2, 3, 5, 4, 6);
		// findFirst() , findAny() 를 사용하면 , 첫번째 요소값을 반환 하는데 , return taype 은 OptionalInt
		OptionalInt result = stream.findFirst();
		System.out.println(result.getAsInt());

```
# 스트림 정렬
```java

public static void main(String[] args) {
		List<String> myList = new ArrayList<String>(Arrays.asList("JAVA", "HTML", "JAVASCRIPT","CSS"));
		myList.stream().sorted().forEach(item->System.out.println(item));
		List<String> myList2 = new ArrayList<String>(Arrays.asList("JAVA", "HTML", "JAVASCRIPT","CSS"));
		myList2.stream().sorted(Comparator.reverseOrder()).forEach(item->System.out.println(item));;
	}

```

# 스트림 제한 
1) limit() : 해당 스트림의 첫번째 요소부터 전달된 개수만큼의 요소만으로 이루어진 새로운 스트림을 반환 

2) skip() : 해당 스트림의 첫번째 요소부터 전달된 개수만큼 요소를 제외한 나머지 요소만으로 이루어진 새로운 스트림을 반환  

```java
public static void main(String[] args) {
		IntStream stream3 = IntStream.range(0, 10);
		// limit(5) => 0~4 까지  
		//stream3.limit(5).forEach(item->System.out.println(item));
		// skip(5) => 5부터 9까지 
		//stream3.skip(5).forEach(item->System.out.println(item));
		// 2~4 까지 
		stream3.limit(5).skip(2).forEach(item->System.out.println(item));
		
		
		
	}


```

# 컬렉션에서 스트림 생성 
collection 인터페이스를 구현한 모른 list , set 컬렉션 클래스에서도 stream() 메소드로 스트림을 생성할수있다.
parallelStream()을 사용하면 병렬처리 가능한 스트림을 생성할수있다.
```java
public static void main(String[] args) {
		ArrayList<Integer> a1 = new ArrayList<Integer>();
		a1.add(10);
		a1.add(20);
		a1.add(30);
		a1.add(40);
		
		Stream<Integer> s1 = a1.stream();
		s1.forEach((item->System.out.println(item)));
		
	}


```

# 배열에서 스트림 생성 

```java
public static void main(String[] args) {
		// 배열에서 스트림 생성 
		String[] arr = new String[] {"하나","둘","셋","넷"};
		Stream<String> s1 = Arrays.stream(arr);
		
		// 배열에서 특정 부분만을 이용하여 생성 
		Stream<String> t1 = Arrays.stream(arr,1,3);
		
	}

```

# 가변 매개변수 에서 스트림 생성 
Stream 에서 of 를 사용하여 가변적으로 생성가능 
```java

	// 가변매개변수에서 스트림 생성 
		String [] param = new String [] {"t","q","r","s" } ;
		Stream<String> stream  = Stream.of(param);
		stream.forEach(item->System.out.println(item));
```

# 지정된 범위에서 연속된 정수
```java

public static void main(String[] args) {
		// 0~4 까지 
		IntStream stream1 = IntStream.range(0, 5);
		stream1.forEach(item->System.out.println(item));
		// 0~5 까지
		IntStream stream2 = IntStream.rangeClosed(0, 5);
		stream2.forEach(item->System.out.println(item));
		
		
		
	}


```

# 난수 스트림 

```java
// 특정 타입의 난수로 이루어진 스트림 생성

IntStream stream = new Random().ints(4);

```
# optional class

Java8에서는 Optional<T> 클래스를 사용해 NPE를 방지할 수 있도록 도와준다. Optional<T>는 null이 올 수 있는 값을 감싸는 Wrapper 클래스로, 참조하더라도 NPE가 발생하지 않도록 도와준다. Optional 클래스는 아래와 같은 value에 값을 저장하기 때문에 값이 null이더라도 바로 NPE가 발생하지 않으며, 클래스이기 때문에 각종 메소드를 제공해준다
	
	
```java
	String temp = Optional.of("test").map(String::toUpperCase).orElse("not test");
		
		System.out.println(temp);
	
```
# hasMap 에서 키값 / value 값으로 요소 찾기

```java
	// 키값으로 찾기

	Map<String , Object> temp = new HashMap<String , Object>();
		temp.put("key1", "temp1");
		temp.put("key2", "temp2");
		temp.put("key3", "temp3");
		temp.put("key4", "temp4");
		Optional<Object> tem = temp.entrySet().stream().filter(item -> item.getKey().equals("key1")).map(Map.Entry::getValue).findFirst();
		System.out.println(tem.get());

	// 밸류값으로 키 찾기
	
	Map<String , Object> temp = new HashMap<String , Object>();
		temp.put("key1", "temp1");
		temp.put("key2", "temp2");
		temp.put("key3", "temp3");
		temp.put("key4", "temp4");
		Optional<String> tem = temp.entrySet().stream().filter(item -> item.getValue().equals("temp1")).map(Map.Entry::getKey).findFirst();
		System.out.println(tem.get());


```
# find 
```java

ArrayList<String> temp5 = new ArrayList<String>(Arrays.asList("hello","world","name"));
		Optional<String> result = temp5.stream().filter(s -> s.startsWith("h")).findFirst();
		System.out.println(result.get());


```
# sort( 정렬 ) 
```java
ArrayList<String> words = new ArrayList<String>(Arrays.asList("a","c","q","b","f","h","g","d"));
		List<String> result = words.stream().sorted().collect(Collectors.toList());


```
# Comparator

```java
String arr2[] = {
				"21 Junkyu",
				"21 Dohyun",
				"20 Sunyoung"
				};
		
		//Arrays.asList(arr2).stream().sorted((a,b)->a.split(" ")[0].equals(b.split(" ")[0]) ? a.compareTo(b) : a.length()-b.length()).collect(Collectors.toList()).forEach(item->System.out.println(item));;
//		Arrays.asList(arr)
//		.stream()
//		.distinct()
//		.sorted((a,b)->a.length() == b.length()? a.compareTo(b) : a.length() -b.length())
//		.forEach(item->System.out.println(item));
//		
	
		Arrays.sort(arr2, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				if(s1.split(" ")[0] == s2.split(" ")[0]) {
					return 0; 
				}else {
					System.out.println(s2.length() - s1.length());
					return s2.length() - s1.length();
				}
			}
		});
		
		Arrays.asList(arr2).stream().distinct().forEach(item->System.out.println(item));


```
# 오름차순 내림차순 정렬 
```java

String str = "ACBED";
		List<String> stringArr = Arrays.asList(str.split(""));
	
		// Stream.of() , array.stream() 차이 좀 ..
		List<String> result2 = stringArr.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()); // 내림차순
		List<String> result3 = stringArr.stream().sorted().collect(Collectors.toList());
		System.out.println("result2"+result2);
		System.out.println("result3"+result3);

```

# 대소문자 혼합될경우의 정렬 
```java
ArrayList<String> words = new ArrayList<String>(Arrays.asList("A","c","Q","B","f","H","g","D"));

		List<String> result = words.stream().map(item -> item.toUpperCase()).sorted().collect(Collectors.toList());
		System.out.println(result);

```
# Local variable '변수명' defined in an enclosing scope must be final or effectively final
람다식에서 지역변수는  변경이 불가능하기떄문에 변수를 클래스단에서 생성해서 사용한다.
```java
public class Test {
	
	private static int empty = 0;
	
	public static void main(String[] args) {
		
		ArrayList<Integer> temp5 = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
		
		
		temp5.stream().skip(6).forEach(item-> System.out.println(empty = item + empty));
	
	}



```
# foreach (lamda) 
```java

ArrayList<String> a1 = new ArrayList<String>(Arrays.asList("hello","world","name"));
		a1.forEach(item->System.out.println(item));	
	
	
	
HashMap<String, String> map = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
        map.put("no", "3");
        map.put("title", "test3");
        map.put("no1", "4");
        map.put("title1", "test4");
        list.add(map);
        list.forEach(item->System.out.println(item));

```

# java 8 Filter ( lamda )
```java
/**
	List<Integer> numberList = new ArrayList<Integer>();
		int [] arr = {1,2,3,4,5,6,7,8,9};
		for(int i = 0 ; i < arr.length ; i++) {
			numberList.add(arr[i]);
		}
		numberList.stream().filter(item->item > 4).forEach(item->System.out.println(item));
			
	
**/
public static void main(String[] args) {
		List <Integer> number = Arrays.asList(1,2,3,4,5,6,7);

		List<Integer> result22 =  number.stream().filter(item->item > 4).collect(Collectors.toList());
		System.out.println(result22);
		
// 혹은 

	ArrayList<String> a1 = new ArrayList<String>(Arrays.asList("hello","world","name"));
		List<String> temp = a1.stream().map(item -> item.toUpperCase()).collect(Collectors.toList());
		System.out.println(temp);

```
# list HashMap 에서 요소 빼오기 

``` java

	HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.put("name1", "kaka1");
		temp.put("name2", "kaka2");
		temp.put("name3", "kaka3");
		temp.put("name4", "kaka4");
		temp.put("name5", "kaka5");
		List<HashMap> list = new ArrayList<>();
		list.add(temp);
		for(int i = 0 ; i < list.size() ; i++) {
			System.out.println(list.get(i).entrySet().stream()
	                .filter(entry -> Objects.equals(((Entry) entry).getValue(), "kaka1"))	         
	                .collect(Collectors.toList()));
		}


```
	
# distinct (중복제거)

``` java
List<String> a1 = new ArrayList<String>();
		a1.add("hello");
		a1.add("hello2");
		a1.add("hello");
		a1.add("hello1");
		
		List<String> result = a1.stream().distinct().collect(Collectors.toList());
		result.forEach(item->System.out.println(result));
	
IntStream stream2 = IntStream.of(7, 5, 5, 2, 1, 2, 3, 5, 4, 6);
		stream2.distinct().forEach(item->System.out.println(item));;
	
	
// 홀수만 출력하기
	IntStream stream2 = IntStream.of(7, 5, 5, 2, 1, 2, 3, 5, 4, 6);
		stream2.distinct().filter(item -> item % 2 != 0).forEach(item->System.out.println(item));
	
	
	
```
	
# stream 활용한 문제풀이 
<img width="461" alt="스크린샷 2022-04-06 오후 10 58 09" src="https://user-images.githubusercontent.com/69393030/161991775-770f73c7-9d48-4cd9-a882-7a37014e0538.png">


	
```java
	// 본인은 이렇게함 
public class Test {
	
	public static void main(String[] args) {
		List<String> myList = new ArrayList<String>();
		long n = 118372;
		String num = Integer.toString((int) n);
		String [] num2 = num.split("");

		for(int i = 0 ; i < num2.length; i++) {
			myList.add(num2[i]);
		}
	
		List<String> result = myList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		
		System.out.println(Long.parseLong(String.join("", result)));
	
		
		
	}
	
}	
	
```
# 비밀번호 체크
```java
String pattern1 = "^.*(?=^.{8,20}$)(?=.*\\d)(?=.*[a-z])(?=.*[^0-9a-zA-Z]).*$";		// 숫자,소문자,특수문자
			String pattern2 = "^.*(?=^.{8,20}$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$";				// 숫자,소문자,대문자
			String pattern3 = "^.*(?=^.{8,20}$)(?=.*\\d)(?=.*[A-Z])(?=.*[^0-9a-zA-Z]).*$";		// 숫자,대문자,특수문자
			String pattern4 = "^.*(?=^.{8,20}$)(?=.*[a-z])(?=.*[A-Z])(?=.*[^0-9a-zA-Z]).*$";	// 소문자,대문자,특수문자	
```
