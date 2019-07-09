package qsos.lib.base.utils.date

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import qsos.lib.base.utils.DateUtils
import java.lang.reflect.Type
import java.util.*

/**
 * @author : 华清松
 * @description : Long转日期
 */
class DateDeserializer : JsonDeserializer<Date> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
        return try {
            DateUtils.strToDate(json.asJsonPrimitive.asString)
        } catch (e: Exception) {
            e.printStackTrace()
            Date(json.asJsonPrimitive.asLong)
        }
    }
}