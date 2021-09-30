package com.example.dictionary.data.network

import android.util.Log
import com.example.dictionary.data.Status
import com.example.dictionary.data.languages.Languages
import com.example.dictionary.data.repository.DictionaryRepository
import com.example.dictionary.data.translate.Translate
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

object Client {
    private val client = OkHttpClient()

    fun getLanguages():Status<List<Languages>> {
        val request = Request.Builder().url("https://translate.argosopentech.com/languages").build()
        val response= client.newCall(request).execute()
        return if (response.isSuccessful){
            val result = Gson().fromJson(response.body?.string(),Array<Languages>::class.java).toList()
            Log.i("LANGUAGES",result.toString())

            DictionaryRepository.initLanguageList(result.toList())
            DictionaryRepository.initLanguageListTarget(result.toList())
            Status.Success(result)


        }else
        {
            Status.Fail(response.message)
        }

    }

    fun translatedText(x: String):Status<Translate>{

        val url="https://translate.argosopentech.com/translate"

       val formBody=FormBody.Builder()
           .add("q", x)
           .add("source",DictionaryRepository.sourceLanguages)
           .add("target",DictionaryRepository.targetLanguages)
           .build()

        val request=Request.Builder().url(url = url).post(formBody).build()
        val response= client.newCall(request).execute()
        return if (response.isSuccessful){
            val result = Gson().fromJson(response.body?.string(),Translate::class.java)
            Log.i("TRANSLATE",result.translatedText)
            Status.Success(result)
        }else
        {
            Status.Fail(response.message)
        }


    }

}