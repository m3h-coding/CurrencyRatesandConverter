package com.hafiz.currencyratesandconverter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hafiz.currencyratesandconverter.databinding.ActivityMainBinding
import com.hafiz.currencyratesandconverter.datamodel.BankDataResponse
import com.hafiz.currencyratesandconverter.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = MainViewModel.provideFactory(this)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        lifecycleScope.launch {
            mainViewModel.fetchBankData()
            mainViewModel.fetchCurrencyRates("marketplace/fixer-api", "USD")
        }

        mainViewModel.bankData.observe(this) { bankData ->
            if (bankData != null) {
                displayBankData(bankData)
            } else {
                Toast.makeText(this@MainActivity, "Error fetching bank data", Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.currencyRates.observe(this) { rates ->
            if (rates != null) {
                displayCurrencyRates(rates)
            } else {
                Toast.makeText(this@MainActivity, "Error fetching rates", Toast.LENGTH_SHORT).show()
            }
        }

        setupUIInteractions()
    }

    @SuppressLint("SetTextI18n")
    private fun displayBankData(bankData: BankDataResponse) {
        binding.apply {
            bankNameTextView.text = "Bank Name: ${bankData.bank_data.name}"
            bankCityTextView.text = "City: ${bankData.bank_data.city}"
            bankZipTextView.text = "ZIP: ${bankData.bank_data.zip}"
            bankBicTextView.text = "BIC: ${bankData.bank_data.bic}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayCurrencyRates(rates: Map<String, Double>) {
        val currencyList = rates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fromCurrencySpinner.adapter = adapter

        binding.convertButton.setOnClickListener {
            val selectedCurrency = binding.fromCurrencySpinner.selectedItem as String
            val amount = binding.amountInput.text.toString().toDoubleOrNull()

            if (amount != null) {
                val convertedAmount = amount * rates[selectedCurrency]!!
                binding.convertedResultTextView.text = "Converted Amount: $convertedAmount $selectedCurrency"
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUIInteractions() {
        // Handle other UI interactions here, e.g., button clicks, spinner selections
        // Example:
        binding.toCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle spinner item selection
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when no item is selected in the spinner
            }
        }
    }
}



