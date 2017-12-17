package compose

import com.google.common.truth.Truth.assertAbout
import com.google.testing.compile.JavaFileObjects.forResource
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.*

class ComposeProcessorSpek : Spek({

    given("a processor") {
        val processor by memoized { ComposeProcessor() }

        on("process simple child type") {
            val files = assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(listOf(
                    forResource("simple/input/ChildType.java"),
                    forResource("simple/input/ParentType.java")
                ))
            it("generates composed file") {
                files.processedWith(processor).compilesWithoutError()
                    .and().generatesSources(forResource("simple/expected/ComposedChildType.java"))
            }
        }

        on("process child with fields") {
            val files = assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(listOf(
                    forResource("withDelegateMethod/input/ChildType.java"),
                    forResource("withDelegateMethod/input/ParentType.java")
                ))
            it("generates composed file") {
                files.processedWith(processor).compilesWithoutError()
                    .and().generatesSources(forResource("withDelegateMethod/expected/ComposedChildType.java"))
            }
        }

    }
})

