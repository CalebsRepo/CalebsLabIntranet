package calebslab.calebslabintranet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.JsonToken;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


import component.Contacts;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {
    /* 엑셀 파일 저장 관련 변수 */
    Context context;
    File xlsFile;


    final Context myApp = this;
    WebView web; // 웹뷰 선언
    WebViewInterface webViewInterface;  //웹뷰 javascript interface 선언
    JSONObject jsonData; // 자바스크립트에서 값을 받을 json 변수 선언
    JSONArray jsonContacts; // 전화번호부에서 이름, 전화번호를 받아와 jsonarray형태로 변환 후 web단으로 넘기기 위한 변수

    /* HYJ
      사용하고 있는 변수들
     */
    //갤러리용
    private  static  final int FILECHOOSER_RESULTCODE =1;
    private final static int FILECHOOOSER_LOLLIPOP_REQ_CODE=2;
    private ValueCallback<Uri> mUploadMessage =null;
    private ValueCallback<Uri[]> filePathCallbackLollipop;
    private Uri mCapturedImageURI;
    private static String userData;
    private static String pwdTest;

    //호출한 이미지들 실제경로 배열로 저장
    private String[] salesTeamArray;
    private ArrayList imgRealPath = new ArrayList();

    private final Handler handler = new Handler();
    private Object JsonToken;
    OnSwipeTouchListener onSwipeTouchListener;
    private String urlPath = "file:///android_asset/html/";
    public MyProgressDialog progressDialog;

    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //세로모드 고정
        web = (WebView) findViewById(R.id.webview); //웹뷰 선언
        webViewInterface = new WebViewInterface(MainActivity.this, web);//임시
        web.setWebViewClient(new WebViewClient());

        // 웹뷰 세팅
        web.addJavascriptInterface(webViewInterface, "temp"); //임시
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
        webSet.setDomStorageEnabled(true); //로컬스토리지 사용 허용

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.setWebContentsDebuggingEnabled(true);             //API 레벨 21부터 이용 가능.
        }
        web.addJavascriptInterface(new WebAppInterface(this), "android");

        web.loadUrl("file:///android_asset/html/login.html"); // 처음 로드할 페이지

        web.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
            }
            @Override
            public void onSwipeRight() {
            }
        });

        web.setWebViewClient(new WebViewClient() {

            //************************************************************************
            //  날짜: 20190304
            //  만든이: 이승환
            //  내용: 웹뷰 페이지 이동시 로딩아이콘 설정
            //        로그인화면으로 이동시에는 제외
            //        로딩아이콘 노출시간은 0.6초
            //************************************************************************
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!web.getUrl().equals("file:///android_asset/html/login.html")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog = MyProgressDialog.show(myApp,"","",true,true,null);
                            handler.postDelayed( new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (progressDialog!=null&&progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                    }
                                    catch ( Exception e ) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 600);
                        }
                    } );
                    super.onPageStarted(view, url, favicon);
                }
            }
            /**
             * 내용 : 웹뷰 내 링크 터치 시 새로운 창이 뜨지 않고 해당 웹뷰 안에서 새로운 페이지가 로딩되도록 함
             * param view : 대상 WebView 객체 , url : 이동 대상 url
             * return : boolean true
             **/
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //************************************************************************
                //  날짜: 20190213
                //  만든이: HYJ
                //  내용: 외부 앱 호출
                //        전화 또는 메일, 또는 그 외 앱 호출시 loadUrl 타지않고 호출하는 앱으로 연결
                //        intent의 경우 Manifest에서 intent scheme 이용해서 사용해야함
                //************************************************************************
                if (url.startsWith(("tel:")) || url.startsWith("mailto:")) {
                    if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(intent);
                    } else{
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(intent);
                    }
                    return true;
                }
                //************************************************************************
                //  END
                //************************************************************************

                Log.d("MovePage", "이동 대상 URL  : " + url);
                view.loadUrl(url);
                Log.d("MovePage", "이동 완료");
                return true;

            }
        });

        //************************************************************************
        //  날짜: 20190212
        //  만든이: 공통
        //  내용: 크롬 클라이언트 생성
        //************************************************************************
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

            //************************************************************************
            //  날짜: 20190212
            //  만든이: HYJ
            //  내용: 웹뷰에서 갤러리 open, 이미지 선택 START
            //      => 웹뷰에서는 버전별로 파일첨부 코드가 필요하다
            //        Android 5.0 이후 버전에서는 onShowFileChooser를 이용해서 파일선택을 할수 있다
            //        그 이전 버전 확인은 http://acorn-world.tistory.com/62에서 확인가능하다
            //************************************************************************
            public  boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                              FileChooserParams fileChooserParams){
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
                //doFileUpload();
                return true;
            }
            //************************************************************************
            //  날짜: 20190212
            //  만든이: HYJ
            //  내용: 웹뷰에서 갤러리 open, 이미지 선택 END
            //************************************************************************
        });
    }

    //************************************************************************
    //  날짜: 20190212
    //  만든이: 공통
    //  내용: 안드로이드와 자바스크립트간의 데이터 주고 받기
    //************************************************************************

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

        /**
         * 내용 : 로그아웃
         * param sndUrl : 대상 서버 URL, id : 로그아웃 아이디
         * return : 로그아웃 성공 시 "success" 실패 시 "fail" 반환
         **/
        @JavascriptInterface
        public String callLogout(final String sndUrl, final String id) throws Exception {
            userData ="";
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {

                    try{
                        URL url;
                        HttpURLConnection conn;
                        DataOutputStream wr;

                        String callUrl = sndUrl +"logOut.json";

                        url = new URL(callUrl);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(10000);
                        conn.setRequestMethod("POST");
                        conn.setDefaultUseCaches(false);
                        conn.setUseCaches(false);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        SessionManager sm = new SessionManager();
                        sm.setCookieHeader(myApp, conn);

                        String param = "id=" + id;

                        wr = new DataOutputStream(conn.getOutputStream());
                        wr.writeBytes(param);
                        wr.flush();
                        wr.close();

                        Log.d("LOG", url + "로 HTTP 요청 전송");

                        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) { //이때 요청이 보내짐.

                            Log.d("LOG", "HTTP_OK를 받지 못했습니다.");

                        } else {

                            InputStream in = new BufferedInputStream(conn.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String result ="";
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result += line;
                            }
                            userData = result;
                        }
                        conn.disconnect();
                    } catch(MalformedURLException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }

                }
            });
            t2.start();
            t2.join();

            return userData;
        }

        /**
         * 내용 : android preference에 저장된 세션 아이디 반환
         * return : 세션 아이디 반환
         **/
        @JavascriptInterface
        public String returnSessionId() {

            SharedPreferences pref = myApp.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
            String sessionId = pref.getString("sessionid", null);
            sessionId = sessionId.substring(11);

            return sessionId;
        }

        //************************************************************************
        //  날짜: 20190212
        //  만든이: HYJ
        //  내용: 웹뷰에서 주소록 앱 연결
        //************************************************************************
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

        //************************************************************************
        //  날짜: 20190213
        //  만든이: 남기완
        //  내용: login.html에서 pushToken() 호출하여 getNativePushToken()을 호출한다.
        //************************************************************************
        @JavascriptInterface
        public void pushToken() {
            Log.d("GWNAM", "Web JS에서 MainActivity쪽 function 호출");
            getNativePushToken();
        }

        //************************************************************************
        //  날짜: 20190213
        //  만든이: 남기완
        //  내용: login.html로 생성된 push토큰을 전송한다.
        //************************************************************************
        public void getNativePushToken() {
            Log.d("GWNAM", "Native영역 Token 정보 가져오기");
            JsonToken = FirebaseInstanceId.getInstance().getToken();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    String args = null;
                    if(JsonToken != null) args = JsonToken.toString();
                    Log.d("GWNAM", "jsonContacts = "+ args);
                    web.loadUrl("javascript:getToken('" + args + "')"); // 해당 url의 자바스크립트 함수 호출
                }
            });
        }

    }


    /*
    아래 코드가 있어야 웹뷰에서도 갤러리에 접근해서 이미지를 가져와서 웹뷰의 첨부파일로 입력이 완료됩

   => 파일 가져온 후 결과값을 처리하기 위한 부분
   startActivityForResult 를 통해서 엑티비티의 이동을 구현하고 이동한 엑티비티가 종료되면
   onActivityResult 를 호출하도록 한다. 이 과정에서 여러 결과값들이나 코드들을 넘겨받을 수 있는데,
   위에서 카메라나 사진첩을 호출할 때 startActivityForResult 를 통해서 구현하였다.
   그 말인 즉슨 startActivityForResult 를 통해서 카메라나 사진첩을 호출하고
   onActivityResult 에서 결과값으로 사진이나 이미지를 넘겨받는 것이다.
  */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
            getPath(result);
        } else if (requestCode == FILECHOOOSER_LOLLIPOP_REQ_CODE) {
            if (filePathCallbackLollipop == null) {
                return;
            }
            filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            filePathCallbackLollipop = null;
        }
        Uri urlTest;
        if(intent!=null) {
            urlTest = intent.getData();
            getPath(urlTest);
        }
    }

    /*실제경로 구하기*/
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        //Log.d("파일 실제경로:", cursor.getString(columnIndex));

        //imgRealPath.add(cursor.getString(columnIndex));
        return cursor.getString(columnIndex);
    }

    //************************************************************************
    //  날짜: 20190212
    //  만든이: HYJ
    //  내용: FTP 이미지 전송 END
    //************************************************************************

    /* 물리 백버튼키 처리 */
    @Override
    public void onBackPressed() {
        WebBackForwardList list = web.copyBackForwardList(); // 누적된 history 를 저장할 변수
        if (list.getCurrentIndex() <= 0 && !web.canGoBack() || web.getUrl().equals(urlPath + "login.html")
                || web.getUrl().equals(urlPath + "main.html")) { // 처음 들어온 페이지이거나, history 가 없는 경우, 로그인/index 페이지
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
            // history 가 있는 경우
        } else if(web.getUrl().equals(urlPath + "projectList.html")|| web.getUrl().equals(urlPath + "employeeInfo.html")
                || web.getUrl().equals(urlPath + "planList.html")|| web.getUrl().equals(urlPath + "qnaDetail.html")
                || web.getUrl().equals(urlPath + "holidayList.html")) {
                web.goBackOrForward(-(list.getCurrentIndex()) + 1);
            }else {
                web.goBack();
                web.clearHistory(); // history 삭제
        }

        }
    }

