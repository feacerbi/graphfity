package com.github.ivancarras.graphfity.plugin.presentation.plugin

import com.github.ivancarras.graphfity.plugin.domain.model.*
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GraphfityTask : DefaultTask() {
    @Input
    val nodeTypesPath: Property<String> = project.objects.property(String::class.java)

    @Input
    val graphImagePath: Property<String> = project.objects.property(String::class.java)

    @Input
    val projectRootName: Property<String> = project.objects.property(String::class.java)


    @TaskAction
    fun graphfity() {

        val nodeTypesPath = nodeTypesPath.get()
        val nodeTypes = loadNodeTypes(nodeTypesPath)
        val projectRootName = projectRootName.get()

        val rootProject = getRootProject(projectRootName)
        val nodes = HashSet<NodeData>()
        val dependencies = HashSet<Pair<NodeData, NodeData>>()
        val graph = AdjacencyListGraph<String>()

        obtainDependenciesData(rootProject, nodes, dependencies, nodeTypes,graph)

      //  addDependenciesToFile(dotFile, dependencies)
       // generateGraph(dotFile)

        //new
        testingGraphVisitor(graph)
    }

    private fun getRootProject(projectRootName: String): Project {
        return project.findProject(projectRootName)
            ?: throw kotlin.IllegalArgumentException("The property provided as projectRootPath: $projectRootName does not correspond to any project")
    }

    private fun loadNodeTypes(nodeTypesPath: String): List<NodeType> {
        val jsonFile = File(nodeTypesPath)
        val jsonObjects = JsonSlurper().parseText(jsonFile.readText())
        return if (jsonObjects is List<*>) {
            jsonObjects.fold(emptyList()) { acc, item ->
                if (item is Map<*, *>) {
                    acc + NodeType(
                        name = item["name"] as String,
                        regex = item["regex"] as String,
                        isEnabled = item["isEnabled"] as Boolean,
                        shape = item["shape"] as String,
                        fillColor = item["fillColor"] as String
                    )
                } else {
                    acc
                }
            }
        } else {
            emptyList()
        }
    }

    private fun generateGraph(dotFile: File) {
        val dotCommand = listOf("dot", "-Tpng", "-O", DOT_FILE)
        ProcessBuilder(dotCommand)
            .directory(dotFile.parentFile)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .run {
                waitFor()
                dotFile.delete()
                if (exitValue() != 0) {
                    throw RuntimeException(errorStream.toString())
                } else {
                    println("Project module dependency graph created at ${dotFile.path}.png")
                }
            }
    }

    private fun testingGraphVisitor(graph: Graph<String>){
        val graphVisitor = GraphVisitor(graph)
        graphVisitor.breadthFirstSearch(Vertex(":app")).forEach {
            print(it.data +" - ")
        }
    }

    private fun obtainDependenciesData(
        project: Project,
        projects: HashSet<NodeData>,
        dependencies: HashSet<Pair<NodeData, NodeData>>,
        nodeTypes: List<NodeType>,
        graph: Graph<String>
    ) {
        val projectNodeData = mapProjectToNode(project, nodeTypes)

        if (projectNodeData != null && projectNodeData.nodeType.isEnabled) {
            projects.add(projectNodeData)
        }

        project.configurations.forEach { config ->
            config.dependencies
                .withType(ProjectDependency::class.java)
                .map { it.dependencyProject }
                .filterNot { project == it.project }
                .forEach { dependencyProject ->
                    val dependencyProjectNodeData = mapProjectToNode(dependencyProject, nodeTypes)
                    if (dependencyProjectNodeData != null && projectNodeData != null &&
                        dependencyProjectNodeData.nodeType.isEnabled
                    ) {
                        projects.add(dependencyProjectNodeData)
                        dependencies.add(Pair(projectNodeData, dependencyProjectNodeData))
                        graph.addDirectedEdge(Vertex(projectNodeData.path), Vertex(dependencyProjectNodeData.path))
                        obtainDependenciesData(
                            dependencyProject,
                            projects,
                            dependencies,
                            nodeTypes,
                            graph
                        )
                    }
                }
        }
    }

    private fun mapProjectToNode(project: Project, nodeTypes: List<NodeType>): NodeData? =
        nodeTypes.firstOrNull { nodeType ->
            nodeType.regex.toRegex().matches(project.path)
        }?.let { nodeType ->
            NodeData(
                path = project.path, nodeType = nodeType
            )
        }

    companion object {
        private const val DOT_FILE = "project.dot"
    }
}