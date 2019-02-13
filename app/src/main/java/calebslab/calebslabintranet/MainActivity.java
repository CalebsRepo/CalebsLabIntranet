package calebslab.calebslabintranet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import component.Contacts;


public class MainActivity extends AppCompatActivity {

    final Context myApp = this;

    WebView web; // 웹뷰 선언
    JSONObject jsonData; // 자바스크립트에서 값을 받을 json 변수 선언
    JSONArray jsonContacts; // 전화번호부에서 이름, 전화번호를 받아와 jsonarray형태로 변환 후 web단으로 넘기기 위한 변수

    /*갤러리용*/
    private  static  final int FILECHOOSER_RESULTCODE =1;
    private final static int FILECHOOOSER_LOLLIPOP_REQ_CODE=2;
    private ValueCallback<Uri> mUploadMessage =null;
    private ValueCallback<Uri[]> filePathCallbackLollipop;
    private Uri mCapturedImageURI;


    private final Handler handler = new Handler();

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //세로모드 고정
        web = (WebView) findViewById(R.id.webview); //웹뷰 선언
        web.setWebViewClient(new WebViewClient());

        // 웹뷰 세팅
        WebSettings webSet = web.getSettings();                   // 웹뷰 설정
        webSet.setJavaScriptEnabled                     (true) ; // 자바스크립트 허용
        webSet.setLoadWithOverviewMode(true);
        webSet.setUseWideViewPort                       (true) ; // 웹뷰에 맞게 출력하기
        webSet.setBuiltInZoomControls                   (false); // 안드로이드 내장 줌 컨트롤 사용 X
        webSet.setAllowUniversalAccessFromFileURLs      (true) ; // file://URL이면 어느 오리진에 대해서도 Ajax로 요청을 보낼 수 있다.
        // API 레벨 16부터 이용할 수 있다.
        webSet.setJavaScriptCanOpenWindowsAutomatically (true) ; // javascript 가  window.open()을 사용할 수 있도록 설정
        webSet.setSupportMultipleWindows                (true) ; // 여러개의 윈도우를 사용할 수 있도록 설정
        webSet.setSaveFormData                          (false); // 폼의 입력값를 저장하지 않는다
        webSet.setSavePassword                          (false); // 암호를 저장하지 않는다.
        webSet.setLayoutAlgorithm                       (WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.setWebContentsDebuggingEnabled(true);             //API 레벨 21부터 이용 가능.
        }
        web.addJavascriptInterface(new WebAppInterface(this), "android");

        web.loadUrl("file:///android_asset/html/index.html"); // 처음 로드할 페이지


        web.setWebViewClient(new android.webkit.WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                /*전화 또는 메일로 연결시 loadUrl 타지않고 해당 앱으로 연결*/
                if (url.startsWith(("tel:")) || url.startsWith("mailto:")) {
                    Intent intent ;
                    if (url.startsWith("tel:")) {
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    }else {
                        intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    }
                    startActivity(intent);
                    return true;
                }

                Log.d("MovePage", "이동 대상 URL  : " + url);
                view.loadUrl(url);
                Log.d("MovePage", "이동 완료");
                return true;

            }
        });



        //크롬 클라이언트 생성:
        //html5의 file 기능을 사용하기 위해서는 웹뷰에 setWebChromeClient 설정이 따로 필요하다
        web.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(myApp)
