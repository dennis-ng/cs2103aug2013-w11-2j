package typetodo.db;

import org.joda.time.DateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateTimeTypeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
	// No need for an InstanceCreator since DateTime provides a no-args constructor
	@Override
	public JsonElement serialize(DateTime src, java.lang.reflect.Type srcType, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
	@Override
	public DateTime deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context)
			throws com.google.gson.JsonParseException {
		return new DateTime(json.getAsString());
	}
}
