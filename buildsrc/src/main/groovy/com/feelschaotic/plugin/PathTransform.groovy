package com.feelschaotic.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * @author feelschaotic
 * @create 2018/11/3.
 */

class PathTransform extends Transform {

    Project project
    TransformOutputProvider outputProvider

    // 构造函数，我们将Project保存下来备用
    public PathTransform(Project project) {
        this.project = project
    }

    // 设置我们自定义的Transform对应的Task名称
    @Override
    String getName() {
        return "PathTransform"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        this.outputProvider = outputProvider
        traversalInputs(inputs)
    }

    /**
     * Transform的inputs有两种类型:
     *  一种是目录， DirectoryInput
     *  一种是jar包，JarInput
     *  要分开遍历
     */
    private ArrayList<TransformInput> traversalInputs(Collection<TransformInput> inputs) {
        inputs.each {
            TransformInput input ->
                traversalDirInputs(input)
                traversalJarInputs(input)
        }
    }

    /**
     * 对类型为文件夹的input进行遍历
     */

    private ArrayList<DirectoryInput> traversalDirInputs(TransformInput input) {
        input.directoryInputs.each {
            /**
             * 文件夹里面包含的是
             *  我们手写的类
             *  R.class、
             *  BuildConfig.class
             *  R$XXX.class
             *  等
             *  根据自己的需要对应处理
             */
            println("it == ${it}")

            //注入代码buildSrc\src\main\groovy\demo
            TryCatchInject.injectDir(it.file.absolutePath, "com\\feelschaotic\\javassist")

            // 获取output目录
            def dest = outputProvider.getContentLocation(it.name
                    , it.contentTypes, it.scopes, Format.DIRECTORY)

            // 将input的目录复制到output指定目录
            FileUtils.copyDirectory(it.file, dest)
        }
    }

    /**
     * 对类型为jar文件的input进行遍历
     */
    private ArrayList<JarInput> traversalJarInputs(TransformInput input) {
        input.jarInputs.each {
            JarInput jarInput ->
                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
        }
    }
}
