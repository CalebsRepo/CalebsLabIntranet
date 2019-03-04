package calebslab.calebslabintranet;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginService {

    private static String loginData;

    public String login(String id, String pwd, String sndUrl, Activity mContext){

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
                        while ((line = reader.readLine()) != null) {
                            output += line;
                        }
                        //전달받은 세션 아이디 android preference에 저장
                        sm.getCookieHeader(conn, mContext);

                        loginData = output;

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

        try {

            t1.join();

        }catch (InterruptedException e){

            t1.interrupt();
        }

        return loginData;

    }

    }
