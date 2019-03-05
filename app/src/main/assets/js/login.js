    /**
    * 내용 : 로그인
    * param id : 아이디, pwd : 패스워드
    * return : 로그인 성공 시 String userData, 실패 시 null
    **/
    function callLogin(id, pwd) {

       login_result = window.temp.callLogin(id, pwd, sndUrl);

       return login_result;
    }

    /**
    * 내용 : 로그아웃
    * return : 로그인 실패 시 false 리턴, 성공 시 login.html로 이동
    **/
    function callLogout(){

            loginData = JSON.parse(getUserData());
            id = loginData.id;
            logout_result = window.temp.callLogout(sndUrl, id);



            if(logout_result == "success"){
                sessionStorage.removeItem("userData");
                movePage("login.html");
            }else{
                alert("로그아웃 실패!");
                return false;
            }


    }

    /**
    * 내용 : 프리퍼런스에 저장된 세션아이디 가져오기
    * return : 문자열 형식의 세션id를 가져온다
    **/
    function returnSessionId(){

        sessionId = window.temp.returnSessionId();

        return sessionId;
    }
