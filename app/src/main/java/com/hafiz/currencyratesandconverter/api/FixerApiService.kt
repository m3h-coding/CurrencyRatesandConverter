package com.hafiz.currencyratesandconverter.api

import com.hafiz.currencyratesandconverter.datamodel.CurrencyRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FixerApiService {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("access_key") apiKey: String,
        @Query("base") baseCurrency: String
    ): CurrencyRatesResponse
}