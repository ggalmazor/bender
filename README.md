# Bender
Bender is a non-general-purpose, very opinionated light web server library to be used with Java 8

# Functional style
Bender uses Java 8's new Functional and Stream APIs and Optional monad. It also uses [Javaslang's](http://javaslang.com) Try monad as well.

Route handlers in Bender are supposed to always return a [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Response.java) object. [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Response.java) instances can be retrieved from the input [Request](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Request.java) object.

As all handlers are Function<[Request](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Request.java),[Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Response.java)> instances, filter chains should be implemented by the user with function decorators that could stop the chain by returning a [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Response.java) object or by returning the [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Response.java) object of the decorated Function.

It is intended that as much of the state as posible to be immutable.

# Route matching

Currently matching incoming routes thanks to [Urin](http://urin.sourceforge.net)

Placeholders for dynamic path parts can be defined prefixing them with `:` as per convention in other web libraries like [Spark](http://sparkjava.com).

Take a look into [ExampleWeb.java](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/example/ExampleWeb.java).

# JSON in, JSON out

All **input payload** is assumed to be a JSON string and all content on [Response](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Response.java) is going to be served serialized in JSON.

Input parameters travelling in the path or the query string will be merged into a params `Map<String,Object>` in the [Request](https://github.com/ggalmazor/bender/blob/master/src/main/java/com/buntplanet/bender/Request.java).

# To do

 - Should we look into Request's headers in order to produce a coherent Response?
 - Is Route Matching too slow?
 - What happens with OPTIONS requests?
