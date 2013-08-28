package com.jl.crm.android.utils;

import android.os.AsyncTask;

import java.lang.reflect.*;

/**
 * Support for easily moving code off of the main IO thread.
 *
 * @author Josh Long
 */
public class AndroidUiThreadUtils {
	public static <T> T runOffUiThread(final T target, final Class<?>... tClass) {

		InvocationHandler invocationHandler = new InvocationHandler() {

			@Override
			public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

				final Method methodToInvokeOnTargetObject =
						  target.getClass().getMethod(method.getName(), method.getParameterTypes());

				AsyncTask<?, ?, T> valueReturningAsyncTask =
						  new AsyncTask<Object, Object, T>() {
							  @Override
							  protected T doInBackground(Object... params) {
								  try {
									  return (T) methodToInvokeOnTargetObject.invoke(target, args);
								  }
								  catch (Exception e) {
									  throw new RuntimeException(e);
								  }
							  }
						  };
				return valueReturningAsyncTask.execute(new Object[0]).get();
			}
		};
		Object objectProxy = Proxy.newProxyInstance(target.getClass().getClassLoader(), tClass, invocationHandler);
		return (T) objectProxy;
	}
}
