package com.jl.crm.services;

import org.apache.commons.lang.SystemUtils;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Driver;
import java.util.Collections;

@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan
@PropertySource ("/config.properties")
@Configuration
public class ServiceConfiguration {

	public static final File CRM_STORAGE_DIRECTORY = new File(SystemUtils.getUserHome(), "crm");
	public static final File CRM_STORAGE_UPLOADS_DIRECTORY = new File(CRM_STORAGE_DIRECTORY, "uploads");
	public static final File CRM_STORAGE_PROFILES_DIRECTORY = new File(CRM_STORAGE_DIRECTORY, "profiles");

	@PostConstruct
	public void setupStorage() throws Throwable {

		File[] files = new File[]{CRM_STORAGE_DIRECTORY, CRM_STORAGE_PROFILES_DIRECTORY, CRM_STORAGE_UPLOADS_DIRECTORY};
		for (File f : files) {
			if (!f.exists() && !f.mkdirs()){
				throw new RuntimeException("you must create the profile " +
				                           "photos directory, " + f.getAbsolutePath() +
				                           ". Unable to do so from this process.");
			}
		}
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.POSTGRESQL);
		adapter.setGenerateDdl(true);
		adapter.setShowSql(true);

		HibernateJpaDialect dialect = new HibernateJpaDialect();

		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPackagesToScan(User.class.getPackage().getName());
		emf.setDataSource(dataSource);
		emf.setJpaPropertyMap(Collections.singletonMap("hibernate.hbm2ddl.auto", "create-update"));
		emf.setJpaDialect(dialect);
		emf.setJpaVendorAdapter(adapter);
		return emf;
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

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
}
