function common_movePage(url, jsonData) {

        if(jsonData != null) {
            window.android.movePage(url, JSON.stringify(jsonData));
        } else {
            window.android.movePage(url, null);
        }
}


function emailSend(){
    window.android.openApp();
}


function phoneCall(){
    window.android.openApp();
}




     //////////////////////////////////////////////////////////////
     // 웹서버 통신 START
     ///////////////////////////////////////////////////////////////

   //전역변수 모음
   var sndUrl = "http://calebslab1.cafe24.com/";   //서버주소값
   var ajaxResult="";

      //공통 Util 모음

      /**
       * 서버호출 샘플
       */
    function navigate(params, target, successFnc, errorFnc) {
        $.ajax({
            url : sndUrl + target,
            type : "post",
            dataType: "json",
            data : params,
            success : successFnc,
            error : errorFnc
        });
    }

     //////////////////////////////////////////////////////////////
     // 웹서버 통신 END
     ///////////////////////////////////////////////////////////////

    //sessionStorage에 jsonObject 형식의 데이터를 key/value로 저장
    function setStorageItem(param, key){

        var setItem = JSON.stringify(param);

        console.log("set_key: "+key+" setValue: "+setItem);

        sessionStorage.setItem(key,setItem);

    }

    //sessionStorage에 저장된 특정 key값의 데이터 반환
    function getStorageItem(key){

        var result = "";

        if(key != null && key != ""){
            result = sessionStorage.getItem(key);
            sessionStorage.removeItem(key);
        }

        console.log("get_key: "+key+" getValue: "+result);

        return result;
    }