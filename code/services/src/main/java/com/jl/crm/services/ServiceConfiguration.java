package com.jl.crm.services;

import org.apache.commons.lang.SystemUtils;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Driver;

@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan
@PropertySource ("/config.properties")
@Configuration
public class ServiceConfiguration {

	public static final String CRM_NAME = "crm";
	public static final File CRM_STORAGE_DIRECTORY = new File(SystemUtils.getUserHome(), CRM_NAME);
	public static final File CRM_STORAGE_UPLOADS_DIRECTORY = new File(CRM_STORAGE_DIRECTORY, "uploads");
	public static final File CRM_STORAGE_PROFILES_DIRECTORY = new File(CRM_STORAGE_DIRECTORY, "profiles");

	@PostConstruct
	public void setupStorage() throws Throwable {

		File[] files = {CRM_STORAGE_DIRECTORY, CRM_STORAGE_PROFILES_DIRECTORY, CRM_STORAGE_UPLOADS_DIRECTORY};
		for (File f : files) {
			if (!f.exists() && !f.mkdirs()){
				throw new RuntimeException("you must create the profile " +
				                           "photos directory, " + f.getAbsolutePath() +
				                           ". Unable to do so from this process.");
			}
		}
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(HibernateJpaVendorAdapter adapter, DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPackagesToScan(User.class.getPackage().getName());
		emf.setDataSource(dataSource);
		emf.setJpaVendorAdapter(adapter);
		return emf;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
}

@Configuration
@Profile ("production")
class ProductionDataSourceConfiguration {
	@Bean
	public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.POSTGRESQL);
		adapter.setGenerateDdl(true);
		adapter.setShowSql(true);
		return adapter;
	}

	@Bean
	public DataSource dataSource(Environment env) {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(env.getPropertyAsClass("dataSource.driverClass", Driver.class));
		dataSource.setUrl(env.getProperty("dataSource.url").trim());
		dataSource.setUsername(env.getProperty("dataSource.user").trim());
		dataSource.setPassword(env.getProperty("dataSource.password").trim());
		return dataSource;
	}
}

@Configuration
@Profile ("default")
class EmbeddedDataSourceConfiguration {
	@Bean
	public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.H2);
		adapter.setGenerateDdl(true);
		adapter.setShowSql(true);
		return adapter;
	}

	/**
	 * You can access this H2 database at <a href = "http://localhost:8080/admin/console">the H2 administration
	 * console</a>.
	 */
	@Bean
	public DataSource dataSource() {

		String applicationName = ServiceConfiguration.CRM_NAME;

		EmbeddedDatabaseFactory embeddedDatabaseFactory = new EmbeddedDatabaseFactory();
		embeddedDatabaseFactory.setDatabaseName(applicationName);
		embeddedDatabaseFactory.setDatabaseType(EmbeddedDatabaseType.H2);

		ClassPathResource classPathResource = new ClassPathResource("/" + applicationName + "-schema-h2.sql");

		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(classPathResource);
		embeddedDatabaseFactory.setDatabasePopulator(resourceDatabasePopulator);

		return embeddedDatabaseFactory.getDatabase();
	}
}
