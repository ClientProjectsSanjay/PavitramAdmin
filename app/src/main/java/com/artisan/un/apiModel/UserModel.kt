package com.artisan.un.apiModel

class AuthModel {
    var name: String? = null
    var emailId: String? = null
    var mobileNo: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var userType: String? = null
    var isPromotionalEmailEnabled: Boolean = false
}

class LoginModel {
    var phoneOrEmail: String? = null
    var password: String? = null
}