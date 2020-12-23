package com.heqifuhou.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Stack;

public class StaticReflectUtils {
	public static List<String> getActionList(final Class<?> cls) {
		List<String> ls = new Stack<String>();
		Field fields[] = cls.getDeclaredFields();
		for (Field field : fields) {
			int nMod = field.getModifiers();
			// final static String
			if (Modifier.isFinal(nMod) && Modifier.isStatic(nMod)
					&& field.getGenericType() == String.class) {
				// BroadcastAction(supportAction = true)
				FilterAction jf = field
						.getAnnotation(FilterAction.class);
				if (jf != null && jf.isSupport()) {
					try {
						String action = field.get(null).toString();
						ls.add(action);
					} catch (Exception e) {
					}
				}
			}
		}
		return ls;
	}

	// 定义要支持
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FilterAction {
		public boolean isSupport() default true;
	}
}
