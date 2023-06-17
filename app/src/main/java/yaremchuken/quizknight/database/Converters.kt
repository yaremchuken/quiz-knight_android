package yaremchuken.quizknight.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class Converters<T> {
    @TypeConverter
    fun listToString(value: List<T>): String {
        val type = object : TypeToken<List<T>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun stringToList(value: String): List<T> {
        val type = object : TypeToken<List<T>>() {}.type
        return Gson().fromJson(value, type)
    }
}

class StringConverter: Converters<String>()