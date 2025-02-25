package com.apollographql.ijplugin.refactoring.migration.v3tov4

import com.apollographql.ijplugin.ApolloBundle
import com.apollographql.ijplugin.refactoring.migration.ApolloMigrationRefactoringProcessor
import com.apollographql.ijplugin.refactoring.migration.apollo3
import com.apollographql.ijplugin.refactoring.migration.item.ConstructorInsteadOfBuilder
import com.apollographql.ijplugin.refactoring.migration.item.RemoveMethodCall
import com.apollographql.ijplugin.refactoring.migration.item.UpdateClassName
import com.apollographql.ijplugin.refactoring.migration.item.UpdateCustomTypeMappingInBuildKts
import com.apollographql.ijplugin.refactoring.migration.item.UpdateFieldName
import com.apollographql.ijplugin.refactoring.migration.item.UpdateGradleDependenciesBuildKts
import com.apollographql.ijplugin.refactoring.migration.item.UpdateGradleDependenciesInToml
import com.apollographql.ijplugin.refactoring.migration.item.UpdateGradlePluginInBuildKts
import com.apollographql.ijplugin.refactoring.migration.item.UpdateMethodCall
import com.apollographql.ijplugin.refactoring.migration.item.UpdateMethodName
import com.apollographql.ijplugin.refactoring.migration.v3tov4.item.EncloseInService
import com.apollographql.ijplugin.refactoring.migration.v3tov4.item.RemoveFieldInService
import com.apollographql.ijplugin.refactoring.migration.v3tov4.item.RemoveWatchMethodArguments
import com.apollographql.ijplugin.refactoring.migration.v3tov4.item.UpdateEnumClassUpperCase
import com.apollographql.ijplugin.refactoring.migration.v3tov4.item.UpdateFieldNameInService
import com.apollographql.ijplugin.refactoring.migration.v3tov4.item.UpdateMultiModuleConfiguration
import com.apollographql.ijplugin.refactoring.migration.v3tov4.item.UpdateWebSocketReconnectWhen
import com.intellij.openapi.project.Project

private const val apollo4LatestVersion = "4.0.0-alpha.2"

/**
 * Migrations of Apollo Kotlin v3 to v4.
 */
class ApolloV3ToV4MigrationProcessor(project: Project) : ApolloMigrationRefactoringProcessor(project) {
  override val refactoringName = ApolloBundle.message("ApolloV3ToV4MigrationProcessor.title")

  override val noUsageMessage = ApolloBundle.message("ApolloV3ToV4MigrationProcessor.noUsage")

  override val migrationItems = listOf(
      // Deprecations / renames
      UpdateFieldName("$apollo3.api.ApolloResponse", "dataAssertNoErrors", "dataOrThrow()"),
      UpdateFieldName("$apollo3.exception.ApolloCompositeException", "first", "suppressedExceptions.first()"),
      UpdateFieldName("$apollo3.exception.ApolloCompositeException", "second", "suppressedExceptions.getOrNull(1)"),
      UpdateClassName("$apollo3.exception.ApolloCompositeException", "$apollo3.exception.ApolloException"),
      UpdateClassName("$apollo3.exception.ApolloCanceledException", "$apollo3.exception.ApolloException"),
      UpdateClassName("$apollo3.exception.ApolloGenericException", "$apollo3.exception.ApolloException"),
      UpdateMethodName("$apollo3.ast.GQLResult", "valueAssertNoErrors", "getOrThrow"),
      UpdateMethodName("$apollo3.cache.normalized.api.CacheHeaders", "toBuilder", "newBuilder"),
      UpdateMethodName("$apollo3.ApolloClient", "dispose", "close"),
      UpdateMethodName("$apollo3.ApolloClient", "mutate", "mutation"),
      UpdateMethodName("$apollo3.ApolloClient", "subscribe", "subscription"),
      UpdateMethodName("$apollo3.ApolloClient.Companion", "builder", "Builder"),
      UpdateMethodName("$apollo3.ApolloClient.Builder", "requestedDispatcher", "dispatcher"),
      UpdateMethodName("$apollo3.cache.http.DiskLruHttpCache", "delete", "clearAll"),
      RemoveMethodCall("$apollo3.cache.normalized.NormalizedCache", "emitCacheMisses", extensionTargetClassName = "$apollo3.api.MutableExecutionOptions"),
      UpdateMethodCall(
          "$apollo3.cache.normalized.NormalizedCache",
          "executeCacheAndNetwork",
          "fetchPolicy(FetchPolicy.CacheAndNetwork).toFlow()",
          extensionTargetClassName = "$apollo3.ApolloCall",
          "$apollo3.cache.normalized.fetchPolicy",
          "$apollo3.cache.normalized.FetchPolicy",
      ),
      UpdateMethodCall(
          "$apollo3.cache.normalized.NormalizedCache",
          "clearNormalizedCache",
          replacementExpression = "apolloStore.clearAll()",
          extensionTargetClassName = "$apollo3.ApolloClient",
          "$apollo3.cache.normalized.apolloStore",
      ),
      UpdateMethodCall(
          "$apollo3.cache.normalized.NormalizedCache",
          "apolloStore",
          replacementExpression = "apolloStore",
          extensionTargetClassName = "$apollo3.ApolloClient",
          "$apollo3.cache.normalized.apolloStore",
      ),
      RemoveWatchMethodArguments,
      ConstructorInsteadOfBuilder("$apollo3.cache.normalized.api.CacheKey.Companion", "from"),
      UpdateWebSocketReconnectWhen,

      UpdateEnumClassUpperCase,

      // Gradle
      UpdateGradlePluginInBuildKts(apollo3, apollo3, apollo4LatestVersion),
      UpdateGradleDependenciesInToml(apollo3, apollo3, apollo4LatestVersion),
      UpdateGradleDependenciesBuildKts(apollo3, apollo3),

      UpdateFieldNameInService("generateModelBuilder", "generateModelBuilders"),
      UpdateFieldNameInService("generateTestBuilders", "generateDataBuilders"),
      RemoveFieldInService("languageVersion"),
      UpdateCustomTypeMappingInBuildKts,
      UpdateMultiModuleConfiguration,
      EncloseInService,
  )
}
