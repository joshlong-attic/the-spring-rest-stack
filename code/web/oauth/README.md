# The OAuth-secured REST Endpoint 

This uses the simple OAuth _Resource Owner Password flow_. Here is an example of how to accquired an `access_token`, and then use that `access_token` to update an application.

```
curl -X POST -vu android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=cowbell&username=joshlong&grant_type=password&scope=read%2Cwrite&client_secret=123456&client_id=android-crm
curl http://localhost:8080/users/5 -H "Authorization: Bearer bc2e9d2b-2d44-45cc-8e5b-6c15918d0132"
```

# The SSL Terminated REST Endpoint

This also demonstrates how easy it is to setup SSL on Apache Tomcat (embedded or otherwise!)

Working example of spring boot with embedded tomcat running ssl with a self-signed cert from project root, `keystore.p12`, created with:

```
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
```

Example uses password `localhost`. You can run this application with SSL turned on by activating the `production` profile, such as with: `-Dspring.profiles.active=production`

If you're using the SSL support, then be sure to change the `curl` incantations above to match: e.g.: `https://localhost:8443/users/5`, instead of `http://localhost:8443/users/5`