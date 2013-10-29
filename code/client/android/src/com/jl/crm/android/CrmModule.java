package com.jl.crm.android;

import android.content.Context;
import android.os.Build;

import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.android.activities.fragments.SignInFragment;
import com.jl.crm.android.activities.fragments.SignOutFragment;
import com.jl.crm.android.utils.AndroidUiThreadUtils;
import com.jl.crm.client.CrmApiAdapter;
import com.jl.crm.client.CrmConnectionFactory;
import com.jl.crm.client.CrmOperations;
import com.jl.crm.client.CrmServiceProvider;
import com.jl.crm.client.CrmTemplate;

import dagger.Module;
import dagger.Provides;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

import javax.inject.Singleton;

@Module(injects = {MainActivity.class, SignInFragment.class, SignOutFragment.class})
public class CrmModule {
    private Crm application;

    public CrmModule(Crm crm) {
        this.application = crm;
    }

    @Provides
    @Singleton
    CrmOperations crmOperations(final SQLiteConnectionRepository sqLiteConnectionRepository) {
        try {
            Class<CrmOperations> crmOperationsClass = CrmOperations.class;
            CrmOperations ops = sqLiteConnectionRepository.getPrimaryConnection(crmOperationsClass).getApi();
            return AndroidUiThreadUtils.runOffUiThread(ops, crmOperationsClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    SQLiteConnectionRepository sqLiteConnectionRepository(SQLiteConnectionRepositoryHelper sqLiteConnectionRepositoryHelper,
                                                          ConnectionFactoryRegistry connectionFactoryRegistry) {
        TextEncryptor textEncryptor = AndroidEncryptors.text("password", "5c0744940b5c369b");
        return new SQLiteConnectionRepository(sqLiteConnectionRepositoryHelper, connectionFactoryRegistry, textEncryptor);
    }

    @Provides
    @Singleton
    ConnectionFactoryRegistry connectionFactoryRegistry() {
        return new ConnectionFactoryRegistry();
    }

    @Provides
    @Singleton
    SQLiteConnectionRepositoryHelper repositoryHelper(@InjectAndroidApplicationContext Context context) {
        return new SQLiteConnectionRepositoryHelper(context);
    }

    @Provides
    @Singleton
    CrmApiAdapter apiAdapter() {
        return new CrmApiAdapter();
    }

    @Provides
    @Singleton
    CrmServiceProvider serviceProvider(@InjectAndroidApplicationContext Context c) {

        String baseUrl;
        // "google_sdk" OR "sdk" are emulators
        if (Build.PRODUCT.contains("sdk") || Build.PRODUCT.equals("full_x86")) {
            baseUrl = c.getString(R.string.base_uri_emulator);
        } else {
            baseUrl = c.getString(R.string.base_uri_qa);
        }
        String clientId = c.getString(R.string.oauth_client_id);
        String clientSecret = c.getString(R.string.oauth_client_secret);
        String accessTokenUri = fullUrl(baseUrl, c.getString(R.string.oauth_token_uri));
        String authorizeUri = fullUrl(baseUrl, c.getString(R.string.oauth_authorize_uri));
        return new CrmServiceProvider(baseUrl, clientId, clientSecret, authorizeUri, accessTokenUri);
    }

/*	@Provides
    LayoutInflater layoutInflater(@InjectAndroidApplicationContext Context context) {
		return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}*/


    @Provides
    @Singleton
    CrmConnectionFactory crmConnectionFactory(ConnectionFactoryRegistry connectionFactoryRegistry,
                                              CrmServiceProvider crmServiceProvider,
                                              CrmApiAdapter crmApiAdapter) {
        CrmConnectionFactory crmConnectionFactory = new CrmConnectionFactory(crmServiceProvider, crmApiAdapter);
        connectionFactoryRegistry.addConnectionFactory(crmConnectionFactory);
        return crmConnectionFactory;
    }

    @Provides
    @Singleton
    @InjectAndroidApplicationContext
    Context provideApplicationContext() {
        return this.application.getApplicationContext();
    }

    private String fullUrl(String baseUrl, String end) {
        String base = !baseUrl.endsWith("/") ? baseUrl + "/" : baseUrl;
        String newEnd = end.startsWith("/") ? end.substring(1) : end;
        return base + newEnd;
    }
}
