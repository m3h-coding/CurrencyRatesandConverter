package com.hafiz.currencyratesandconverter.api

import com.hafiz.currencyratesandconverter.datamodel.BankDataResponse
import retrofit2.http.GET

interface BankDataService {
    @GET("bank_data")
    suspend fun getBankData(): BankDataResponse
}