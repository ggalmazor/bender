# Bender
Bender is an experimental, non-general-purpose, very opinionated light web server library to be used with Java 8

**Disclaimer: You really shouldn't use Bender in your projects**

# Functional style
Bender uses Java 8's new Functional and Stream APIs and Optional monad. It also uses [Javaslang's](http://javaslang.com) Try monad as well.

Route handlers in Bender are supposed to always return a [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/bender/Response.java) object. [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/bender/Response.java) instances can be retrieved from the input [Request](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/bender/Request.java) object.

As all handlers are Function<[Request](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/bender/Request.java),[Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/bender/Response.java)> instances, filter chains should be implemented by the user with function decorators that could stop the chain by not calling the decorated handler Function.

It is intended that as much of the state as posible to be immutable.

# Route matching

Currently matching incoming routes thanks to [Urin](http://urin.sourceforge.net)

Placeholders for dynamic path parts can be defined prefixing them with `:` as per convention in other web libraries like [Spark](http://sparkjava.com).

Take a look into [ExampleWeb.java](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/example/ExampleWeb.java).

## HTTP OPTIONS

For every route that is defined you get an associated OPTIONS route for free that will respond accordingly to any OPTIONS REQUEST

# JSON in, JSON out

All **input payload** is assumed to be a JSON string and all content on [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/bender/Response.java) is going to be served serialized in JSON.

Input parameters travelling in the path or the query string will be merged into a params `Map<String,Object>` in the [Request](https://github.com/ggalmazor/bender/blob/master/src/main/java/net/programania/bender/Request.java).

# To do & think

 - Is Route Matching too slow?
 - Make CORS configurable and review how it relates to Response retrieval from Request objects
 - Check & fix immutability on all artifacts
    - Requests have mutable state but don't expose any mutation methods... Is this enough?
    - Should Responses be mutable?
 - Check & fix class and method visibility
 - Write some advanced tests
 - Think of someone using this library ad trying to write "unit" tests. Could that be achieved or they'd always be integration tests? A.K.A. is Jetty needed for testing Bender?
 - Is Bender Thread-efficient? Could Bender be adapted for Netty?
 - Should Bender only support URI objects as paths?
