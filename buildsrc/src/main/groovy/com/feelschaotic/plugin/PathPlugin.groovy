package com.feelschaotic.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

/**
 * @author feelschaotic
 * @create 2018/11/3.
 */

class PathPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.logger.debug "================自定义插件成功！=========="

        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new PathTransform(project))
    }
}
