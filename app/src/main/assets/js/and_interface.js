    function and_emailSend(){
        window.android.openApp();
    }

    function and_phoneCall(){
        window.android.openApp();
    }

    function and_callContacts() {
        window.android.callContacts();
    }

    function and_callPhone() {
        var phoneNum = document.getElementById('phoneNum').value;
        window.android.callPhone(phoneNum)
    }

    function callGallery() {
        window.android.callGallery();
    }