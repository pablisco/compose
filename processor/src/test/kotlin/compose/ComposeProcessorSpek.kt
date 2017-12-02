package compose

import com.google.common.truth.Truth.assertAbout
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaFileObjects.*
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class ComposeProcessorSpek : Spek({

    given("A child class ") {
        val files = assertAbout(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(
                forResource("simple/ChildType.java"),
                forResource("simple/ParentType.java")
            ))

        on("processed") {
            val processed = files.processedWith(ComposeProcessor())

            it("generates composed file") {

                processed.compilesWithoutError()
                    .and().generatesSources(forResource("simple/ComposedChildType.java"))

            }

        }

    }

})