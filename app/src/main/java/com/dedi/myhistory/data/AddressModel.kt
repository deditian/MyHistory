package com.dedi.myhistory.data

data class AddressModel(
    val Response: Response?
)

data class Response(
    val View: List<View>
)

data class View(val Result: List<Result>)

data class Result(
    val Location: Location
)
data class Location(
    val Address: Address
)

data class Address(
    val Label: String
)

