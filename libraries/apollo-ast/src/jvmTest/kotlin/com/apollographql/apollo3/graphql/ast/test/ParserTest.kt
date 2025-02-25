package com.apollographql.apollo3.graphql.ast.test

import com.apollographql.apollo3.ast.Issue
import com.apollographql.apollo3.ast.parseAsGQLDocument
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertEquals

@RunWith(TestParameterInjector::class)
class ParserTest {
  @Test
  fun parserProducesExpectedValues(@TestParameter(valuesProvider = ParametersProvider::class) graphqlFile: File) {
    checkExpected(graphqlFile) {
      graphqlFile.parseAsGQLDocument()
          .issues
          .serialize()
    }
  }

  class ParametersProvider : TestParameter.TestParameterValuesProvider {
    override fun provideValues(): List<File> {
      return File("test-fixtures/parser/")
          .listFiles()!!
          .filter { it.extension == "graphql" }
          .sortedBy { it.name }
          .filter {
            testFilterMatches(it.name)
          }
    }
  }

  /**
   * run the block and checks the result against the .expected file. Will overwrite the result if required
   *
   * @param block the callback to produce the result.
   */
  fun checkExpected(graphQLFile: File, block: () -> String) {
    val actual = block()

    val expectedFile = File(graphQLFile.parent, graphQLFile.nameWithoutExtension + ".expected")
    val expected = try {
      expectedFile.readText()
    } catch (e: Exception) {
      null
    }

    if (shouldUpdateTestFixtures()) {
      expectedFile.writeText(actual)
    } else {
      assertEquals(expected, actual)
    }
  }

  companion object {
    private const val separator = "\n------------\n"

    fun List<Issue>.serialize() = joinToString(separator) {
      "${it.severity}: ${it.javaClass.simpleName} (${it.sourceLocation?.line}:${it.sourceLocation?.column})\n${it.message}"
    }

    fun shouldUpdateTestFixtures(): Boolean {
      if (System.getenv("updateTestFixtures") != null) {
        return true
      }

      return when (System.getProperty("updateTestFixtures")?.trim()) {
        "on", "true", "1" -> true
        else -> false
      }
    }

    private fun testFilterMatches(value: String): Boolean {
      val testFilter = System.getenv("testFilter") ?: System.getProperty("testFilter") ?: return true

      return Regex(testFilter).containsMatchIn(value)
    }
  }
}