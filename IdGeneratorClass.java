public IdGeneratorClass {
  
  public static String idGenerate(){
      String preFix = "USER";
      String middle = "";
      String end = "99999999";
      String result = "";
            
      int maxSize = 12;
      int preFixAndEndLength = preFix.length() + end.length();
      int indexTarget = maxSize - preFixAndEndLength;
            
      if (indexTarget <= -1) {
                result = "자동생성할 id 값이 12자리가 넘어갑니다.";
      } else {
          for (int i = 0; i < indexTarget; i++) {
               middle += "0";
          }
        result = preFix + middle + end;
      }
  }

}
