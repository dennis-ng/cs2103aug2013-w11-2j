/**
 * @author Dennis Ng
 */
package typetodo.db;

import java.lang.reflect.Type;

import typetodo.logic.Task;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TaskAdapter implements JsonSerializer<Task>,
		JsonDeserializer<Task> {
	@Override
	public JsonElement serialize(Task src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
		result.add("properties", context.serialize(src, src.getClass()));

		return result;
	}

	@Override
	public Task deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");

		try {
			return context.deserialize(element,
					Class.forName("typetodo.logic." + type));
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Unknown element type: " + type, e);
		}
	}

}
