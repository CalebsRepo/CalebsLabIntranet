// 숫자체크
function commonUtil_isNumber(data) {
    var val = data;
    var Num = "1234567890";
    for (i=0; i<val.length; i++) {
        if(Num.indexOf(val.substring(i,i+1))<0) {
            return false;
        }
    }
    return true;
}

// 날짜체크
function commonUtil_isDate(data) {
    // '/'나 '-' 구분자 제거
    var val = getRemoveFormat(data);

    // 숫자, length 확인
    if (isNumber(val, msg) && val.length == 8) {
        var year = val.substring(0,4);
        var month = val.substring(4,6);
        var day = val.substring(6,8);

        // 유효날짜 확인
        if(checkDate(year,month,day,msg)){
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}

// 구분자 제거
function commonUtil_getRemoveFormat(val) {
    if(val.length == 10) {
        var arrDate = new Array(3);
        arrDate = val.split("/");
        if(arrDate.length != 3) {
            arrDate = val.split("-");
        }
        return arrDate[0] + arrDate[1] + arrDate[2];
    } else {
        return val;
    }
}
// 날짜 YYYY/MM/DD 포맷
/**
* 내용 : YYYYMMDD 날짜를 YYYY/MM/DD 형태로 Format해준다
* param date : YYYYMMDD 형태 문자형 날짜
* return : YYYY/MM/DD 형태로 포맷팅된 값
**/
function YMDFormatter(date){
     if(!date) return "";
     var formatDate = '';

     // 공백제거
     date=date.replace(/\s/gi, "");

     try{
          if(date.length == 8) {
               formatDate = date.replace(/(\d{4})(\d{2})(\d{2})/, '$1/$2/$3');
          }
     } catch(e) {
          formatDate = date;
          console.log(e);
     }
     return formatDate;
}

// 두 일자의 차이일수를 계산하여 return
function diffDate(date1, date2) {
    var splitDate1 = date1.split('/');
    var splitDate2 = date2.split('/');
    var resultData = "";
    var da1 = new Date(splitDate1[0], splitDate1[1], splitDate1[2]);
    var da2 = new Date(splitDate2[0], splitDate2[1], splitDate2[2]);
    var dif = da2 - da1;
    var cDay = 24 * 60 * 60 * 1000;// 시 * 분 * 초 * 밀리세컨
    resultData = parseInt(dif/cDay);
    console.log("날짜차이 = " + resultData);
    return resultData;
 }
