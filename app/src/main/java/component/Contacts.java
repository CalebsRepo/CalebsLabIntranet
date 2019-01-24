package component;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* 작성자 : 김정진
 * Native 전화번호부에서 이름과 전화번호 가져오기
 * 참고한 블로그 : http://mainia.tistory.com/4924 [녹두장군 - 상상을 현실로]
 */
public class Contacts extends AppCompatActivity {

    public JSONArray getContacts() {
        ArrayList<Map<String, String>> dataList;
        JSONArray jsonContacts = null; // 전화번호부에서 이름, 전화번호를 받아와 jsonarray형태로 변환 후 web단으로 넘기기 위한 변수

        dataList = new ArrayList<Map<String, String>>();
        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc");

        while (c.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            // 연락처 id 값
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            // 연락처 대표 이름
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            map.put("name", name);

            // ID로 전화 정보 조회
            Cursor phoneCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null, null);

            // 데이터가 있는 경우
            if (phoneCursor.moveToFirst()) {
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                map.put("phone", number);
            }

            phoneCursor.close();
            dataList.add(map);
        }// end while
        c.close();

        // JSON 으로 변환
        try {
            jsonContacts = new JSONArray();//배열이 필요할때
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject tempContacts = new JSONObject();//배열 내에 들어갈 json
                tempContacts.put("name", dataList.get(i).get("name"));
                tempContacts.put("phonenum", dataList.get(i).get("phone"));
                jsonContacts.put(tempContacts);
            }

            Log.d("JSON Test", jsonContacts.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        출처: http://mainia.tistory.com/5673 [녹두장군 - 상상을 현실로]

        return jsonContacts;
    }

    public Contacts() {
        super();
    }

    public void callPhone(String phoneNum) {
        String pNum;
        pNum = phoneNum.replaceAll("-","");
        Log.d("JJKIM","Contacts클래스 callPhone()메소드 전화번호 : " + pNum);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + pNum));
        startActivity(intent);
    }
}
