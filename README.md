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

```

# java 8 Filter ( lamda )
```java
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
