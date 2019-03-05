package calebslab.calebslabintranet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;



public class WebViewInterface {

    private WebView mAppView;
    private Activity mContext;
    private Handler mHandler;
    private static String userData;
    JSONObject jsonData;
    LoginService ls = new LoginService();

    /**
     * 생성자.
     * @param activity : context
     * @param view : 적용될 웹뷰
     */
    public WebViewInterface(Activity activity, WebView view, Handler handler) {
        mAppView = view;
        mContext = activity;
        mHandler = handler;
    }


    /**
     * 내용 : 로그인
     * param id : 로그인 아이디, pwd : 로그인 패스워드, sndUrl : 대상 서버 URL
     * return : 로그인 성공 시 userData String , 실패시 null 반환
     **/
    @JavascriptInterface
    public String callLogin(final String id, final String pwd, final String sndUrl) throws Exception {

        userData = ls.login(id,pwd,sndUrl,mContext);

        return userData;
    }

    /**
     * 내용 : 로그아웃
     * param sndUrl : 대상 서버 URL, id : 로그아웃 아이디
     * return : 로그아웃 성공 시 "success" 실패 시 "fail" 반환
     **/
    @JavascriptInterface
    public String callLogout(final String sndUrl, final String id) throws Exception{

        userData = ls.logOut(sndUrl,id,mContext);

        return userData;
    }


    /**
     * 내용 : android preference에 저장된 세션 아이디 반환
     * return : 세션 아이디 반환
     **/
    @JavascriptInterface
    public String returnSessionId() {

        SharedPreferences pref = mContext.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
        String sessionId = pref.getString("sessionid", null);
        sessionId = sessionId.substring(11);

        return sessionId;
    }

    /**
     * 내용 : 화면이동 함수
     * return : 세션 아이디 반환
     **/
    @JavascriptInterface
    public void movePage(final String url, final String json) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(json != null) {
                    try {
                        jsonData = new JSONObject(json);
                        //Toast.makeText(mContext, json, Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAppView.loadUrl("file:///android_asset/html/" + url); // url로 페이지 이동
            }
        });
    }

}
