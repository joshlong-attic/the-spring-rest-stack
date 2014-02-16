# The OAuth-secured REST Endpoint 

This uses the simple OAuth _Resource Owner Password flow_. Here is an example of how to accquired an `access_token`, and then use that `access_token` to update an application.

```
curl -X POST -vu android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=cowbell&username=joshlong&grant_type=password&scope=read%2Cwrite&client_secret=123456&client_id=android-crm
curl http://localhost:8080/users/5 -H "Authorization: Bearer bc2e9d2b-2d44-45cc-8e5b-6c15918d0132"
```

