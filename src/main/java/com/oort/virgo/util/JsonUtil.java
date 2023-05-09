package com.oort.virgo.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.lang.reflect.Type;


public class JsonUtil {
	
	public static final ObjectMapper mapper = new ObjectMapper();
	static{
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
		
		mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.PUBLIC_ONLY);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	}

	public static String jsonFromObject(Object object, boolean pretty) throws Exception{

		if(pretty) {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} else {
			return mapper.writeValueAsString(object);
		}
	}
	
	
	public static String jsonFromObject(Object object,Type genericType) throws Exception{
		return mapper.writer().withType(mapper.constructType(genericType)).writeValueAsString(object);
	}
	
	/**
	 * 适用于没有泛型的情况
	 * @param <T>
	 * @param json
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	public static <T> T objectFromJson(String json, Class<T> klass) throws Exception{
		T object;
		object = mapper.readValue(json, klass);
		return object;
	}
	
	/**
	 * 适用于已知的泛型，比如HashMap<String,Long>
	 * 具体用法：
	 *    HashMap<String,Long> map = (HashMap<String,Long>)objectFromJson(jsonStr,new TypeReference<HashMap<String,Long>>(){});
	 * @param <T>
	 * @param json
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	public static <T> T objectFromJson(String json, TypeReference<T> klass) throws Exception{
		T object;
		object = mapper.readValue(json, klass);
		return object;
	}
	
	/**
	 * 适用于未知的泛型，比如反射某个属性，无法知道确切的泛型类型
	 * 具体用法：
	 * 		Field f = ....;
	 * 		Object o = objectFromJson(jsonStr,f.getGenericType());
	 * 		
	 * @param <T>
	 * @param json
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static <T> T objectFromJson(String json, Type type) throws Exception{
		T object;
		object = mapper.readValue(json, mapper.constructType(type));
		return object;
	}
	
}
