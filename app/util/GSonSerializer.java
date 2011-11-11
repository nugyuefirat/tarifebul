package util;

import java.util.List;



import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GSonSerializer {

	public static Gson createGSonBuilder(final List<Class<?>> classesToSkip) {
		return new GsonBuilder()
				.setExclusionStrategies(new ExclusionStrategy() {

					public boolean shouldSkipClass(Class<?> clazz) {
						return classesToSkip.contains(clazz);
					}

					/**
					 * Custom field exclusion goes here
					 */
					public boolean shouldSkipField(FieldAttributes f) {
						return false;
					}

				})
				/**
				 * Use serializeNulls method if you want To serialize null
				 * values By default, Gson does not serialize null values
				 */
				.serializeNulls().create();
	}
}
