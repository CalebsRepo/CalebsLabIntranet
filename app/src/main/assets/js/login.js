document.write('<script type="text/javascript" src="https://code.jquery.com/jquery-1.11.1.min.js"><\/script>')
document.write('<script type="text/javascript" src="../js/common_val.js"><\/script>')
document.write('<script type="text/javascript" src="../js/and_interface.js"><\/script>')
document.write('<script type="text/javascript" src="../js/common_util.js"><\/script>')
//로그인
function callLogin(id, pwd) {

   login_result = window.android.callLogin(id, pwd, sndUrl);

   return login_result;
}
//로그아웃
function callLogout(){

        logout_result = window.android.callLogout(sndUrl);

        if(logout_result == "success"){
            movePage("login.html");
        }else{
            alert("로그아웃 실패!");
            return false;
        }


}
//프리퍼런스에 저장된 세션아이디를 가져온다
function returnSessionId(){

    sessionId = window.android.returnSessionId();

    return sessionId;
}
