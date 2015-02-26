package com.jl.crm.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
	    SpringApplication.run(Application.class);
	}

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


	@Configuration
	@Profile({"default", "test"})
	static class DefaultDataSourceConfiguration {

		private Log log = LogFactory.getLog(getClass());

		@PostConstruct
		public void begin ()
				throws Exception {

			long userId = 5;

			File profilePhotoForUser5 = new File(
					Application.CRM_STORAGE_PROFILES_DIRECTORY,
					Long.toString(userId));

			if (!profilePhotoForUser5.exists()) {
				String pathForProfilePhoto = "/sample-photos/spring-dog-2.png";
				Resource classPathResource = new ClassPathResource(pathForProfilePhoto);
				Assert.isTrue(classPathResource.exists(),
						"the resource " + pathForProfilePhoto + " does not exist");

				try (InputStream i = classPathResource.getInputStream();
				     OutputStream o = new FileOutputStream(pathForProfilePhoto)) {
					byte[] buf = new byte[1024];
					int bytesRead;
					while ((bytesRead = i.read(buf)) > 0) {
						o.write(buf, 0, bytesRead);
					}
				}
				log.debug("setup photo " + profilePhotoForUser5.getAbsolutePath()
						+ " for the sample user #" + Long.toString(userId) + "'s profile photo.");
			}
			if (!profilePhotoForUser5.exists()) {
				throw new RuntimeException("couldn't setup profile photos at " + profilePhotoForUser5.getAbsolutePath() + ".");
			}
		}

	}
}
