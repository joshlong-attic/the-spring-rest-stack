package com.jl.crm.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@ComponentScan
@Configuration
@EnableJpaRepositories
public class ServiceConfiguration {

    public static final String CRM_NAME = "crm";
    /**
     * The root directory to which all uploads for the application are uploaded.
     */
    public static final File CRM_STORAGE_DIRECTORY = new File(
            System.getProperty("user.home"), CRM_NAME);
    /**
     * Things are first uploaded by the application server to this directory. it's a sort
     * of staging directory
     */
    public static final File CRM_STORAGE_UPLOADS_DIRECTORY = new File(CRM_STORAGE_DIRECTORY, "uploads");
    /**
     * When a profile photo is uploaded, the resultant, completely uploaded image is
     * stored in this directory
     */
    public static final File CRM_STORAGE_PROFILES_DIRECTORY = new File(CRM_STORAGE_DIRECTORY, "profiles");

    @PostConstruct
    protected void setupStorage() throws Throwable {
        File[] files = {CRM_STORAGE_DIRECTORY, CRM_STORAGE_UPLOADS_DIRECTORY, CRM_STORAGE_PROFILES_DIRECTORY};
        for (File f : files) {
            if (!f.exists() && !f.mkdirs()) {
                String msg = String.format("you must create the profile photos directory ('%s') " +
                        "and make it accessible to this process. Unable to do so from this process.", f.getAbsolutePath());
                throw new RuntimeException(msg);
            }
        }
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(  JpaVendorAdapter adapter, DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan(User.class.getPackage().getName());
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(adapter);
        return emf;
    }

    @Bean
    PlatformTransactionManager transactionManager( EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Configuration
    @Profile({"default", "test"})
    static class DefaultDataSourceConfiguration {

        private Log log = LogFactory.getLog(getClass());

        @PostConstruct
        protected void setupTestProfileImages() throws Exception {
            long userId = 5;
            File profilePhotoForUser5 = new File(ServiceConfiguration.CRM_STORAGE_PROFILES_DIRECTORY, Long.toString(userId));
            if (!profilePhotoForUser5.exists()) {
                // copy the profile photo back
                String pathForProfilePhoto = "/sample-photos/spring-dog-2.png";
                ClassPathResource classPathResource = new ClassPathResource(pathForProfilePhoto);
                Assert.isTrue(classPathResource.exists(), "the resource " + pathForProfilePhoto + " does not exist");
                OutputStream outputStream = new FileOutputStream(profilePhotoForUser5);
                InputStream inputStream = classPathResource.getInputStream();
                try {
                    IOUtils.copy(inputStream, outputStream);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                }
                log.debug("setup photo " + profilePhotoForUser5.getAbsolutePath() + " for the sample user #" + Long.toString(userId) + "'s profile photo.");
            }
            if (!profilePhotoForUser5.exists()) {
                throw new RuntimeException("couldn't setup profile photos at " + profilePhotoForUser5.getAbsolutePath() + ".");
            }
        }

    }
}

