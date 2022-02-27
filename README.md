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
