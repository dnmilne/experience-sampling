package org.poscomp.xp.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * Created by dmilne on 16/07/2014.
 */
public class IdFormatting {


    public static class IdSerializer extends JsonSerializer<ObjectId> {

        @Override
        public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider provider)
                throws JsonProcessingException, IOException {
            gen.writeString(value.toString());
        }
    }

    public static class IdDeserializer extends JsonDeserializer<ObjectId> {


        @Override
        public ObjectId deserialize(JsonParser parser, DeserializationContext context)
                throws JsonProcessingException, IOException  {

            String idStr = parser.getText();

            if (idStr == null || idStr.trim().length() == 0 || idStr.equals("null"))
                return null ;

            ObjectId id = ObjectId.massageToObjectId(idStr) ;

            if (id == null)
                throw new IOException("Could not parse id '" + idStr + "'") ;

            return id ;
        }

    }

}
