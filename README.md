# java 8 Filter ( lamda )
```java
public static void main(String[] args) {
		ArrayList<Integer> a1 = new ArrayList<Integer>();
		a1.add(10);
		a1.add(20);
		a1.add(30);
		a1.add(40);
		a1.add(50);
    // filter 를 사용한경우 
		List<Integer> temp = a1.stream()
		.filter(item -> item > 30)
		.collect(Collectors.toList());
		// filter 를 사용하지않은경우 
		System.out.println(temp);
		for(int i = 0 ; i < a1.size();i++) {
			if(a1.get(i)>30) {
				System.out.println(a1.get(i));
			}
		
		}


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
