package com.hafiz.currencyratesandconverter.datamodel

data class BankDataResponse(
    val bank_data: BankData,
    val blz_code: String,
    val valid: Boolean
)

