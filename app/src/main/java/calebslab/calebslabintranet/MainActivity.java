package calebslab.calebslabintranet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.ArrayList;
import java.util.List;


import component.Contacts;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    final Context myApp = this;

    WebView web; // 웹뷰 선언
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

    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility"})
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

        web.setWebViewClient(new android.webkit.WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //************************************************************************
                //  날짜: 20190213
                //  만든이: HYJ
                //  내용: 외부 앱 호출 START
                //        전화 또는 메일, 또는 그 외 앱 호출시 loadUrl 타지않고 호출하는 앱으로 연결
                //        intent의 경우 Manifest에서 intent scheme 이용해서 사용해야함
                //************************************************************************
                if (url.startsWith(("tel:")) || url.startsWith("mailto:") || url.startsWith("intent:")) {
                    if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(intent);
                    } else if (url.startsWith("mailto:")) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(intent);
                    } else {
                        try {
                            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                            Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                            if (existPackage != null) {
                                startActivity(intent); //앱 충돌?????????? 마켓연결은 되는데 실제 외부 앱 연동이 안됨. 이거 나중에 처리하자
                            } else {
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                                startActivity(marketIntent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };
                    return true;
                }
                //************************************************************************
                //  날짜: 20190213
                //  만든이: HYJ
                //  내용: END
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

        // 휴가내역 엑셀 다운로드
        @JavascriptInterface
        public void saveExcel(final String result){
            Log.d("이승환", "result>>>>>>>>>>>>>>>"+result);
//            int idx = result.indexOf("@");
//            String result1 =  "";
//            String result2 =  "";
//            result1 = result.substring(0, idx);
//            result2 = result.substring(idx+1);
//            Log.d("이승환", "result11>>>>>>>>>>>>>>>"+result1);
//            Log.d("이승환", "result22>>>>>>>>>>>>>>>"+result2);

            Workbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("휴가현황"); // 새로운 시트 생성

            Row row = sheet.createRow(0); // 새로운 행 생성
            Cell cell;

            cell = row.createCell(0); // 1번 셀 생성
            cell.setCellValue("No."); // 1번 셀 값 입력

            cell = row.createCell(1); // 2번 셀 생성
            cell.setCellValue("아이디"); // 2번 셀 값 입력

            cell = row.createCell(2); // 3번 셀 생성
            cell.setCellValue("이름"); // 3번 셀 값 입력

            cell = row.createCell(3); // 4번 셀 생성
            cell.setCellValue("직급"); // 4번 셀 값 입력

            cell = row.createCell(4); // 5번 셀 생성
            cell.setCellValue("입사일"); // 5번 셀 값 입

            cell = row.createCell(5); // 6번 셀 생성
            cell.setCellValue("근속기간"); // 6번 셀 값 입

            cell = row.createCell(6); // 7번 셀 생성
            cell.setCellValue("근무년차"); // 7번 셀 값 입


            if (result != null) {
                try {
                    JSONArray arr = new JSONArray(result);   // 사원정보
                    Log.d("이승환", "arr>>>>>>>>>>>>>>>"+arr);
                    for(int i=0; i < arr.length(); i++) {
                        JSONObject jObject = arr.getJSONObject(i);
                        Log.d("이승환", "arr>>>>>>>>>>>>>>>"+jObject);

                        // 직원수 2번데이터에만 있는컬럼 값 null아닐경우로 해도 될듯
                        if(i< 11){
                            String id = jObject.getString("id");
                            String name = jObject.getString("name");
                            String joinYear = jObject.getString("join_year");
                            String joinMon = jObject.getString("work_mon");
                            String hsYmd =  jObject.getString("hs_ymd");
                            String heYmd =  jObject.getString("he_ymd");
                            String workingDate = "";

                            if (Integer.parseInt(joinYear) > 0) {
                                workingDate = joinYear +"년" +joinMon + "개월";
                            }else {
                                workingDate = joinMon + "개월";
                            }

                            row = sheet.createRow(i+1); // 행추가
                            cell = row.createCell(0);
                            cell.setCellValue(i+1);
                            cell = row.createCell(1);
                            cell.setCellValue(jObject.getString("id"));
                            cell = row.createCell(2);
                            cell.setCellValue(jObject.getString("name"));
                            cell = row.createCell(3);
                            cell.setCellValue(jObject.getString("grade"));
                            cell = row.createCell(4);
                            cell.setCellValue(jObject.getString("join_ymd"));
                            cell = row.createCell(5);
                            cell.setCellValue(workingDate);
                            cell = row.createCell(6);       // 근무년차 : 연차발생기간 ~
                            cell.setCellValue(hsYmd+ "~" + heYmd);
                        } else {
                            // 아이디 비교
                            // 기간



                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//
//                    JSONArray arr = new JSONArray(result1);   // 사원정보
//                    Log.d("이승환", "arr1>>>>>>>>>>>>>>>"+arr);
//
//                    JSONArray arr2 = new JSONArray(result2);   // 근무년차별 사용내역
//                    Log.d("이승환", "arr2>>>>>>>>>>>>>>>"+arr2);
//
//                    for(int i=0; i < arr.length(); i++) {
//
//                        JSONObject jObject = arr.getJSONObject(i);  // JSONObject 추출
//                        JSONObject jObject2 = arr2.getJSONObject(i);  // JSONObject 추출
//
//                        /* 사원정보 관련*/
//                        String id = jObject.getString("id");
//                        String name = jObject.getString("name");
//                        String joinYear = jObject.getString("join_year");
//                        String joinMon = jObject.getString("work_mon");
//                        String hsYmd =  jObject.getString("hs_ymd");
//                        String heYmd =  jObject.getString("he_ymd");
//                        String workingDate = "";
//
//                        if (Integer.parseInt(joinYear) > 0) {
//                            workingDate = joinYear +"년" +joinMon + "개월";
//                        }else {
//                            workingDate = joinMon + "개월";
//                        }
//
//                        row = sheet.createRow(i+1); // 행추가
//                        cell = row.createCell(0);
//                        cell.setCellValue(i+1);
//                        cell = row.createCell(1);
//                        cell.setCellValue(jObject.getString("id"));
//                        cell = row.createCell(2);
//                        cell.setCellValue(jObject.getString("name"));
//                        cell = row.createCell(3);
//                        cell.setCellValue(jObject.getString("grade"));
//                        cell = row.createCell(4);
//                        cell.setCellValue(jObject.getString("join_ymd"));
//                        cell = row.createCell(5);
//                        cell.setCellValue(workingDate);
//                        cell = row.createCell(6);       // 근무년차 : 연차발생기간 ~
//                        cell.setCellValue(hsYmd+ "~" + heYmd);
//
//
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

            File xlsFile = new File(getExternalFilesDir(null),"test.xls");
            try{
                FileOutputStream os = new FileOutputStream(xlsFile);
                workbook.write(os); // 외부 저장소에 엑셀 파일 생성
            }catch (IOException e){
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(),xlsFile.getAbsolutePath()+"에 저장되었습니다",Toast.LENGTH_SHORT).show();

            Uri path = Uri.fromFile(xlsFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/excel");
            shareIntent.putExtra(Intent.EXTRA_STREAM,path);
            startActivity(Intent.createChooser(shareIntent,"엑셀 내보내기"));
        }




        //로그인 통신
        @JavascriptInterface
        public String callLogin(final String id, final String pwd, final String sndUrl) throws Exception {

            userData ="";

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {

                    try{
                        URL url;
                        HttpURLConnection conn;
                        DataOutputStream wr;
                        String callUrl = sndUrl +"login.json";

                        url = new URL(callUrl);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(10000);
                        conn.setRequestMethod("POST");
                        conn.connect();
                        SessionManager sm = new SessionManager();

                        String param = "id=" + id + "&pwd=" + pwd;

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
                            String output = "";
                            String line;
                            System.out.println("reader 전");
                            while ((line = reader.readLine()) != null) {
                                output += line;
                            }

                            System.out.println(conn.getHeaderField("Set-Cookie"));
                            sm.getCookieHeader(conn, myApp);

                            userData = output;

                        }
                        conn.disconnect();
                    } catch(MalformedURLException e){
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                }
            });

            t1.start();
            t1.join();

            return userData;
        }

        //로그인 통신
        @JavascriptInterface
        public String callLogout(final String sndUrl) throws Exception {
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

                        wr = new DataOutputStream(conn.getOutputStream());
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
                            Log.d("logout result", userData);
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


        @JavascriptInterface
        public String returnSessionId() {

            SharedPreferences pref = myApp.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
            String sessionid = pref.getString("sessionid", null);
            sessionid = sessionid.substring(11);

            return sessionid;
        }


        //복호화
        @JavascriptInterface
        public String pwdTest(final String pwd) throws Exception {

            pwdTest ="";

            Thread t3 = new Thread(new Runnable() {
                @Override
                public void run() {

                    try{
                        URL url;
                        HttpURLConnection conn;
                        DataOutputStream wr;
                        String callUrl = "http://192.168.10.157:8080/Caleb/pwdDecrypt.json";

                        url = new URL(callUrl);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(10000);
                        conn.setRequestMethod("POST");
                        //해더 타입. 호출하려는 곳과 해더 타입이 맞지 않으면 오류가 발생.
                        // 호출하려는 곳에서 xml,  json, html, text 등 리턴하는 타입을 확인하여 작성해야함
                        conn.connect();
                        SessionManager sm = new SessionManager();

                        String param = "pwd=" + pwd;

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
                            String output = "";
                            String line;
                            System.out.println("reader 전");
                            while ((line = reader.readLine()) != null) {
                                output += line;
                            }

                            System.out.println(conn.getHeaderField("Set-Cookie"));
                            sm.getCookieHeader(conn, myApp);

                            pwdTest = output;

                        }
                        conn.disconnect();
                    } catch(MalformedURLException e){
                        e.printStackTrace();
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                }
            });

            t3.start();
            t3.join();

            return pwdTest;
        }
        //************************************************************************
        //  날짜: 20190212
        //  만든이: HYJ
        //  내용: 웹뷰에서 이미지 주소 얻기, FTP 파일 전송 이벤트 시작
        //************************************************************************
        /* 이미지 Array 찾아오기 */
        @JavascriptInterface
        public void imgPath(final int imgNum, final String imageInfo) {
            Log.d("현재 올리고자하는 이미지 정보는용:",imageInfo);
            if(imgNum==1) {
                /*첫 호출시 imageInfo(프로필,사인여부)를 저장해놓는다*/
                imgRealPath.set(0,imgRealPath.get(0).toString()+":"+imageInfo);
            }else if((imgNum==2)) {
                /*만약에 첫번째 index의 imageInfo(프로필인지, 사인인지 여부)가  두번째 index의 imageInfo와 똑같다면,
                  이는 갤러리를 한번 더 요청해서 이미지를 바꾸려고한것이므로, 두번째 index를 지우고 해당 정보를 첫번째 index에 넣는다*/
                if(imgRealPath.get(0).toString().contains(imageInfo)) {
                    imgRealPath.set(0, imgRealPath.get(1).toString() + ":" + imageInfo);
                    imgRealPath.remove(1);
                }else{
                    imgRealPath.set(1, imgRealPath.get(1).toString() + ":" + imageInfo);
                }
            }else{
                if(imgRealPath.get(0).toString().contains(imageInfo)) {
                    Log.d("imgRealPath2:",imgRealPath.get(0).toString());
                    imgRealPath.set(0,imgRealPath.get(2).toString()+":"+imageInfo);
                    imgRealPath.remove(2);
                }else{
                    imgRealPath.set(1,imgRealPath.get(2).toString()+":"+imageInfo);
                    imgRealPath.remove(2);
                }
            }
        }

        @JavascriptInterface
        public void imgFtpSend() {
            checkPermissions();
            NThread nThread = new NThread();
            nThread.start();
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

        /* getNativePushToken() 호출 */
        @JavascriptInterface
        public void pushToken() {
            Log.d("GWNAM", "Web JS에서 MainActivity쪽 function 호출");
            getNativePushToken();
        }

        /* PUSH 토큰 보내기 */
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



    //************************************************************************
    //  날짜: 20190212
    //  만든이: HYJ
    //  내용: FTP 이미지 전송 START
    //       사용자 권한획득, 스레드, FTP 전송, 이미지 실제경로 method
    //************************************************************************

    /*   Handler handler = new Handler();
     */
    /*********  work only for Dedicated IP ***********/
    static final String FTP_HOST= "iup.cdn3.cafe24.com";

    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "calebslab1";

    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS  ="wjswls!1";

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};//권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101;//권한 동의 여부 문의 후 callback함수에 쓰일 변수


    //사용권한 묻는 함수
    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);//현재 컨텍스트가 pm 권한을 가졌는지 확인
            if (result != PackageManager.PERMISSION_GRANTED) {//사용자가 해당 권한을 가지고 있지 않을 경우
                permissionList.add(pm);//리스트에 해당 권한명을 추가한다
            }
        }
        if (!permissionList.isEmpty()) {//권한이 추가되었으면 해당 리스트가 empty가 아니므로, request 즉 권한을 요청한다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //권한 요청의 콜백 함수. PERMISSION_GRANTED 로 권한을 획득하였는지를 확인할 수 있다.
    //아래에서는 !=를 사용했기에 권한 사용에 동의를 안했을 경우를 h 사용해서 코딩.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    //권한 획득에 동의하지 않았을 경우, 아래 메시지를 띄우며 해당 액티비티를 종료.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class NThread extends Thread{
        public NThread() {
        }
        @Override

        public void run() {

            for(int i=0; i<imgRealPath.size(); i++){
                upload(i);
            }
            /*스레드가 끝나면 인덱스 clear시킨다*/
            imgRealPath.clear();
        }

        public void upload(int i){
            /********** Pick file from memory *******/
            //장치로부터 메모리 주소를 얻어낸 뒤, 파일명을 가지고 찾는다.
            //현재 이것은 내장메모리 루트폴더에 있는 것.

            String filePathOrg=imgRealPath.get(i).toString();

            String filePath=filePathOrg.substring(1,filePathOrg.lastIndexOf(":"));
            String fileInfo=filePathOrg.substring(filePathOrg.lastIndexOf(":")+1);

            File f = new File(filePath);
            Log.d("파일 존재하는 디렉토리:",f.getParent().toString());
            Log.d("파일 이름",f.getName());;
            // Upload file

            File convFile = new File(f.getParent() + "/"+"hyjlm92.png");
            f.renameTo(convFile);
            uploadFile(f, fileInfo);
        }
    }

    public void uploadFile(File fileName,String fileInfo){

        FTPClient client = new FTPClient();

        try {
            client.connect(FTP_HOST,21);//ftp 서버와 연결, 호스트와 포트를 기입
            client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
            client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
            client.changeDirectory("/www/imployeeInfo/"+fileInfo);//서버에서 넣고 싶은 파일 경로를 기입

            client.upload(fileName, new MyTransferListener());//업로드 시작

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"성공",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                }
            });

            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    // progress 보여주기 => 추후에 사용할지 여부 결정
    private class MyTransferListener implements FTPDataTransferListener {
        public void started() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Transfer started
                    Toast.makeText(getBaseContext(), "파일 업로드 시작", Toast.LENGTH_SHORT).show();
                    //System.out.println(" Upload Started ...");
                }
            });
        }
        public void transferred(int length) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Yet other length bytes has been transferred since the last time this
                    // method was called
                    Toast.makeText(getBaseContext(), "파일 전송 중", Toast.LENGTH_SHORT).show();
                    //System.out.println(" transferred ..." + length);
                }
            });
        }
        public void completed() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Transfer completed
                    Toast.makeText(getBaseContext(), "파일 전송 완료", Toast.LENGTH_SHORT).show();
                    //System.out.println(" completed ..." );
                }
            });
        }
        public void aborted() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Transfer aborted
                    Toast.makeText(getBaseContext(),"파일전송 중단 ,다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    //System.out.println(" aborted ..." );
                }
            });
        }
        public void failed() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("파일전송 실패" );
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
        Log.d("파일 실제경로:", cursor.getString(columnIndex));

        imgRealPath.add(cursor.getString(columnIndex));
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