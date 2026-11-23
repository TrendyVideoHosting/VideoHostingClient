package mikhail.shell.video.hosting.data.converters

import com.google.gson.Gson
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class EnumConverterFactory(
    private val gson: Gson
) : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation?>,
        retrofit: Retrofit
    ): Converter<*, String?>? {
        if (type !is Class<*> || !type.isEnum) {
            return null
        }

        return Converter<Any, String?> { value ->
            gson.toJsonTree(value).asString
        }
    }
}