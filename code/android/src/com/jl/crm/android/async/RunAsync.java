package com.jl.crm.android.async;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;

/**
 * When placed on a method that's passed through {@link RunAsyncProxyCreator#runAsync(Object, Class[])},
 * triggers the execution of the method to be run using {@link android.os.AsyncTask}, which has the effect
 * of moving IO intensive operations <EM>off</EM> the UI-painting thread.
 *
 * @author Josh Long
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {METHOD})
public @interface RunAsync {
}
