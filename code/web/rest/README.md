
#What Does this Module Demonstrate?

What's interesting about this module is that we've got a basic REST API. We can use the API from any HTTP client. We're taking
advantage of Spring's basic web machinery, setting status codes, handling file uploads, and serializing entities back and forth in
a fairly consistent way. For any one looking to make the leap to REST and still feel like you're working with types,
this approach might be enough. Might.

Note the handling of the status code and links in `UserController#addCustomer`.

```

    @RequestMapping(method = RequestMethod.POST, value = "/{user}/customers")
    ResponseEntity<Customer> addCustomer(@PathVariable Long user, @RequestBody Customer c) {
        Customer customer = crmService.addCustomer(user, c.getFirstName(), c.getLastName());

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{user}/customers/{customer}")
                .buildAndExpand(user, customer.getId())
                .toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uriOfNewResource);

        return new ResponseEntity<Customer>(customer, httpHeaders, HttpStatus.CREATED);
    }
```