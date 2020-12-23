package com.heqifuhou.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

//by zhujun
public class CopyObjectUtils {
	// 继承结构一样，字段一样
	// 下面的方法完全可以代替他
	public static Object copySameBeanProperties(Object toObject,
			Object fromObject) {
		Class<? extends Object> fromClassType = fromObject.getClass();
		Class<? extends Object> toClassType = toObject.getClass();
		do {
			Field fields[] = fromClassType.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i].getName();
				Field targetField = null;
				try {
					targetField = toClassType.getDeclaredField(fieldName);
				} catch (NoSuchFieldException e) {
					return null;// 不存在对等的
				}
				// static final
				int nMod = fields[i].getModifiers();
				if (Modifier.isFinal(nMod) && Modifier.isStatic(nMod)) {
					continue;
				}

				// 类型不同不COPY
				if (fields[i].getType() != targetField.getType()) {
					continue;
				}
				// CanCopy(isCanCopy = false)
				CanCopy jf = fields[i].getAnnotation(CanCopy.class);
				if (jf != null && jf.isCanCopy() == false) {
					continue;
				}
				String firstLetter = fieldName.substring(0, 1).toUpperCase();
				String getMethodName = "get" + firstLetter
						+ fieldName.substring(1);
				String setMethodName = "set" + firstLetter
						+ fieldName.substring(1);
				try {
					Method getMethod = fromClassType.getMethod(getMethodName,
							new Class[] {});
					Method setMethod = toClassType.getMethod(setMethodName,
							new Class[] { fields[i].getType() });
					Object value = getMethod
							.invoke(fromObject, new Object[] {});
					if (null != value) {
						setMethod.invoke(toObject, new Object[] { value });
					}
				} catch (Exception e) {
				}
			}
			fromClassType = fromClassType.getSuperclass();
			toClassType = toClassType.getSuperclass();
			if (fromClassType.equals(Object.class)
					|| toClassType.equals(Object.class)) {
				return toObject;
			}
		} while (true);
	}

	// 定义是否不COPY
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CanCopy {
		public boolean isCanCopy() default true;
	}

	// 结构不一样，字段一样
	public static Object copyDiffBeanProperties(Object toObject,
			Object fromObject) {
		Class<? extends Object> fromClassType = fromObject.getClass();
		Class<? extends Object> toClassType = toObject.getClass();
		do {
			// 看目标需要什么，再反查
			Field fields[] = toClassType.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (isCanCopyMod(fields[i])) {
					continue;
				}
				// 在源中找相应的数据并赋值
				Class<? extends Object> _formClassType = fromClassType;
				do {
					String fieldName = fields[i].getName();
					Field fromObjField = null;
					try {
						fromObjField = toClassType.getDeclaredField(fieldName);
					} catch (NoSuchFieldException e) {
					}
					// 在源中找到相同的字段
					if (fromObjField != null
							&& isCanCopyMod(fields[i])
							&& fields[i].getType() == fromObjField.getType()) {
						String firstLetter = fieldName.substring(0, 1)
								.toUpperCase();
						String getMethodName = "get" + firstLetter
								+ fieldName.substring(1);
						String setMethodName = "set" + firstLetter
								+ fieldName.substring(1);
						try {
							Method getMethod = _formClassType.getMethod(
									getMethodName, new Class[] {});
							Method setMethod = toClassType.getMethod(
									setMethodName,
									new Class[] { fields[i].getType() });
							Object value = getMethod.invoke(fromObject,
									new Object[] {});
							if (null != value) {
								setMethod.invoke(toObject,
										new Object[] { value });
							}
						} catch (Exception e) {
							// 相同的字段，赋值失败，无视掉
						}
						break;
					}
					// 向基类中查询
					_formClassType = _formClassType.getSuperclass();
					if (_formClassType.equals(Object.class)) {
						break;
					}
				} while (true);
			}
			// 目标向基类发展
			toClassType = toClassType.getSuperclass();
			if (toClassType.equals(Object.class)) {
				// 源已经找不到了，可以了
				return toObject;
			}
		} while (true);

	}

	// 判断是否能COPY
	private static boolean isCanCopyMod(Field to) {
		int nToMod = to.getModifiers();
		if (Modifier.isFinal(nToMod) && Modifier.isStatic(nToMod)) {
			return false;
		}
		// 目标上有标记不能COPY的。CanCopy(isCanCopy = false)
		CanCopy jf = to.getAnnotation(CanCopy.class);
		if (jf != null && !jf.isCanCopy()) {
			return false;
		}
		return true;
	}
}