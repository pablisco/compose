package compose

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter

fun TypeElement.toTypeSpec(
    name: String = qualifiedName.toString(),
    editor: TypeSpec.Builder.() -> Unit = {}
): TypeSpec =
    TypeSpec.classBuilder(name)
        .addFields(ElementFilter.fieldsIn(enclosedElements))
        .also(editor)
        .build()


private fun TypeSpec.Builder.addFields(fields: List<VariableElement>) = this.also {
    addFields(fields.map { it.toFieldSpec() })
}

private fun VariableElement.toFieldSpec() =
    FieldSpec.builder(
        TypeName.get(asType()), simpleName.toString()
    ).addModifiers(modifiers)
        .build()!!

private fun FieldSpec.Builder.addModifiers(modifiers: Set<Modifier>) =
    addModifiers(*modifiers.toTypedArray())





