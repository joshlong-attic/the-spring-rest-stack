package com.joshlong.spring.walkingtour.android.async;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {METHOD})
public  @interface RunOffUiThread {
}
