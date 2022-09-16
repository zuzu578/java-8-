package egovframework.system.listener;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import egovframework.system.login.handler.LoginSuccessHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomHttpSessionListener implements HttpSessionBindingListener{

    
    //로그인한 접속자를 담기위한 해시테이블
    private static Hashtable loginUsers = new Hashtable();
    
    /*
     * 이 메소드는 세션이 연결되을때 호출된다.(session.setAttribute("login", this))
     * Hashtable에 세션과 접속자 아이디를 저장한다.
     */
    public void valueBound(HttpSessionBindingEvent event) {
        //session값을 put한다.
        loginUsers.put(event.getSession(), event.getName());
        log.debug(event.getName() + "님이 로그인 하셨습니다.");
        log.debug("현재 접속자 수 : " +  getUserCount());
     }
    
    
     /*
      * 이 메소드는 세션이 끊겼을때 호출된다.(invalidate)
      * Hashtable에 저장된 로그인한 정보를 제거해 준다.
      */
     public void valueUnbound(HttpSessionBindingEvent event) {
         //session값을 찾아서 없애준다.
    	  log.debug("event.getSession() ===========>" + event.getSession());
         loginUsers.remove(event.getSession());
         log.debug("  " + event.getName() + "님이 로그아웃 하셨습니다.");
         log.debug("현재 접속자 수 : " +  getUserCount());
     }
     
     
     /*
      * 입력받은 아이디를 해시테이블에서 삭제. 
      * @param userID 사용자 아이디
      * @return void
      */ 
     public void removeSession(HttpSession nowSession, String deleteSessionTarget){
          Enumeration e = loginUsers.keys();
          HttpSession session = null;
          while(e.hasMoreElements()){
               session = (HttpSession)e.nextElement();
               	 // 현재세션을 제외한 모든 중복되는 세션 제거
               if(deleteSessionTarget.equals("previous")) {
            	   if(!nowSession.getId().equals(session.getId())){
               	   	session.invalidate();
                  	 }
               	 }
               // 현재세션을 제거 , 이전세션을 사용 
               if(deleteSessionTarget.equals("now")) {
            	   if(nowSession.getId().equals(session.getId())){
                  	   	session.invalidate();
                    }
               	 }
//               if(loginUsers.get(session).equals(userId)){
//                   
//            	   //세션이 invalidate될때 HttpSessionBindingListener를 
//                 //구현하는 클레스의 valueUnbound()함수가 호출된다.          
//            	session.invalidate();
//               }
          }
          
     }
//     /**
//      * 현재 로그인한 세션아이디를 제외한 중복되는 아이디의 세션을 제거 (로그인 중복제거)
//      * @param session
//      */
//     public void removeSessionBySessionId(HttpSession session) {
//    	 Enumeration e = loginUsers.keys();
//    	 while(e.hasMoreElements()) {
//    		 System.out.println("session test ===>" + loginUsers.get(session));
//    	 }
//     }

    /*
     * 해당 아이디의 동시 사용을 막기위해서 
     * 이미 사용중인 아이디인지를 확인한다.
     * @param userID 사용자 아이디
     * @return boolean 이미 사용 중인 경우 true, 사용중이 아니면 false
     */
    public boolean isUsing(String userID){
    	 return loginUsers.containsValue(userID);
    }
     
    
    /*
     * 로그인을 완료한 사용자의 아이디를 세션에 저장하는 메소드
     * @param session 세션 객체
     * @param userID 사용자 아이디
     */
    public void setSession(HttpSession session, String userId){
        //이순간에 Session Binding이벤트가 일어나는 시점
        //name값으로 userId, value값으로 자기자신(HttpSessionBindingListener를 구현하는 Object)
        session.setAttribute(userId, this);//login에 자기자신을 집어넣는다.
    }
     
     
    /*
      * 입력받은 세션Object로 아이디를 리턴한다.
      * @param session : 접속한 사용자의 session Object
      * @return String : 접속자 아이디
     */
    public String getUserID(HttpSession session){
        return (String)loginUsers.get(session);
    }
     
     
    /*
     * 현재 접속한 총 사용자 수
     * @return int  현재 접속자 수
     */
    public int getUserCount(){
        return loginUsers.size();
    }
     
     
    /*
     * 현재 접속중인 모든 사용자 아이디를 출력
     * @return void
     */
    public void printloginUsers(){
        Enumeration e = loginUsers.keys();
        HttpSession session = null;
        log.debug("===========================================");
        int i = 0;
        while(e.hasMoreElements()){
            session = (HttpSession)e.nextElement();
            log.debug((++i) + ". 접속자 : " +  loginUsers.get(session));
        }
        log.debug("===========================================");
     }
     
    


}
