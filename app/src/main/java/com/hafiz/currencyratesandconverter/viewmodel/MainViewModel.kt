package com.hafiz.currencyratesandconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hafiz.currencyratesandconverter.api.BankDataService
import com.hafiz.currencyratesandconverter.api.FixerApiService
import com.hafiz.currencyratesandconverter.datamodel.BankDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {
    private val bankDataService: BankDataService
    private val fixerApiService: FixerApiService

    private val _bankData = MutableLiveData<BankDataResponse?>()
    val bankData: LiveData<BankDataResponse?> = _bankData

    private val _currencyRates = MutableLiveData<Map<String, Double>?>()
    val currencyRates: LiveData<Map<String, Double>?> = _currencyRates

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apilayer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bankDataService = retrofit.create(BankDataService::class.java)

        val retrofitFixer = Retrofit.Builder()
            .baseUrl("https://apilayer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fixerApiService = retrofitFixer.create(FixerApiService::class.java)
    }

    suspend fun fetchBankData() {
        try {
            val response = withContext(Dispatchers.IO) {
                bankDataService.getBankData()
            }
            _bankData.postValue(response)
        } catch (e: Exception) {
            _bankData.postValue(null)
            // Handle network errors
        }
    }

    suspend fun fetchCurrencyRates(apiKey: String, baseCurrency: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                fixerApiService.getLatestRates(apiKey, baseCurrency)
            }
            _currencyRates.postValue(response.rates)
        } catch (e: Exception) {
            _currencyRates.postValue(null)
            // Handle network errors
        }
    }
}

