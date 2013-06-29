package com.joshlong.spring.walkingtour.android;

import android.content.Context;
import com.joshlong.spring.walkingtour.android.async.RunOnIoThreadProxyCreator;
import com.joshlong.spring.walkingtour.android.service.*;
import com.joshlong.spring.walkingtour.android.view.activities.*;
import com.squareup.otto.Bus;
import dagger.*;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.inject.Singleton;

@Module(injects = {CustomerDetailActivity.class, CustomerListActivity.class})
public class CrmModule {

    private Crm application;
    private Context context;

    public CrmModule(Crm crm) {
        this.application = crm;
        this.context = this.application.getApplicationContext();
    }

    @Provides
    @InjectAndroidApplicationContext
    Context provideApplicationContext() {
        return this.context;
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }

    @Singleton
    @Provides
    RestTemplate provideRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        return restTemplate;
    }


    @Singleton
    @Provides
        // CustomerService is the interface, CustomerServiceClient is the implementation
    CustomerService provideCustomerService(@InjectAndroidApplicationContext Context context, RestTemplate restTemplate) {
        String baseUri = context.getString(R.string.base_uri);
        CustomerServiceClient customerServiceClient = new CustomerServiceClient(baseUri);
        customerServiceClient.setRestTemplate(restTemplate);

        CustomerService customerServiceProxy = RunOnIoThreadProxyCreator.runOnIoThread(customerServiceClient, CustomerService.class);

        return customerServiceProxy;
    }

}