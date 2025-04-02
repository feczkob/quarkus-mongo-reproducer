package com.ab.planner.nbt.application.bean

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import io.quarkus.jackson.ObjectMapperCustomizer
import jakarta.inject.Singleton
import org.openapitools.jackson.nullable.JsonNullableModule
import java.time.LocalDate

@Singleton
class MapperConfig : ObjectMapperCustomizer {
    override fun customize(objectMapper: ObjectMapper): Unit =
        objectMapper.run {
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, false)

            enable(SerializationFeature.INDENT_OUTPUT)

            setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)

            propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            registerModule(JsonNullableModule())

            val javaTimeModule =
                JavaTimeModule()
                    .apply {
                        addSerializer(LocalDate::class.java, LocalDateSerializer.INSTANCE)
                        addDeserializer(LocalDate::class.java, LocalDateDeserializer.INSTANCE)
                    }

            registerModule(javaTimeModule)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        }
}
