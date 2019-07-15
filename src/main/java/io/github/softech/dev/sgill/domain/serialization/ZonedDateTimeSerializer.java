package io.github.softech.dev.sgill.domain.serialization;

    import com.fasterxml.jackson.core.JsonGenerator;
    import com.fasterxml.jackson.core.JsonParser;
    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.DeserializationContext;
    import com.fasterxml.jackson.databind.JsonDeserializer;
    import com.fasterxml.jackson.databind.JsonSerializer;
    import com.fasterxml.jackson.databind.SerializerProvider;
    import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
    import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
    import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

    import java.io.IOException;
    import java.time.ZonedDateTime;
    import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    //private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'").withZone(ZONE_UTC);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public  void serialize(ZonedDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(DTF.format(value));
    }


    @Override
    public  Class<ZonedDateTime> handledType() {
        return ZonedDateTime.class;
    }

}
