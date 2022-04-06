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
