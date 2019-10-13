package co.infinum.collar.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project

class CollarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def isAppProject = project.plugins.withType(AppPlugin)
        def isLibProject = project.plugins.withType(LibraryPlugin)

        final def log = project.logger
        def variants
        if (isAppProject) {
            variants = project.android.applicationVariants
        } else if (isLibProject) {
            variants = project.android.libraryVariants
        } else {
            throw new IllegalStateException("Must be android project or android-library project.")
        }

        project.dependencies {
            implementation "org.aspectj:aspectjrt:1.9.4"
            implementation 'co.infinum.collar:collar-core:1.0.0'
        }

        project.afterEvaluate {

            variants.all { variant ->
                def buildTypeName = variant.name.capitalize()

                def aopTask = project.task("compile${buildTypeName}AspectJ") {
                    doLast {
                        String[] args = [
                            "-encoding", "UTF-8",
                            "-showWeaveInfo",
                            "-g",
                            "-source", "1.8",
                            "-target", "1.8",
                            "-inpath", [javaCompile.destinationDir.toString(), "collar-core.aar"].join(File.pathSeparator),
                            "-aspectpath", [javaCompile.classpath.asPath, "collar-core.aar"].join(File.pathSeparator),
                            "-d", javaCompile.destinationDir.toString(),
                            "-classpath", javaCompile.classpath.asPath,
                            "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)
                        ]

                        MessageHandler handler = new MessageHandler(true);
                        new Main().run(args, handler);
                        for (IMessage message : handler.getMessages(null, true)) {
                            switch (message.getKind()) {
                                case IMessage.ABORT:
                                case IMessage.ERROR:
                                case IMessage.FAIL:
                                    log.error message.message, message.thrown
                                    break;
                                case IMessage.WARNING:
                                    log.warn message.message, message.thrown
                                    break;
                                case IMessage.INFO:
                                    log.info message.message, message.thrown
                                    break;
                                case IMessage.DEBUG:
                                    log.debug message.message, message.thrown
                                    break;
                            }
                        }
                    }
                }

                variant.javaCompile.finalizedBy(aopTask)
            }
        }
    }
}