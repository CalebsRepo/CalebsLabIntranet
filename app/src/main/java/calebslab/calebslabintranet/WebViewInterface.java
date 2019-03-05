package calebslab.calebslabintranet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class WebViewInterface {

    private WebView mAppView;
    private Activity mContext;
    private static String userData;
    /* 엑셀 파일 저장 관련 변수 */
    Context context;
    File xlsFile;

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

    /* 연차사용내역 엑셀 파일 생성 및 공유 */
    @JavascriptInterface
    public void saveExcel(final String result){
        Toast.makeText(mContext.getApplicationContext(),"Excel 파일을 생성 중 입니다.",Toast.LENGTH_SHORT).show();

        holidayConditionExcel he = new holidayConditionExcel();
        Workbook workbook = he.makeExcel(result);

        // 저장할 폴더 및 파일명
        xlsFile = new File(mContext.getExternalFilesDir(null),"휴가사용내역.xls");

        try{
            FileOutputStream os = new FileOutputStream(xlsFile);
            workbook.write(os);
        }catch (IOException e){
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) { // Android Nougat ( 7.0 ) and later
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider",xlsFile);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setDataAndType(uri, "application/excel");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            mContext.startActivity(Intent.createChooser(intent,"엑셀 내보내기"));

        } else {
            Uri uri = Uri.fromFile(xlsFile);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/excel");
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            mContext.startActivity(Intent.createChooser(intent,"엑셀 내보내기"));
        }
    }

}
