package compose

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic
import kotlin.reflect.KProperty1

class ComposeProcessor : AbstractProcessor() {

    private lateinit var environment: ProcessingEnvironment
    private val messager by lazy { environment.messager }
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
        roundEnv.elementsToCompose.forEach {
            if (it.isClass()) {
                processElement(it)
            } else {
                printError(it, NOT_A_CLASS)
            }
        }
    }

    private fun processElement(element: Element) {
        val typeElement = element as TypeElement

        val superClassName = elementUtils.getTypeElement(element.qualifiedName)

        val pkg = elementUtils.getPackageOf(superClassName).takeUnless { it.isUnnamed }
        val packageName = pkg?.qualifiedName?.toString()

        val composeMirror = typeElement.annotationMirror(Compose::class.java)
        val superClassType: TypeMirror = composeMirror[Compose::extend] ?: throw RuntimeException("No extends")
        val superclass: TypeName = TypeName.get(superClassType)

        val factoryClassName = composeMirror.let {
            val prefix: String = it[Compose::prefix] ?: ""
            val postfix: String = it[Compose::postfix] ?: ""
            "$prefix${superClassName.simpleName}$postfix"
        }.takeUnless { it == superClassName.simpleName.toString() } ?: "Composed${superClassName.simpleName}"

        val typeSpec = TypeSpec.classBuilder(factoryClassName).superclass(superclass)/*.addMethod(method.build())*/.build()

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

    private fun Element.isClass() =
        kind == ElementKind.CLASS

    private fun printError(element: Element, message: String?) =
        messager.printMessage(Diagnostic.Kind.ERROR, message ?: "Unknonwn error", element)

    private fun print(message: String) =
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message)

    private fun TypeElement.annotationMirror(clazz: Class<*>, clazzName: String = clazz.name): AnnotationMirror =
        annotationMirrors.first { it.annotationType.toString() == clazzName }

    @Suppress("UNCHECKED_CAST")
    private operator fun <T> AnnotationMirror.get(key: String): T? =
        elementValues.filter { (k, _) -> k.simpleName.toString() == key }
            .map { (_, v) -> v }
            .firstOrNull()?.value as T?

    private operator fun <T> AnnotationMirror.get(p: KProperty1<Compose, Any>): T? =
        this[p.name]

}
