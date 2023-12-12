package com.hafiz.currencyratesandconverter.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hafiz.currencyratesandconverter.api.BankDataService
import com.hafiz.currencyratesandconverter.api.FixerApiService
import com.hafiz.currencyratesandconverter.datamodel.BankDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class MainViewModel(private val context: Context) : ViewModel() {
    private val bankDataService: BankDataService
    private val fixerApiService: FixerApiService

    private val _bankData = MutableLiveData<BankDataResponse?>()
    val bankData: LiveData<BankDataResponse?> = _bankData

    private val _currencyRates = MutableLiveData<Map<String, Double>?>()
    val currencyRates: LiveData<Map<String, Double>?> = _currencyRates

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apilayer.com/")
           // .client(createPinnedOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bankDataService = retrofit.create(BankDataService::class.java)
        fixerApiService = retrofit.create(FixerApiService::class.java)
    }

    // Function to create OkHttpClient with SSL Pinning
    private fun createPinnedOkHttpClient(): OkHttpClient {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificate = certificateFactory.generateCertificate(context.assets.open("server_certificate.crt")) as X509Certificate

            val certificatePinner = CertificatePinner.Builder()
                .add("apilayer.com", CertificatePinner.pin(certificate))
                .build()

            return OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                // Other configurations if needed
                .build()
        } catch (e: CertificateException) {
            e.printStackTrace()
            // Handle CertificateException
            throw RuntimeException("CertificateException occurred: ${e.message}")
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle IOException
            throw RuntimeException("IOException occurred: ${e.message}")
        }
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

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        return MainViewModel(context) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}

