//: annotations/InterfaceExtractorProcessor.java
// APT-based annotation processing.
// {Exec: apt -factory
// annotations.InterfaceExtractorProcessorFactory
// Multiplier.java -s ../annotations}
package org.sean.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic;
import java.io.*;
import java.util.*;

@AutoService(Processor.class)
public class InterfaceExtractorProcessor
        extends AbstractProcessor {

    public static final String TIP_SRC_GENERATED = "AUTO GENERATED SOURCE, DON'T MODIFY IT.";

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

        Messager messager = processingEnv.getMessager();
        try {
            for (Element e : roundEnv.getElementsAnnotatedWith(ExtractInterface.class)) {
                if (e == null) {
                    continue;
                }

                if (e.getKind().equals(ElementKind.CLASS)) {
                    TypeElement classElement = (TypeElement) e;

                    for (Element enclosed : classElement.getEnclosedElements()) {
                        if (enclosed.getKind().equals(ElementKind.METHOD)) {
                            Set<Modifier> modifiers = enclosed.getModifiers();

                            if (modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.STATIC)) {

                                ExecutableElement execElement = (ExecutableElement) enclosed;

                                String pkg = classElement.getQualifiedName().toString();
                                pkg = pkg.substring(0, pkg.length() - 1);

                                String name = e.getAnnotation(ExtractInterface.class).value();

                                String methodName = enclosed.getSimpleName().toString();

                                MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                                // https://stackoverflow.com/questions/7763311/how-to-get-parameter-type-from-javax-lang-model-variableelement
                                // https://github.com/JakeWharton/butterknife/butterknife-compiler/src/main/java/butterknife/compiler/ButterKnifeProcessor.java
                                List<? extends VariableElement> parameters = execElement.getParameters();
                                for (VariableElement ve : parameters) {
                                    TypeMirror methodParameterType = ve.asType();
                                    if (methodParameterType instanceof TypeVariable) {
                                        TypeVariable typeVariable = (TypeVariable) methodParameterType;
                                        methodParameterType = typeVariable.getUpperBound();
                                    }

                                    builder.addParameter(
                                            TypeName.get(methodParameterType),
                                            ve.getSimpleName().toString()
                                    );
                                }
                                MethodSpec spec = builder.build();

                                TypeSpec mainDef = TypeSpec.interfaceBuilder(name)
                                        .addModifiers(Modifier.PUBLIC)
                                        .addJavadoc(TIP_SRC_GENERATED)
                                        .addMethod(spec)
                                        .build();
                                JavaFile output = JavaFile.builder(pkg, mainDef).build();
                                output.writeTo(filer);
                            }

                        }
                    }
                }
            }
        } catch (IOException exp) {
            exp.printStackTrace();
            messager.printMessage(Diagnostic.Kind.ERROR, "processing the extracting interface");
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "process complete");

        return false;
    }
} ///:~
