# microBean™ [Jackson](https://github.com/FasterXML/jackson) CDI Integration

The microBean™ Jackson CDI Integration project provides a CDI portable
extension that allows
[`ObjectMapper`](https://fasterxml.github.io/jackson-databind/javadoc/2.10/com/fasterxml/jackson/databind/ObjectMapper.html)
instances to be injected in your CDI-based application.

## Installation

Declare a dependency in your CDI-based Maven project:

```
<dependency>
  <groupId>org.microbean</groupId>
  <artifactId>microbean-jackson-cdi</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

Inject an `ObjectMapper` into a CDI bean:

```
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import com.fasterxml.jackson.databind.ObjectMapper;

// This will be the default ObjectMapper.
@Inject
private ObjectMapper objectMapper;

// This will be the same ObjectMapper as above.
@Inject
@Default
private ObjectMapper defaultObjectMapper;

// This will be another ObjectMapper.
@Inject
@YourQualifierHere
private ObjectMapper specialObjectMapper;
```

## Customization

To customize the `ObjectMapper` created by the portable extension
furnished by this project, observe an `ObjectMapper`-typed CDI event:

```
private static final void onDefaultObjectMapperCreation(@Observes final ObjectMapper objectMapperBeingCreated) {
  // The ObjectMapper received here is the default one and is in the process of
  // being registered in application scope.  You may customize it here, but do
  // not retain a reference to it.  Here is an arbitrary example.
  objectMapperBeingCreated.setLocale(java.util.Locale.getDefault());
}

private static final void onQualifiedObjectMapperCreation(@Observes @YourQualifierHere final ObjectMapper yellowObjectManagerBeingCreated) {
  // The ObjectMapper received here is the @YourQualifierHere-qualified one from
  // the example above and is in the process of being registered in application
  // scope.  You may customize it here, but do not retain a reference to it.
  // Here is an arbitrary example.
  yellowObjectMapperBeingCreated.setTimeZone(java.util.TimeZone.getTimeZone("GMT-8"));
}
```
