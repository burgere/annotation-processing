# annotation-processing
This repository demonstrates how to use annotations and process them at compile-time. I drew from Eugen Baeldung's wonderful tutorial at http://www.baeldung.com/java-annotation-processing-builder, though I stray a bit from the implementation.

## Motivating Example: generating a builder
If you've ever coded your own [builder](https://sourcemaking.com/design_patterns/builder), you're familiar with its boiler-plate nature. Or, if you've used [Lombok's `@Builder` annotation](https://projectlombok.org/features/Builder.html), you might wonder how you could do something similar.

This repository demonstrates how you can write your own `@Builder` annotation, apply it to the class of your choice, and have the compiler process the annotation, resulting in a new source file for the builder that you can use.

## Using this repository
After cloning to your machine, you'll have two directories, and each is a standalone Gradle project:
- **builder** -  contains the `Builder` annotation that can be applied to a class of your choice. The `BuilderProcessor` is what's used by the Java compiler to examine your source code during compilation and produce a new source file.
- **vehicle** - contains an example project demonstrating how the `Builder` annotation can be applied to a class.

The builder is included in the **vehicle** repository as a dependency, which means Gradle must be able to find it. To facilitate this, the `maven` plugin is included in the **builder** project. This plugin gives us the `install` task, which will save the built archive into the local Maven repository:

**builder/build.gradle**
```groovy
apply plugin 'maven`
```
To install the archive to your local Maven repository, simply run the `install` task:
```
$ cd builder
$ ./gradlew install
```

The archive can then be included in any other project (e.g. **vehicle**) by including it as a compile-time dependency from the `mavenLocal()` repository:

**vehicle/build.gradle**
```groovy
repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compile 'io.rama:builder:1.0-SNAPSHOT'
}
```

## Google Auto for Annotation Processing
For the compiler to recognize annotation processors, you can use a `javac` option when invoking the compiler from the command line. As an alternative, you can use the ability of `javac` to run annotation processors that are present on the classpath by including certain metadata in the processor's archive. [Google's `@AutoService` annotation](https://github.com/google/auto/tree/master/service) will trigger the insertion of this metadata into the generated archive so that you don't have to do this manually:
**builder/src/main/java/io/rama/builder/BuilderProcessor.java**
```java
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {
...
}
```

## Building and running your code
If you make any changes to the **builder** project, you'll need to build and install that project to see your changes in the **vehicle** project:
```
$ cd builder
$ ./gradlew install
```

## Known Issues
I am not sure how to get IntelliJ to recognize the generated source (and resulting bytecode) in the **vehicle** project. This means that your IDE may not recognize `VehicleBuilder` in the following code:
```java
public class App {
    public static void main(String[] args) {
        Vehicle v = new VehicleBuilder() // Possibly not recognized by IDE
            .make("Toyota")
            .model("Corolla")
            .year(1995)
            .build();
        System.out.println(v);
    }
}
```
No worries, though - you can still run this code from the command line with Gradle tasks.
