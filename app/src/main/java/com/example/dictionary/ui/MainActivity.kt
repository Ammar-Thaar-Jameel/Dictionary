package com.example.dictionary.ui

import android.util.Log
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.example.dictionary.R
import com.example.dictionary.data.Status
import com.example.dictionary.data.network.Client
import com.example.dictionary.data.repository.DictionaryRepository
import com.example.dictionary.data.repository.DictionaryRepository.sourceLanguages
import com.example.dictionary.data.repository.DictionaryRepository.targetLanguages
import com.example.dictionary.databinding.ActivityMainBinding
import com.example.dictionary.ui.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val theme: Int
        get() = R.style.Theme_Dictionary
    override val inflate: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun setup() {
    if (DictionaryRepository.Languages.isEmpty())
    {
        makeRequestLanguages()
    }else{
        initSpinner()
    }


        binding.button.setOnClickListener() {
            val translateText = binding.inputTextField.text.toString()
            makeRequest(translateText)
        }

    }

    private fun makeRequest(translateText: String) {
        val flow = flow {
            val result = Client.translatedText(translateText)
            emit(result)
        }.flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.catch { Log.v("CATCH", "fail:${it.message}") }.collect {
                when (it) {
                    is Status.Fail -> Log.v("FAIL", "fail:${it.message}")
                    Status.Loading -> Log.v("LOADING", "loading")
                    is Status.Success -> {
                        binding.textViewTranslate.text = it.data.translatedText
                        Log.v("SUCCESS", "SUCCESS:${it.data.translatedText}")
                    }
                }
            }
        }
    }


    private fun makeRequestLanguages() {
        val flow = flow {
            val result = Client.getLanguages()
            emit(result)
        }.flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.catch { Log.v("MAINCATCH", "fail:${it.message}") }.collect {
                when (it) {
                    is Status.Fail -> Log.v("MainFAIL", "fail:${it.message}")
                    is Status.Loading -> Log.v("MAINLOADING", "loading")
                    is Status.Success -> initSpinner()


                }
            }
        }
    }


    private fun initSpinner() {
        val languageItems = DictionaryRepository.languageList.map { it.name }
        val autoCompleteArrayAdapter = ArrayAdapter(this, R.layout.list_item_spinner, languageItems)


        val languageItemsTarget = DictionaryRepository.languagesListTarget.map { it.name }
        val autoCompleteArrayAdapterTarget = ArrayAdapter(this, R.layout.list_item_spinner, languageItemsTarget)
        binding.apply {
            firstLanguage.apply {
                setAdapter(autoCompleteArrayAdapter)
                onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        sourceLanguages = DictionaryRepository.languageList[position].code
                    }
            }


            secondLanguage.apply {
                setAdapter(autoCompleteArrayAdapterTarget)
                onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        targetLanguages = DictionaryRepository.languagesListTarget[position].code
                    }
            }

        }


    }


}