package com.curious.vbp.processors;

import com.curious.vbp.annotation.viewbinder.BindView;
import com.curious.vbp.annotation.viewbinder.OnClick;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.*;

public class VbpProcessor extends AbstractProcessor {

    private static final String BINDING_CLASS_SUFFIX = "$$VBP";
    private Filer mFiler;
    private Types typeUtils;
    private Elements elementUtills;
    private Messager messager;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mFiler = processingEnvironment.getFiler();
        elementUtills = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
        Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<>();

        System.out.println("=====1111" );
        //OnClick Annotation
        for (Element element : roundEnvironment.getElementsAnnotatedWith(OnClick.class)) {
            if (simpleCheck(element, OnClick.class, messager)) {
                String name = element.getSimpleName().toString();
                int[] resIds = element.getAnnotation(OnClick.class).value();
                System.out.println("=====" + name + "===" + Arrays.toString(resIds));

                TypeElement enclosingType = (TypeElement) element.getEnclosingElement();
                BindingClass bindingClass = targetClassMap.get(enclosingType);
                if (bindingClass == null) {
                    String targetType = enclosingType.getQualifiedName().toString();
                    String classPackage = elementUtills.getPackageOf(enclosingType).getQualifiedName().toString();
                    int packageLen = classPackage.length() + 1;
                    String className = targetType.substring(packageLen).replace('.', '$') + BINDING_CLASS_SUFFIX;
                    String classFqcn = classPackage + "." + className;
                    bindingClass = new BindingClass(classPackage, className, targetType, classFqcn);
                }

                bindingClass.addOnClickBinding(new OnclickResourceBinding(resIds, name));

                targetClassMap.put(enclosingType, bindingClass);
                erasedTargetNames.add(enclosingType);
            }
        }

        //BindView Annotation
        for(Element element:roundEnvironment.getElementsAnnotatedWith(BindView.class)){
            if (simpleCheck(element, BindView.class, messager)) {
                String name = element.getSimpleName().toString();
                int resId = element.getAnnotation(BindView.class).value();
                System.out.println("=====" + name + "===" +resId+"==="+elementUtills.getPackageOf(element).getQualifiedName());

                System.out.println(TypeName.get((Type) element).toString());



//                TypeElement enclosingType = (TypeElement) element.getEnclosingElement();
//                BindingClass bindingClass = targetClassMap.get(enclosingType);
//                if (bindingClass == null) {
//                    String targetType = enclosingType.getQualifiedName().toString();
//                    String classPackage = elementUtills.getPackageOf(enclosingType).getQualifiedName().toString();
//                    int packageLen = classPackage.length() + 1;
//                    String className = targetType.substring(packageLen).replace('.', '$') + BINDING_CLASS_SUFFIX;
//                    String classFqcn = classPackage + "." + className;
//                    bindingClass = new BindingClass(classPackage, className, targetType, classFqcn);
//                }
//
//                bindingClass.addBindViewBinding(new BindViewResourceBinding(resId, name));
//
//                targetClassMap.put(enclosingType, bindingClass);
//                erasedTargetNames.add(enclosingType);
            }
        }

        for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
            //TypeElement typeElement = entry.getKey();
            BindingClass bindingClass = entry.getValue();
            try {
                bindingClass.brewJava().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> result = new HashSet<>();
        result.add(BindView.class.getCanonicalName());
        result.add(OnClick.class.getCanonicalName());
        return result;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }



    private boolean simpleCheck(Element element, Class<? extends Annotation> annotationClass, Messager messager) {
        Set<Modifier> modifiers = element.getModifiers();
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        //can not be private or static
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
            printError(messager, element, "@%s %s must not be private or static.(%s %s)",
                    annotationClass.getSimpleName(), "fields", enclosingElement.getQualifiedName(), element.getSimpleName());
            return false;
        }
        //only used in Class
        if (enclosingElement.getKind() != ElementKind.CLASS) {
            printError(messager, element, "@%s %s can only be used in classes.(%s %s)",
                    annotationClass.getSimpleName(), "fields", enclosingElement.getQualifiedName(), element.getSimpleName());
            return false;
        }
        if (enclosingElement.getModifiers().contains(Modifier.PRIVATE)) {
            printError(messager, element, "@%s %s can not be contained in private class.(%s %s)",
                    annotationClass.getSimpleName(), "fields", enclosingElement.getQualifiedName(), element.getSimpleName());
            return false;
        }
        String qualifiedName = enclosingElement.getQualifiedName().toString();
        if (qualifiedName.startsWith("android.")) {
            printError(messager, element, "@%s-annotated class incorrectly in Android framework package.(%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }
        if (qualifiedName.startsWith("java.")) {
            printError(messager, element, "@%s-annotated class incorrectly in Java framework package.(%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }
        return true;
    }


    private void printError(Messager messager, Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
