package com.example.dictionary.data.repository

import com.example.dictionary.data.languages.Languages

object DictionaryRepository {

    val languageList = mutableListOf<Languages>()
    val languagesListTarget= mutableListOf<Languages>()
    lateinit var sourceLanguages:String
    lateinit var targetLanguages:String


    fun initLanguageList(Languages: List<Languages>){
        languageList.addAll(Languages)
        languageList.add(0,Languages("auto","Auto Detect"))
    }

    fun initLanguageListTarget(Languages: List<Languages>)=   languagesListTarget.addAll(Languages)


    val Languages get() = languageList
    val languagesTarget get() = languagesListTarget




}