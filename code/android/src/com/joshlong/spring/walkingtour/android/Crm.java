package com.joshlong.spring.walkingtour.android;


import android.app.*;
import dagger.ObjectGraph;

import java.util.*;

/**
 * the current Android Application instance.
 *
 * @author Josh Long
 */
public class Crm extends Application {
    private ObjectGraph objectGraph;
    public static Crm forActivity(Activity a ){
        return (Crm) a.getApplication();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Object[] modules = getModules().toArray();
        objectGraph = ObjectGraph.create(modules);
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(
                new CrmModule(this)
        );
    }

    public ObjectGraph getObjectGraph() {
        return this.objectGraph;
    }


    public void    inject ( Object crmComponent ){
    this.objectGraph.inject(crmComponent);
     }

}
