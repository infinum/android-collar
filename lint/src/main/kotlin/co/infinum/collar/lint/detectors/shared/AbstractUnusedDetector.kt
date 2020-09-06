package co.infinum.collar.lint.detectors.shared

import co.infinum.collar.lint.detectors.UnusedUserPropertiesDetector
import co.infinum.collar.lint.issues.UnusedUserPropertyIssue
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiJavaCodeReferenceElement
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.toUElement

@Suppress("UnstableApiUsage")
abstract class AbstractUnusedDetector : AbstractDetector() {

    private val testOnlyOpenTypes = mutableMapOf<String, Location>()
    private val cache = mutableListOf<String>()

    companion object {
        const val TEST_ONLY_OPEN = "UserProperties"
        const val FQCN_TEST_ONLY_OPEN = "co.infinum.collar.annotations.$TEST_ONLY_OPEN"

        private val IMPLEMENTATION = Implementation(
            UnusedUserPropertiesDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val TYPE_USAGE = Issue.create(
            "TestOnlyOpenType",
            "This type is not open",
            "Type with @$TEST_ONLY_OPEN annotation is open for tests only. " +
                "Consider making it open explicitly.",
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            IMPLEMENTATION
        )

        val INHERITANCE_USAGE = Issue.create(
            "InheritingTestOnlyOpenType",
            "This type is not open, so it cannot be inherited",
            "Type with @$TEST_ONLY_OPEN annotation is open for tests only, so it cannot be inherited.",
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            IMPLEMENTATION
        )
    }

    override fun quickFix(): LintFix? = null

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) = visitClassImpl(context, node)
        }
    }

    private fun visitClassImpl(context: JavaContext, node: UClass) {
        node.qualifiedName?.let { qualifiedName ->

            if (isCached(qualifiedName) || hasTestOnlyOpenAnnotation(node)) {
                addTestOnlyType(qualifiedName, context.getNameLocation(node))
            }
        }

        parentClassOf(node)?.let { parentClass ->

            if (isCached(parentClass)) {
                reportInheritanceUsage(context, node, parentClass)
            } else {
                if (hasTestOnlyOpenAnnotation(parentClass)) {
                    cache(parentClass)
                    reportInheritanceUsage(context, node, parentClass)
                }
            }
        }
    }

    override fun afterCheckRootProject(context: Context) {
        testOnlyOpenTypes.forEach {

            if (cache.contains(it.key)) {
                reportTypeUsage(context, it.value)
            }
        }
    }

    private fun isCached(element: PsiJavaCodeReferenceElement) =
        element.qualifiedName?.let { isCached(it) } ?: false

    private fun isCached(name: String) =
        cache.contains(name)

    private fun cache(element: PsiJavaCodeReferenceElement) =
        cache.add(element.qualifiedName)

    private fun addTestOnlyType(name: String, location: Location) {
        testOnlyOpenTypes[name] = location
    }

    private fun parentClassOf(node: UClass): PsiJavaCodeReferenceElement? {
        return node.extendsList
            ?.referenceElements
            ?.firstOrNull()
    }

    private fun hasTestOnlyOpenAnnotation(element: PsiJavaCodeReferenceElement): Boolean {
        return element.resolve()
            ?.toUElement(UClass::class.java)
            ?.let {
                hasTestOnlyOpenAnnotation(it)
            }
            ?: false
    }

    private fun hasTestOnlyOpenAnnotation(node: UClass): Boolean {
        return node.annotations
            .any { FQCN_TEST_ONLY_OPEN == it.qualifiedName }
    }

    private fun reportInheritanceUsage(context: JavaContext, clazz: UClass, parent: PsiJavaCodeReferenceElement) {
        val location = context.uastParser.createLocation(parent)
        val message = "This type is open for tests only"
        context.report(INHERITANCE_USAGE, clazz, location, message)
    }

    private fun reportTypeUsage(context: Context, location: Location) {
        val message = "This type is open for tests only, still being inherited from"
        context.report(TYPE_USAGE, location, message)
    }
}