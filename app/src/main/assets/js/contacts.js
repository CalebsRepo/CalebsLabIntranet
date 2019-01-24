function callContacts() {
    window.android.callContacts();
}

function callPhone() {
    var phoneNum = document.getElementById('phoneNum').value;
    window.android.callPhone(phoneNum)
}