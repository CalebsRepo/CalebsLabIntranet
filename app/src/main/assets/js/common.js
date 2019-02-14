    document.write('<script type="text/javascript" src="https://code.jquery.com/jquery-1.11.1.min.js"><\/script>')
    document.write('<script type="text/javascript" src="../js/common_val.js"><\/script>')
    document.write('<script type="text/javascript" src="../js/and_interface.js"><\/script>')
    document.write('<script type="text/javascript" src="../js/common_util.js"><\/script>')

    /**
     * 페이지 이동
     */
    function movePage(url, param) {
		console.log("movePage: "+url);
        if(url !=null && param != null){
            setStorageItem(temp_key,param);
        }
        location.href=url;
    }

    /**
     * 서버호출
     */
    function navigate(param1, param2, param3, param4) {

        sessionId = window.android.returnSessionId();
        //JSON 데이터가 없을 시
        if(arguments.length == 2) {
            $.ajax({
                url : sndUrl + param1, type : "post", dataType: "json",
                beforeSend : function(xmlHttpRequest){
                                    xmlHttpRequest.setRequestHeader("AJAX", "true");
                                    xmlHttpRequest.setRequestHeader("sessionId", sessionId);
                                },
                success : param2,
                error:function(xhr, textStatus, error){

                        if(xhr.status=="400")
                        {

                        alert("세션이 만료되었습니다.");

                        movePage("login.html");

                        }else{

                            alert("통신 중 문제가 발생하였습니다.");

                        }
                    }
            });
        } else if(arguments.length == 4){
        //ajax 동기로 사용할 때
        //parameter("",url, success함수, async값)
            $.ajax({
                url : sndUrl + param2, type : "post", dataType: "json", async:param4,
                beforeSend : function(xmlHttpRequest){
                                    xmlHttpRequest.setRequestHeader("AJAX", "true");
                                    xmlHttpRequest.setRequestHeader("sessionId", sessionId);
                                },
                success : param3,
                error:function(xhr, textStatus, error){

                        if(xhr.status=="400")
                        {

                        alert("세션이 만료되었습니다.");

                        movePage("login.html");

                        }else{
                        alert("통신 중 문제가 발생하였습니다.");
                        }
                    }
            });
          }else {
        //JSON 데이터가 있을 시
            $.ajax({
                url : sndUrl + param2, type : "post", dataType: "json", data : param1,
                beforeSend : function(xmlHttpRequest){
                                    xmlHttpRequest.setRequestHeader("AJAX", "true");
                                    xmlHttpRequest.setRequestHeader("sessionId", sessionId);
                                },
                success : param3,
                error:function(xhr, textStatus, error){

                    if(xhr.status=="400")
                    {

                    alert("세션이 만료되었습니다.");

                    movePage("login.html");

                    }else{

                        alert("통신 중 문제가 발생하였습니다.");

                    }




                }
            });
        }
    }

    function getSessionMap(result) {
        sessionInfo = JSON.parse(result);
        //세션 설정후 호출될 callback함수
        if (getSessionInfo() != undefined) {
            getSessionInfo();
        }
    }

    //sessionStorage에 jsonObject 형식의 데이터를 key/value로 저장
    function setStorageItem(key, param){
        var setItem = "";
        if(param != null){
            setItem = JSON.stringify(param);
        }
        if(key !=null && key !=""){
            sessionStorage.setItem(key,setItem);
            console.log("set_key: "+key+" setValue: "+setItem);
        }else{
            sessionStorage.setItem(temp_key,setItem);
            console.log("set_key: "+temp_key+" setValue: "+setItem);
        }

    }

    //sessionStorage에 저장된 특정 key값의 데이터 반환
    function getStorageItem(key){
        var result = "";
        if(key != null && key != ""){
            result = sessionStorage.getItem(key);
            sessionStorage.removeItem(key); //한번 출력한 키는 삭제
        }else{ //key를 지정하지 않을 시 임의 지정(temp_key)
            result = sessionStorage.getItem(temp_key);
            sessionStorage.removeItem(temp_key);
        }
        return result;
    }

    function getJSessionId(){
        var jsId = document.cookie.match(/JSESSIONID=[^;]+/);
        if(jsId != null) {
            if (jsId instanceof Array)
                jsId = jsId[0].substring(11);
            else
                jsId = jsId.substring(11);
        }
        console.log("sessionId return : "+ jsId);
        return jsId;
    }
    //session Storage에 저장된 로그인 유저 데이터 (json String)
    function getUserData(){

        var userData = sessionStorage.getItem("userData");

        return userData;
    }