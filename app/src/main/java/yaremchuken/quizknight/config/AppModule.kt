package yaremchuken.quizknight.config

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import yaremchuken.quizknight.R
import yaremchuken.quizknight.api.yandex.dictionary.YaDictionaryClient
import yaremchuken.quizknight.api.yandex.translate.YaTranslateClient
import javax.inject.Singleton

@Module
class AppModule(
    private val resources: Resources
) {
    @Provides
    @Singleton
    fun yaDictionaryClient() = YaDictionaryClient(resources.getString(R.string.YA_DICTIONARY_API_KEY))

    @Provides
    @Singleton
    fun yaTranslateClient() = YaTranslateClient(resources.getString(R.string.YA_TRANSLATE_API_KEY))
}