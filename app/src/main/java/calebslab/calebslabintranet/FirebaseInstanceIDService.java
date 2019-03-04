/************************************************************************
 *  업무구분명: 디바이스 토큰 생성
 *  세부업무구분명: 디바이스 토큰 생성
 *  작성자: 남기완
 *  설명: 1) 디바이스 토큰 생성
 *  ------------------------------------------------
 *  변경이력
 *  ------------------------------------------------
 *  NO   날짜          		       작성자       내용
 *  1   2019-03-04                 남기완      신규생성
 **************************************************************************/
package calebslab.calebslabintranet;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInstanceIDService";

    @Override
    public void onTokenRefresh() {
        // 설치할때 여기서 토큰을 자동으로 만들어 준다
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // 생성한 토큰을 서버로 날려서 저장하기 위해서 만든거
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {

    }

}