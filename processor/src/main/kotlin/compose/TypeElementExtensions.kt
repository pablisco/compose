package compose

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter

fun TypeElement.toTypeSpec(
    name: String = qualifiedName.toString(),
    editor: TypeSpec.Builder.() -> Unit = {}
): TypeSpec =
    TypeSpec.classBuilder(name)
        .addFields(ElementFilter.fieldsIn(listOf(this)))
        .also(editor)
        .build()


private fun TypeSpec.Builder.addFields(fields: List<VariableElement>): TypeSpec.Builder = this.also {
    addFields(fields.map(::fieldSpec))
}

private fun fieldSpec(variableElement: VariableElement) =
    FieldSpec.builder(
        TypeName.get(variableElement.asType()),
        variableElement.simpleName.toString()
    ).build()



