package com.feelschaotic.plugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.MethodInfo
import javassist.bytecode.annotation.ArrayMemberValue
import javassist.bytecode.annotation.ClassMemberValue

import java.lang.annotation.Annotation

/**
 * @author feelschaotic
 * @create 2018/11/3.
 */

class TryCatchInject {
    private static String path
    private static ClassPool pool = ClassPool.getDefault()

    private static final String CLASS_SUFFIX = ".class"
    private static
    final String PROCESSED_ANNOTATION_NAME = "com.feelschaotic.javassist.annotations.AutoTryCatch"

    static void injectDir(String path, String packageName) {
        this.path = path
        pool.appendClassPath(path)
        traverseFile(packageName)
    }

    private static traverseFile(String packageName) {
        File dir = new File(path)
        if (!dir.isDirectory()) {
            return
        }
        beginTraverseFile(dir, packageName)
    }

    private static beginTraverseFile(File dir, packageName) {
        dir.eachFileRecurse { File file ->

            String filePath = file.absolutePath
            if (isClassFile(filePath)) {
                int index = filePath.indexOf(packageName.replace(".", File.separator).replace("/", File.separator).replace("\\", File.separator))
                boolean isClassFilePath = index != -1
                if (isClassFilePath) {
                    transformPathAndInjectCode(filePath, index)
                }
            }
        }
    }

    private static boolean isClassFile(String filePath) {
        return filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")
    }

    private static void transformPathAndInjectCode(String filePath, int index) {
        String className = getClassNameFromFilePath(filePath, index)
        injectCode(className)
    }

    private static String getClassNameFromFilePath(String filePath, int index) {
        int end = filePath.length() - CLASS_SUFFIX.length()
        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
        className
    }

    private static void injectCode(String className) {
        CtClass c = pool.getCtClass(className)
        //  println("CtClass:" + c)
        defrostClassIfFrozen(c)
        traverseMethod(c)

        c.writeFile(path)
        c.detach()
    }

    private static void traverseMethod(CtClass c) {
        CtMethod[] methods = c.getDeclaredMethods()
        for (ctMethod in methods) {
            // println("ctMethod:" + ctMethod)
            traverseAnnotation(ctMethod)
        }
    }

    private static void traverseAnnotation(CtMethod ctMethod) {
        Annotation[] annotations = ctMethod.getAnnotations()

        for (annotation in annotations) {
            def canonicalName = annotation.annotationType().canonicalName
            if (isSpecifiedAnnotation(canonicalName)) {
                onIsSpecifiedAnnotation(ctMethod, canonicalName)
            }
        }
    }

    private static boolean isSpecifiedAnnotation(String canonicalName) {
        PROCESSED_ANNOTATION_NAME.equals(canonicalName)
    }

    private static void onIsSpecifiedAnnotation(CtMethod ctMethod, String canonicalName) {
        MethodInfo methodInfo = ctMethod.getMethodInfo()
        AnnotationsAttribute attribute = methodInfo.getAttribute(AnnotationsAttribute.visibleTag)
        javassist.bytecode.annotation.Annotation javassistAnnotation = attribute.getAnnotation(canonicalName)
        def names = javassistAnnotation.getMemberNames()
        if (names == null || names.isEmpty()) {
            catchAllExceptions(ctMethod)
            return
        }
        catchSpecifiedExceptions(ctMethod, names, javassistAnnotation)
    }

    private static catchAllExceptions(CtMethod ctMethod) {
        CtClass etype = pool.get("java.lang.Exception")
        ctMethod.addCatch('{com.feelschaotic.javassist.Logger.print($e);return;}', etype)
    }

    private
    static void catchSpecifiedExceptions(CtMethod ctMethod, Set names, javassist.bytecode.annotation.Annotation javassistAnnotation) {
        names.each { def name ->

            ArrayMemberValue arrayMemberValues = (ArrayMemberValue) javassistAnnotation.getMemberValue(name)
            if (arrayMemberValues == null) {
                return
            }
            addMultiCatch(ctMethod, (ClassMemberValue[]) arrayMemberValues.getValue())
        }
    }

    private static void addMultiCatch(CtMethod ctMethod, ClassMemberValue[] classMemberValues) {
        classMemberValues.each { ClassMemberValue classMemberValue ->
            CtClass etype = pool.get(classMemberValue.value)
            ctMethod.addCatch('{ com.feelschaotic.javassist.Logger.print($e);return;}', etype)
        }
    }


    private static void defrostClassIfFrozen(CtClass c) {
        if (c.isFrozen()) {
            c.defrost()
        }
    }

}
