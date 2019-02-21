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
//날짜 YYYY/MM/DD 포맷
function YMDFormatter(num){
     if(!num) return "";
     var formatNum = '';

     // 공백제거
     num=num.replace(/\s/gi, "");

     try{
          if(num.length == 8) {
               formatNum = num.replace(/(\d{4})(\d{2})(\d{2})/, '$1/$2/$3');
          }
     } catch(e) {
          formatNum = num;
          console.log(e);
     }
     return formatNum;
}
