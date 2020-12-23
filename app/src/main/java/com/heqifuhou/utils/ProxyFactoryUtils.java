package com.heqifuhou.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//T 是interface
//obj是类名
public abstract class ProxyFactoryUtils<T> implements InvocationHandler {
	private T target; // 目标对象
	@SuppressWarnings("unchecked")
	public  Object createProxyInstance(Object tObj) {
		this.target=(T)tObj;
		Class<? extends Object> cls = tObj.getClass();
		return Proxy.newProxyInstance(cls.getClassLoader(),
		cls.getInterfaces(), this); 
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result=null;
		doBefore(method);
		try {
			result = method.invoke(target, args);
		} catch (Exception e) {
			doException(method);
		} finally {
			doFinally(method);
		}
		doAfter(method);
		return result;
	}

	// 前置通知
	protected abstract void doBefore(Method method);
	// 后置通知
	protected abstract void doAfter(Method method);
	// 例外通知
	protected abstract void doException(Method method);
	// 最终通知
	protected abstract void doFinally(Method method);
}
