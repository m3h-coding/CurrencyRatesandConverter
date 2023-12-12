package com.hafiz.currencyratesandconverter.datamodel

data class CurrencyRatesResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
    val success: Boolean,
    val timestamp: Long
)
