package co.infinum.collar.plugin.utils

import com.android.build.gradle.BasePlugin
import com.android.build.gradle.internal.api.dsl.extensions.BaseExtension2
import com.android.build.gradle.internal.scope.TaskContainer
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.variant.BaseVariantData
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import java.io.File
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

fun javaTask(baseVariantData: BaseVariantData): JavaCompile {

    /**
     *  Supporting gradle api 3.1.+
     */
    val variantJavacTaskProp = baseVariantData::class.memberProperties.find { prop ->
        prop.name == "javacTask" && prop.returnType.classifier == JavaCompile::class
    }

    variantJavacTaskProp?.let {
        return variantJavacTaskProp.call(baseVariantData) as JavaCompile
    }

    /**
     *  Supporting gradle api 3.2.+
     */
    val taskContainerFunc = baseVariantData::class.functions.find { func ->
        func.name == "getTaskContainer"
    }
    taskContainerFunc?.let {
        val containerResult = taskContainerFunc.call(baseVariantData) as TaskContainer
        val javacTaskProp = containerResult::class.memberProperties.find { prop ->
            prop.name == "javacTask"
        }

        if (javacTaskProp?.returnType?.classifier == JavaCompile::class) {
            return javacTaskProp.call(containerResult) as JavaCompile
        }
    }

    /**
     *  Supporting gradle api 3.3.+ by default
     */
    return baseVariantData.taskContainer.javacTask.get()
}

fun variantDataList(plugin: BasePlugin<out BaseExtension2>): List<BaseVariantData> {
    return variantScopes(plugin).map(VariantScope::getVariantData)
}

fun variantScopes(plugin: BasePlugin<out BaseExtension2>): List<VariantScope> {
    return plugin.variantManager.variantScopes
}