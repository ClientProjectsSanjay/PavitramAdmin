package com.artisan.un.utils

enum class AppLanguage constructor(val code: String) {
    ENGLISH("en"),
    KANNADA("kn")
}

enum class TextInputType {
    TEXT,
    EMAIL,
    NUMBER,
    DATE,
    PHONE,
    NONE,
    TEXT_PASSWORD,
}
enum class MenuType {
    HOME,
    PROFILE,
    PRODUCT,
    MyOrder,
    DRAFT,
    BLOG,
    LANGUAGE,
    TERMS_AND_CONDITIONS,
    PRIVACY_POLICY,
    RATE_US,
    HELP,
}

enum class EnterMobileAction(var action: String) {
    FORGOT_PASSWORD("FORGOT_PASSWORD"),
    UPDATE_MOBILE("UPDATE_MOBILE"),
}

enum class ProductMediaType{
    VIDEO,
    IMAGE,
    FILE,
}

enum class DocumentFileType(requestCode: Int){
    AADHARFRONT(101),
    AADHARBACK(201),
    LETTERHEAD(301),
    PAN(401),
    GST(501),
    BRN(601),
    UDHYAM(701),
    FACTORY(801),
    FSSAI(901),
    ACCOUNTPROOF(1001),
}

enum class DateOfBirthType{
    AADHAR,
    PAN,
}
