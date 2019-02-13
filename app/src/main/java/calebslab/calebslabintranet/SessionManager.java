package calebslab.calebslabintranet;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.HttpURLConnection;
import java.util.List;

public class SessionManager {

    //프리퍼런스에 세션정보 저장
    public void setSessionIdInSharedPref(String sessionid, Context context){
        SharedPreferences pref = context.getSharedPreferences("sessionCookie",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        if(pref.getString("sessionid",null) == null){ //처음 로그인하여 세션아이디를 받은 경우
            Log.d("LOG","처음 로그인하여 세션 아이디를 pref에 넣었습니다."+sessionid);
        }else if(!pref.getString("sessionid",null).equals(sessionid)){ //서버의 세션 아이디 만료 후 갱신된 아이디가 수신된경우
            Log.d("LOG","기존의 세션 아이디"+pref.getString("sessionid",null)+"가 만료 되어서 "
                    +"서버의 세션 아이디 "+sessionid+" 로 교체 되었습니다.");
        }
        edit.putString("sessionid",sessionid);
        edit.apply(); //비동기 처리
    }

    public void getCookieHeader(HttpURLConnection conn, Context context){//Set-Cookie에 배열로 돼있는 쿠키들을 스트링 한줄로 변환
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
        //cookies -> [JSESSIONID=D3F829CE262BC65853F851F6549C7F3E; Path=/smartudy; HttpOnly] -> []가 쿠키1개임.
        //Path -> 쿠키가 유효한 경로 ,/smartudy의 하위 경로에 위의 쿠키를 사용 가능.
        if (cookies != null) {
            for (String cookie : cookies) {
                String sessionid = cookie.split(";\\s*")[0];
                //JSESSIONID=FB42C80FC3428ABBEF185C24DBBF6C40를 얻음.
                //세션아이디가 포함된 쿠키를 얻었음.
                setSessionIdInSharedPref(sessionid, context);

            }
        }
        //return null;
    }

    public void setCookieHeader(Context context, HttpURLConnection conn){
        SharedPreferences pref = context.getSharedPreferences("sessionCookie",Context.MODE_PRIVATE);
        String sessionid = pref.getString("sessionid",null);
        if(sessionid!=null) {
            Log.d("LOG","세션 아이디"+sessionid+"가 요청 헤더에 포함 되었습니다.");
            conn.setRequestProperty("Cookie", sessionid);
        }
    }


}
