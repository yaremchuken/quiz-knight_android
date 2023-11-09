package yaremchuken.quizknight.api.yandex.translate

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import yaremchuken.quizknight.api.RestException
import java.io.IOException
import java.util.Locale

/**
 * REST Client for Yandex Cloud Translate Service V2
 * https://cloud.yandex.ru/docs/translate/
 */
class YaTranslateClient(
    apiKey: String
) {
    companion object {
        private const val SERVICE_URL = "https://translate.api.cloud.yandex.net/translate/v2/translate"
        private const val CONTENT_TYPE = "application/json"
        private const val MEDIA_TYPE = "application/json; charset=utf-8"
    }

    private val httpClient = OkHttpClient()

    private val headers = Headers.headersOf(
        "Content-Type", CONTENT_TYPE,
        "Authorization", "Api-Key $apiKey")

    /**
     * Make REST call to Yandex Translate service.
     * @return map where key is source text and value is it translation.
     */
    suspend fun translate(texts: Array<String>, source: Locale, target: Locale): Map<String, String> {
        val yaReq = YaTranslateRequest(texts, source.language, target.language)
        val body = Gson().toJson(yaReq).toRequestBody(MEDIA_TYPE.toMediaType())

        val request = Request.Builder()
            .url(SERVICE_URL)
            .headers(headers)
            .post(body)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                httpClient.newCall(request).execute().use {
                    if (!it.isSuccessful || it.body == null) {
                        throw IOException("Service responded with code: ${it.code}")
                    }

                    val yaTrans = Gson().fromJson(it.body!!.string(), YaTranslateResponse::class.java)

                    val translations: MutableMap<String, String> = HashMap()
                    texts.forEachIndexed { idx, text -> translations[text] = yaTrans.translations[idx].text }

                    return@withContext translations
                }
            } catch (ex: IOException) {
                throw RestException(SERVICE_URL, yaReq.toString(), ex)
            }
        }
    }
}