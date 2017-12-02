package compose

import com.google.auto.common.MoreElements
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KProperty1

class ComposeProcessor : AbstractProcessor(), ProcessEnvironmentAware {

    override lateinit var environment: ProcessingEnvironment
    private val elementUtils by lazy { environment.elementUtils }
    private val filer by lazy { environment.filer }

    companion object {
        val NOT_A_CLASS = "Only classes can be annotated with ${Compose::class.java.simpleName}"
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        environment = processingEnv
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean = true.also {
        roundEnv.elementsToCompose.forEach { element ->
            when {
                !element.isClass() -> printError(element, NOT_A_CLASS)
                else -> process(element)
            }
        }
    }

    private fun process(element: Element) {
        when(element) {
            is TypeElement -> process(element)
            else -> printError(element, "Expected TypeElement")
        }
    }

    private fun process(element: TypeElement) {
        val superClassName = elementUtils.getTypeElement(element.qualifiedName)

        val packageElement = elementUtils.getPackageOf(superClassName).takeUnless { it.isUnnamed }
        val packageName = packageElement?.qualifiedName?.toString()

        val composeMirror: AnnotationMirror = element.annotationFor(Compose::class.java)!!

        val superClassType: TypeMirror = composeMirror.valueFor(Compose::value) ?: throw RuntimeException("No extend")
        val superclass: TypeName = TypeName.get(superClassType)

        val composedClassName = composeMirror.let {
            val prefix: String = it.valueFor(Compose::prefix) ?: ""
            val postfix: String = it.valueFor(Compose::postfix) ?: ""
            "$prefix${superClassName.simpleName}$postfix"
        }.takeUnless { it == superClassName.simpleName.toString() } ?: "Composed${superClassName.simpleName}"

        val typeSpec = element.toTypeSpec(composedClassName) {
            superclass(superclass)
        }

        try {
            JavaFile.builder(packageName, typeSpec).build().writeTo(filer)
        } catch (e: IOException) {
            printError(element, e.message)
        }
    }

    override fun getSupportedSourceVersion(): SourceVersion =
        SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> =
        listOf(Compose::class.java).map { it.canonicalName }.toSet()

    private inline val RoundEnvironment.elementsToCompose
        get() = getElementsAnnotatedWith(Compose::class.java)

}