//                        .setTitle("Warning") // AlertDialog
//                        .setIcon(R.drawable.warning_icon) //warning icon add
                        .setMessage(message)
                        .setPositiveButton("OK",
                                new AlertDialog.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            };

            /* 웹뷰에서는 버전별로 파일첨부 코드가 필요하다
               Android 5.0 이후 버전에서는 onShowFileChooser를 이용해서 파일선택을 할수 있다
               그 이전 버전 확인은 http://acorn-world.tistory.com/62에서 확인가능하다
            */
            public  boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                              WebChromeClient.FileChooserParams fileChooserParams){
                Log.d("MainActivity","HYJ:갤러리열기");

                // Callback 초기화 (중요!)
                if(filePathCallbackLollipop !=null){
                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop =null;
                }

                filePathCallbackLollipop =filePathCallback;

                // 코드 추가 START

                //Create AndroidExampleFolder at sdcard
                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }

                // Create camera captured image file path and name
                File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                mCapturedImageURI = Uri.fromFile(file);

                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                // 코드 추가 END


                Intent i =new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");

                //코드 추가 START


                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});


                //코드추가 END
                startActivityForResult(Intent.createChooser(i,"File Chooser"),FILECHOOOSER_LOLLIPOP_REQ_CODE);
                return true;
            }
        });
    }


    public void getNativeContacts() {
        Log.d("JJKIM", "Native영역 전화번호부 정보 가져오기");
        Contacts contacts = new Contacts();
        jsonContacts = contacts.getContacts();

        handler.post(new Runnable() {
            @Override
            public void run() {
                String args = null;
                if(jsonContacts != null) args = jsonContacts.toString();
                Log.d("JJKIM", "jsonContacts = "+ args);
                web.loadUrl("javascript:printContacts('" + args + "')"); // 해당 url의 자바스크립트 함수 호출
            }
        });
    }

    /* 안드로이드와 자바스크립트간의 데이터 주고 받기 */
    public class WebAppInterface {
        Context mContext;


        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /* 페이지 이동*/
        @JavascriptInterface
        public void movePage(final String url, final String json) {
            Log.d("HYJ","아니여기까지 안들어오세여?????");
            handler.post(new Runnable() {
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
                    web.loadUrl("file:///android_asset/html/" + url); // url로 페이지 이동
                }
            });
        }

        /* 전화번호부 가져오기*/
        @JavascriptInterface
        public void callContacts() {
            Log.d("JJKIM", "Web JS에서 MainActivity쪽 function 호출");
            getNativeContacts();
        }

        /* 푸시알림*/
        @JavascriptInterface
        public void callPush() {
            Log.d("JJKIM", "push 메소드 호출");
        }

        /* 전화걸기 */
        @JavascriptInterface
        public void callPhone(String phoneNum) {
            Log.d("JJKIM", "[전화걸기] 전화번호 : "+ phoneNum);
            Contacts contacts = new Contacts();
            contacts.callPhone(phoneNum);
        }

        //주소록 저장
        @JavascriptInterface
        public void saveToOldContact(final String jsonDataTest) {
            Intent contactIntent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT);

            if (jsonDataTest != null) {
                try {
                    jsonData = new JSONObject(jsonDataTest);

                    contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, jsonData.getString("name"));
                    contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, jsonData.getString("cellPhone"));
                    contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    contactIntent.setData(Uri.fromParts("tel", jsonData.getString("cellPhone"), null));

                    contactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, jsonData.getString("email"));
                    contactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
                    contactIntent.setData(Uri.fromParts("mailto", jsonData.getString("email"), null));

                    startActivity(contactIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @JavascriptInterface
        public String returnSessionId(){

            SharedPreferences pref = myApp.getSharedPreferences("sessionCookie",Context.MODE_PRIVATE);
            String sessionid = pref.getString("sessionid",null);
            sessionid = sessionid.substring(11);

            return sessionid;
        }

    }


    /*파일 가져온 후 결과값을 처리하기 위한 부분
     startActivityForResult 를 통해서 엑티비티의 이동을 구현하고 이동한 엑티비티가 종료되면
     onActivityResult 를 호출하도록 한다. 이 과정에서 여러 결과값들을이나 코드들을 넘겨받을 수 있는데,
     위에서 카메라나 사진첩을 호출할 때 startActivityForResult 를 통해서 구현하였다.
     그 말인 즉슨 startActivityForResult 를 통해서 카메라나 사진첩을 호출하고
     onActivityResult 에서 결과값으로 사진이나 이미지를 넘겨받는 것이다.
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d("HYJ:","들어왔는지:"+requestCode);
        if(requestCode == FILECHOOSER_RESULTCODE){
            if(null == mUploadMessage){
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage =null;
        }else if(requestCode == FILECHOOOSER_LOLLIPOP_REQ_CODE){
            Log.d("HYJ:","들어왔는지:"+requestCode);
            if(filePathCallbackLollipop ==null){
                return;
            }
            filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,intent));
            filePathCallbackLollipop=null;
        }

    }

    /* 물리 백버튼키 처리 */
    @Override
    public void onBackPressed() {
        WebBackForwardList list = web.copyBackForwardList(); // 누적된 history 를 저장할 변수
        if (list.getCurrentIndex() <= 0 && !web.canGoBack()) { // 처음 들어온 페이지이거나, history 가 없는 경우
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    //.setTitle("Exit!")
                    .setMessage("Do you want to exit the application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        } else { // history 가 있는 경우
            if (web.canGoBack()) {
                web.goBack();
            }
            web.clearHistory(); // history 삭제
        }
    }
}