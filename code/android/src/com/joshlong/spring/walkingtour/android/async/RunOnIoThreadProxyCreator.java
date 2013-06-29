package com.joshlong.spring.walkingtour.android.async;

import android.os.AsyncTask;

import java.lang.reflect.*;

/**
 * builds JDK proxies that runs each method annotated with
 * {@link RunOffUiThread} in an {@link AsyncTask}.
 * <p/>
 *
 * The client of this proxy can receive the return value in one of two ways:
 *
 * <OL>
 * <LI> the return value of the method </LI>
 * <LI> through an argument of type {@link AsyncCallback} which will collect the result. </LI>
 * </OL>
 *
 * If there is an argument of type {@link AsyncCallback}, the returned value is returned <EM>within</EM>
 * the UI thread, <EM>not</EM> in the same thread as the {@link AsyncTask}.
 *
 * @author Josh Long
 */
public class RunOnIoThreadProxyCreator {

    public static <T> T runOnIoThread(final T o) {
        return runOnIoThread(o, o.getClass().getInterfaces());
    }

    private static Object safeInvoke(Object target, Method method, Object[] arguments) {
        try {
            return method.invoke(target, arguments);
        } catch (Throwable thro) {
            throw new RuntimeException(thro);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T runOnIoThread(final T target, final Class<?>... tClass) {

        InvocationHandler invocationHandler = new InvocationHandler() {

            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

                Class<RunOffUiThread> classOfAnnotation = RunOffUiThread.class;
                final Method methodToInvokeOnTargetObject = target.getClass().getMethod(method.getName(), method.getParameterTypes());
                final boolean runInIoThread = method.getAnnotation(classOfAnnotation) != null || methodToInvokeOnTargetObject.getAnnotation(classOfAnnotation) != null;

                if (runInIoThread) {
                    AsyncTask<Object, Object, Object> asyncTask = new AsyncTask<Object, Object, Object>() {
                        private Object result;
                        private AsyncCallback savedAsyncCallback;

                        @Override
                        protected Object doInBackground(Object... params) {
                            AsyncCallback<Object> asyncUiCallback = new AsyncCallback<Object>() {
                                @Override
                                public void methodInvocationCompleted(Object o) {
                                    result = o;
                                }
                            };
                            int asyncCallbackArgumentIndex = 0;
                            for (Object arg : args) {
                                if (arg instanceof AsyncCallback) {
                                    savedAsyncCallback = (AsyncCallback) arg;
                                    args[asyncCallbackArgumentIndex] = asyncUiCallback;
                                    break;
                                }
                                asyncCallbackArgumentIndex += 1;
                            }
                            return safeInvoke(target, methodToInvokeOnTargetObject, args);

                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            if (this.savedAsyncCallback != null && result != null) { // this is the use case where we have
                                this.savedAsyncCallback.methodInvocationCompleted(result);
                            }
                        }
                    };

                    asyncTask.execute();

                    return null;
                }
                // otherwise simply invoke the method as usual
                return safeInvoke(target, methodToInvokeOnTargetObject, args);
            }
        };
        Object objectProxy = Proxy.newProxyInstance(target.getClass().getClassLoader(), tClass, invocationHandler);
        return (T) objectProxy;
    }

}
