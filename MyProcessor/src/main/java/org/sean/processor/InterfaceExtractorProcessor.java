//: annotations/InterfaceExtractorProcessor.java
// APT-based annotation processing.
// {Exec: apt -factory
// annotations.InterfaceExtractorProcessorFactory
// Multiplier.java -s ../annotations}
package org.sean.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.*;
import java.util.*;

@AutoService(Processor.class)
public class InterfaceExtractorProcessor
        extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> strings = new HashSet<>();
        strings.add(ExtractInterface.class.getCanonicalName());
        return strings;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Test log in InterfaceExtractorProcessor.process");

        try {
            for (Element e : roundEnv.getElementsAnnotatedWith(ExtractInterface.class)) {
                if (e != null) {
                    if (e.getKind().equals(ElementKind.CLASS)) {
                        TypeElement classElement = (TypeElement) e;
                        //PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                        for (Element enclosed : classElement.getEnclosedElements()) {
                            if (enclosed.getKind().equals(ElementKind.METHOD)) {
                                Set<Modifier> modifiers = enclosed.getModifiers();

                                if (modifiers.contains(Modifier.PUBLIC) &&
                                        !modifiers.contains(Modifier.STATIC)) {

                                    ExecutableElement execElement = (ExecutableElement) enclosed;

                                    String pkg = classElement.getQualifiedName().toString();
                                    pkg = pkg.substring(0, pkg.length() - 1);
                                    String name = e.getAnnotation(ExtractInterface.class).value();

                                    String methodName = enclosed.getSimpleName().toString();

                                    MethodSpec spec = MethodSpec.methodBuilder(methodName)
                                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                            .addParameter(TypeName.INT, "x")
                                            .addParameter(TypeName.INT, "y")
                                            .build();
                                    TypeSpec mainDef = TypeSpec.interfaceBuilder(name)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addJavadoc("AUTO GENATRATED, DON'T MODIFY IT.")
                                            .addMethod(spec)
                                            .build();
                                    JavaFile output = JavaFile.builder(pkg, mainDef).build();
                                    output.writeTo(filer);
                                }

                            }
                        }
                    }

                }
            }
        } catch (IOException exp) {
            exp.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "processing the extracting interface");
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "process complete");

        return false;
    }
} ///:~
