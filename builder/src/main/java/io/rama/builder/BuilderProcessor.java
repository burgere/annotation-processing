package io.rama.builder;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("io.rama.builder.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // Iterate over annotations that this processor supports
        annotations.forEach(annotation -> {
            // Get annotated elements (should be classes)
            roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {

                // Get fully-qualified class name
                String qualifiedClassName = ((TypeElement)element).getQualifiedName().toString();

                // Extract package name
                int index = qualifiedClassName.lastIndexOf('.');
                String packageName = null;
                if(index > 0) {
                    packageName = qualifiedClassName.substring(0,index);
                }

                // Extract class name
                String className = qualifiedClassName.substring(index + 1);

                // Get annotation
                Builder builder = element.getAnnotation(Builder.class);

                // Construct builder class name
                String builderClassName = builder.name().isEmpty() ? className + "Builder" : builder.name();

                // Get fields (assume that all fields have associated setters)
                // field name [String] -> field type [String]
                Map<String,String> fields = element.getEnclosedElements().stream()
                        .filter(e -> e instanceof VariableElement)
                        .collect(Collectors.toMap(e -> e.getSimpleName().toString(),e -> e.asType().toString()));

                // Write source file
                try {
                    writeBuilder(className, builderClassName, packageName, fields, builder.methodName());
                } catch(IOException ex) {
                    System.err.printf("Unable to generate builder class `%s` for `%s` class",builderClassName,className);
                }
            });
        });
        return true;
    }

    private void writeBuilder(String className, String builderClassName, String packageName, Map<String,String> fields, String buildMethodName) throws IOException{

        // Create builder source file
        JavaFileObject builderSource = processingEnv.getFiler().createSourceFile(packageName + "." + builderClassName);

        try(PrintWriter out = new PrintWriter(builderSource.openWriter())) {

            // Write package name
            if(packageName != null) {
                out.printf("package %s;%n%n",packageName);
            }

            // Write class name
            out.printf("public class %s {%n",builderClassName);

            // Declare base object
            out.printf("  private %s object = new %s();%n%n",className,className);

            // Write setter methods
            fields.entrySet().forEach(field -> {
                String fieldName = field.getKey();
                String fieldType = field.getValue();
                String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                out.printf("  public %s %s(%s value) {%n",builderClassName,fieldName,fieldType);
                out.printf("    object.%s(value);%n",setterName);
                out.printf("    return this;%n");
                out.printf("  }%n%n");
            });

            // Write build method
            out.printf("  public %s %s() {%n",className,buildMethodName);
            out.printf("    return object;%n");
            out.printf("  }%n");

            // Close class
            out.print("}");
        }
    }
}
