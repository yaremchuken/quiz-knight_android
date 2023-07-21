package yaremchuken.quizknight.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import yaremchuken.quizknight.PersonageType

class Converters {

    private val stringListType = object : TypeToken<List<String>>() {}.type
    private val personageListType = object : TypeToken<List<PersonageType>>() {}.type

    @TypeConverter
    fun listToString(value: List<String>): String = Gson().toJson(value, stringListType)

    @TypeConverter
    fun stringToList(value: String): List<String> = Gson().fromJson(value, stringListType)

    @TypeConverter
    fun listToPersonages(value: List<PersonageType>): String = Gson().toJson(value, personageListType)

    @TypeConverter
    fun personagesToList(value: String): List<PersonageType> =
        Gson().fromJson<List<String>?>(value, stringListType).map { PersonageType.valueOf(it) }
}