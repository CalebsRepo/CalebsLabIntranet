    document.write('<script type="text/javascript" src="https://code.jquery.com/jquery-1.11.1.min.js"><\/script>')
    document.write('<script type="text/javascript" src="../js/common_val.js"><\/script>')
    document.write('<script type="text/javascript" src="../js/and_interface.js"><\/script>')
    document.write('<script type="text/javascript" src="../js/common_util.js"><\/script>')

    /**
     * 페이지 이동
     * param url : 이동할 주소, param : 이동 시 전달할 데이터
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
     parameter(데이터(필수- 없으면 "", url(필수), 성공함수(필수), 실패함수(선택 - 없으면 null이나 ""))
     */
    function navigate(param, urlAddress, successFunc, errorFunc) {

        sessionId = window.android.returnSessionId();
        //JSON 데이터가 없을 시
        if(param == "") {
            console.log("JSON 데이터 없음");
            $.ajax({
                url : sndUrl + urlAddress, type : "post", dataType: "json",
                beforeSend : function(xmlHttpRequest){
                                    xmlHttpRequest.setRequestHeader("AJAX", "true");
                                    xmlHttpRequest.setRequestHeader("sessionId", sessionId);
                                },
                success : successFunc,
                error:function(xhr, textStatus, error){

                        if(xhr.status=="400")
                        {
                            alert("세션이 만료되었습니다.");
                            movePage("login.html");
                        }else{
                            if(errorFunc != "" || errorFunc != null) {
                                errorFunc.call(this, "에러Func 호출임");
                            }else{
                                alert("통신 중 문제가 발생하였습니다.");
                            }
                        }
                    }
            });
         }else {
        //JSON 데이터가 있을 시
            console.log("JSON 데이터 있음");
            $.ajax({
                url : sndUrl + urlAddress, type : "post", dataType: "json", data : param,
                beforeSend : function(xmlHttpRequest){
                                    xmlHttpRequest.setRequestHeader("AJAX", "true");
                                    xmlHttpRequest.setRequestHeader("sessionId", sessionId);
                                },
                success : successFunc,
                error:function(xhr, textStatus, error){

                    if(xhr.status=="400")
                    {
                        alert("세션이 만료되었습니다.");
                        movePage("login.html");

                    }else{
                        if(errorFunc != "" || errorFunc != null) {
                            errorFunc.call(this, "에러Func 호출임");
                        }else{
                            alert("통신 중 문제가 발생하였습니다.");
                        }
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

    // push 보내기 추가
    function sendFirebase(params, flag) {
        var data = "";

        if(flag == "single") {  // 한사람에게 push
            if(params.type != "picture") { // 이미지가 포함되지 않은 경우
                data = JSON.stringify({"to": params.token, "data": {"title" : params.title, "body" : params.body, "type" : params.type, "picture" : ""}});
            }else { // 이미지가 포함된 경우
                data = JSON.stringify({"to": params.token, "data": {"title" : params.title, "body" : params.body, "type" : params.type, "picture" : params.picture}});
            }
        }else if(flag == "multi") { // 여러명에게 push
            if(params.type != "picture") { // 이미지가 포함되지 않은 경우
                data = JSON.stringify({"registration_ids": params.token, "data": {"title" : params.title, "body" : params.body, "type" : params.type, "picture" : ""}});
            }else { // 이미지가 포함된 경우
                data = JSON.stringify({"registration_ids": params.token, "data": {"title" : params.title, "body" : params.body, "type" : params.type, "picture" : params.picture}});
            }
        }


        $.ajax({
            type : 'POST',
            url : "https://fcm.googleapis.com/fcm/send",
            headers : {
                Authorization : 'key=' + 'AAAAkYFS9DQ:APA91bE8wpd2UMMs3a5hs5CUPuB66ibSRNJgTnyxKXAmZPegtessyPpokztLHppj9Hy2kaE22SLGHd7C79kdnDuUdjj6CGTMSnu5EFzJut9wE8q9K8e07AqNezkTlaZdX8ya1OUKbffg'
            },
            contentType : 'application/json',
            dataType: 'json',
            //data: JSON.stringify({"to": params.token, "notification": {"title" : params.title, "body" : params.body }}),
            //data: JSON.stringify({"to": params.token, "data": {"title" : params.title, "body" : params.body }}),    // background에서 sendNotification()함수를 타기 위하여 notification대신 data를 사용한다.
            //data: JSON.stringify({"registration_ids": params.token, "data": {"title" : params.title, "body" : params.body }}),
            data :  data,
            async : false,
            success : function(response) {
                console.log(response);
            },
            error : function(xhr, status, error) {
                console.log(xhr.error);
             }
        });
    }

