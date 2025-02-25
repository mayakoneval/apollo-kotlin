package com.apollographql.ijplugin.util

import com.apollographql.ijplugin.project.ApolloProjectService.ApolloVersion
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile

fun Project.getApolloVersion(): ApolloVersion {
  var foundVersion = ApolloVersion.NONE
  service<ProjectRootManager>().orderEntries().librariesOnly().forEachLibrary { library ->
    val components = library.name?.substringAfter(" ")?.split(":") ?: return@forEachLibrary true
    if (components.size < 3) return@forEachLibrary true
    val (group, _, version) = components
    when (group) {
      "com.apollographql.apollo" -> {
        foundVersion = ApolloVersion.V2
        false
      }

      "com.apollographql.apollo3" -> {
        when {
          version.startsWith("3.") -> {
            foundVersion = ApolloVersion.V3
            false
          }

          version.startsWith("4.") -> {
            foundVersion = ApolloVersion.V4
            false
          }

          else -> true
        }
      }

      else -> true
    }
  }
  return foundVersion
}

fun Module.apolloGeneratedSourcesRoots(): List<VirtualFile> {
  return this.rootManager.contentRoots.filter { it.isApolloGenerated() }
}

fun VirtualFile.isApolloGenerated(): Boolean {
  return path.contains("generated/source/apollo")
}
