package compose

import com.squareup.javapoet.JavaFile
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

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
        when (element) {
            is TypeElement -> process(element)
            else -> printError(element, "Expected TypeElement")
        }
    }

    private fun process(element: TypeElement) {
        try {
            val composedMirror = ComposeAnnotationMirror(element.annotationFor(Compose::class.java)!!)
            val packageName = elementUtils.getPackageOf(element).qualifiedName.toString()
            val typeSpec = element.toTypeSpec(composedMirror.composeName(element.simpleName)) {
                superclass(composedMirror.parentName)
            }
            val javaFile = JavaFile.builder(packageName, typeSpec).build()
            javaFile.writeTo(filer)
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
