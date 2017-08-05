package org.sean.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"org.sean.processor.TestAnnotation"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MyProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        System.out.println("Test log in MyProcessor.process");
        System.out.println(roundEnv.toString());

        for (TypeElement typeElement : annotations) {
            // 遍历annotations获取annotation类型
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                // 使用roundEnv.getElementsAnnotatedWith获取所有被某一类型注解标注的元素，依次遍历
                // 在元素上调用接口获取注解值
                int annoValue = element.getAnnotation(TestAnnotation.class).value();
                String annoWhat = element.getAnnotation(TestAnnotation.class).what();

                System.out.println("value = " + annoValue);
                System.out.println("what = " + annoWhat);

                // 向当前环境输出warning信息
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "value = " + annoValue + ", what = " + annoWhat, element);
            }
        }
        return false;
    }
}
