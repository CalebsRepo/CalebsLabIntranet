package calebslab.calebslabintranet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import component.Contacts;

public class WebViewInterface {

    private WebView mAppView;
    private Activity mContext;
    private static String userData;

    /**
     * 생성자.
     * @param activity : context
     * @param view : 적용될 웹뷰
     */
    public WebViewInterface(Activity activity, WebView view) {
        mAppView = view;
        mContext = activity;
    }


    /**
     * 내용 : 로그인
     * param id : 로그인 아이디, pwd : 로그인 패스워드, sndUrl : 대상 서버 URL
     * return : 로그인 성공 시 userData String , 실패시 null 반환
     **/
    @JavascriptInterface
    public String callLogin(final String id, final String pwd, final String sndUrl) throws Exception {

        LoginService ls = new LoginService();

        userData = ls.login(id,pwd,sndUrl,mContext);

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

}
