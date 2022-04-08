package com.artisan.un.apiModel

import java.util.*

class AadharCardModel {
    var number: String? = null
    var name: String? = null
    var dob: Date? = null
    var imageFront: String? = null
    var imageBack: String? = null
}

class PanCardModel {
    var number: String? = null
    var name: String? = null
    var dob: Date? = null
    var image: String? = null
}

class BrnModel {
    var number: String? = null
    var name: String? = null
    var image: String? = null
}

open class AddressModel {
    var id: Int? = null
    var addressLineOne: String? = null
    var addressLineTwo: String? = null
    var country: String? = null
    var state: String? = null
    var city: String? = null
    var pin: String? = null
    var countries = ArrayList<String>()
    var states = ArrayList<String>()
    var cities = ArrayList<String>()
}