# The Spring REST Stack

This code accompanies a talk that I deliver on **RESTful service development with Spring**. 
* There are online versions of the talk. Notably, I presented it as a [webinar on the SpringSourceDev channel](http://www.youtube.com/watch?v=SC0FPuDKei0).
* See [the slides on my SlideShare.net page](http://www.slideshare.net/joshlong/rest-apis-with-spring). 
* This code lives at [my GitHub page (joshlong) for this project `the-spring-rest-stack`](http://github.com/joshlong/the-spring-rest-stack/code).

The goals of this project are to demonstrate the development of a simple REST service with Spring. The REST service concerns itself with the domain of a simple CRM: a `user` manages a  `customer` collection. 


## Breakdown of the Various Tiers
### The Data and Service Tier
The data tier module `services` uses [Spring Data JPA](http://www.springsource.org/spring-data/jpa) [repositories](http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/repositories.html), a standard SQL database, and Spring's standard transaction management infrastructure to build a service. This tier is configured by importing the class `com.jl.crm.services.ServiceConfiguration`. Currently there are two [Spring profiles](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/new-in-3.1.html#new-in-3.1-bean-definition-profiles). One called `production`, which attempts to the SQL database configured in `/code/services/src/main/resources/config.properties`, and `test`, which manages an embedded H2 database which can be accessed at  the [embedded H2 database console web application](http://localhost:8080/h2/) with the configuration shown below.

![H2 database console](https://raw.github.com/joshlong/the-spring-rest-stack/master/docs/images/h2_database_console_configuration.png "The H2 Database Console configuration")

### The REST Tier 
There are four web-tier modules, `rest`, `hateoas`, `hateoas-data`, and `oauth`, which demonstrate the evolution of a REST service implemented using Spring MVC to manage `users` and `customers`. The initial cut, `rest`, is fairly RESTful, but not [HATEOAS](http://en.wikipedia.org/wiki/HATEOAS) and [hypermedia](http://www.wikipedia.org/wiki/hypermedia) compliant. The next cut, `hateoas` uses [Spring HATEOAS](https://github.com/SpringSource/spring-hateoas) to introduce hypermedia. This improvement, while welcome, is not without an associated cost in code. The next cut, `hateoas-data`, introduces [Spring Data REST](https://github.com/SpringSource/Spring-Data-REST) which reduces boilerplate code associated with managing data from the REST endpoint to the database. In the final cut, `oauth`, we introduce [Spring Security](http://github.com/SpringSource/Spring-Security) and [Spring Security OAuth](http://www.github.com/SpringSource/Spring-Security-OAuth) to secure the API's from both prying eyes and unauthorized application clients. 

### The Client Tier
The `social` module is a Spring Social API binding for the OAuth 2.0-secured CRM REST API. The [`ClientExample`](https://github.com/joshlong/the-spring-rest-stack/blob/master/code/social/src/main/java/com/jl/crm/client/ClientExample.java) demonstrates how to configure and use the Spring Social binding outside of a web container and demonstrates some of its uses. 

The `android` module is a [Spring Android](http://github.com/SpringSource/spring-android)-powered Android client that embeds the `social` client and uses it to support an [Android mobile application](http://www.google.com/mobile/android/).

## Notes on Implementation 
For a detailed walkthrough of all the code, please check [out the tutorial](https://github.com/joshlong/the-spring-rest-stack/blob/master/tutorial.asc).
