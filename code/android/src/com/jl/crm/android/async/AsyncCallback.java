package com.jl.crm.android.async;


/**
 * given whenever a {@link RunAsync}-annotated method wants to receive the result of the processing
 * asynchronously.
 *
 * @param <T>
 * @author Josh Long
 */
public interface AsyncCallback<T> {
    void methodInvocationCompleted(T t)  ;
}