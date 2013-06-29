package com.joshlong.spring.walkingtour.android;

import javax.inject.Qualifier;

/**
 * creates a unique binding so it's clear at injection-sites that we want the Android application context, not an Activity Context.
 **/
@Qualifier
public @interface InjectAndroidApplicationContext {
}
