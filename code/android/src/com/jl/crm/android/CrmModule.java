package com.jl.crm.android;

//@Module (injects = { CrmWebOAuthActivity.class})
public class CrmModule {
/*
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

//    // CustomerService is the interface, CustomerServiceClient is the implementation
//    @Singleton
//    @Provides
//    CustomerService provideCustomerService(@InjectAndroidApplicationContext Context context, RestTemplate restTemplate) {
//        String baseUri = context.getString(R.string.base_uri);
//        CustomerServiceClient customerServiceClient = new CustomerServiceClient(baseUri);
//        customerServiceClient.setRestTemplate(restTemplate);
//
//        return RunAsyncProxyCreator.runAsync(customerServiceClient, CustomerService.class);
//    }*/

}