package yaremchuken.quizknight.api.yandex.dictionary

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import yaremchuken.quizknight.api.RestException
import java.io.IOException
import java.util.Locale

/**
 * REST Client for Yandex Dictionary API Service
 * https://yandex.ru/dev/dictionary/doc/dg/reference/lookup.html
 */
class YaDictionaryClient(
    private val apiKey: String
) {
    companion object {
        private const val SERVICE_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
        private const val PART_OF_SPEECH_SHORT_FLAG = "2"
    }

    private val httpClient = OkHttpClient()

    suspend fun lookup(text: String, source: Locale, target: Locale): Array<YaDictionaryEntity> {
        val lang = "${source.language}-${target.language}"

        val url = SERVICE_URL.toHttpUrl().newBuilder()
        url.addQueryParameter("key", apiKey)
        url.addQueryParameter("lang", lang)
        url.addQueryParameter("text", text)
        url.addQueryParameter("ui", target.language)
        url.addQueryParameter("flags", PART_OF_SPEECH_SHORT_FLAG)

        val request = Request.Builder().url(url.build()).build()

        return withContext(Dispatchers.IO) {
            try {
                httpClient.newCall(request).execute().use {
                    if (!it.isSuccessful || it.body == null) {
                        throw IOException("Service responded with code: ${it.code}")
                    }

                    return@withContext Gson().fromJson(it.body!!.string(), YaDictionaryResponse::class.java).def
                }
            } catch (ex: IOException) {
                throw RestException(SERVICE_URL, "{text=$text, lang=$lang, ui=${target.language}}", ex)
            }
        }
    }
}