    /**
    * 내용 : 값이 음수일 경우 텍스트 색상 변경
    * param data : 체크할 숫자
    * return : 값이 음수일경우 텍스트 색상 빨간색, 아닐경우 data 그대로 리턴
    **/
    function minusCheck(data){
        var minus = "";
       if(data < 0){
            minus = "<span style='color:red'> "+ data +" </span>"
            return minus;
       } else {
            return data;
       }
    }

    /**
    * 내용 : 근속년수 80% 계산시 해당년도가 윤년인지 평년인지 계산
    * param year : 년도
    * return : 해당년도의 근속년수 80% 일수
    **/
    function leapYear(year) {
        if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            wd_80 =  366 * 0.8;
            return wd_80;
        }else {
            wd_80 =  365 * 0.8;
            return wd_80;
        }
    }

    /**
    * 내용 : 연차발생일수 계산
    * param (joinYear : 근무년차, joinMon : 근무월수, joinDay : 근무일수)
    * return : holidayCnt (연차 발생 일수)
    *  내용 - 근무년차에 따른 연차 계산
    *  연차 최대일수는 25일
    *  근속년수 80% 미만 : 1개월마다 1일의 연차
    *  1년 이상 3년 미만 : 15일 연차
    *  3년 이상 5년 미만 : 15일 + 1일 연차
    *  5년 이상 2년마다  : 16일 + (2년*1)일 휴가
    **/
    function calculationHoliday(joinYear,joinMon,joinDay) {
        var holidayCnt = 15;
        var thisYear = new Date().getFullYear();    // 올해년도
        var wd_80 = leapYear(thisYear);             // 입사 후 1년 80% 근무일수

        if(joinYear == 3) {
            holidayCnt = holidayCnt + 1;
        }else if(joinYear > 3) {
            holidayCal = holidayCnt + 1 + Math.floor(( joinYear - 3)/2);
            holidayCnt = (holidayCal > 25) ? 25 : holidayCal;
        }else if(joinDay < wd_80) {
            holidayCnt = joinMon * 1;
        };
        return holidayCnt;
    }

    <!-- 근무기간(년,개월,일) 계산 -->
    function workingDateFormat(joinDay){
        if(joinDay < 30) {
            day = joinDay;
            workingDate = day + '일';
        } else if (joinDay < 365) {
            month = Math.floor(join_day/30);
            day = joinDay - (month * 30);
            workingDate =  month + '개월 ' + day + '일';
        } else {
            year = Math.floor(joinDay/365);
            month = Math.floor((joinDay-(year*365))/30);
            day = joinDay - (year*365) - (month*30);
            workingDate =  year + '년 ' + month + '개월 ' + day + '일';
        }
        return workingDate;
    }
