package com.github.ivancarras.graphfity.plugin.domain.model

import com.github.ivancarras.graphfity.plugin.presentation.util.notContains
import java.util.*
import kotlin.collections.ArrayList

class GraphVisitor<T>(private val graph: Graph<T>) {
    fun breadthFirstSearch(source: Vertex<T>): ArrayList<Vertex<T>> {
        val queued: Queue<Vertex<T>> = LinkedList()
        val visited = arrayListOf<Vertex<T>>()
        queued.add(source)
        visited.add(source)

        while (queued.isNotEmpty()){
            val vertex = queued.poll()
            val neighborsEdges = graph.edges(vertex)
            neighborsEdges.forEach {
                if(visited.notContains(it.destination)){
                    queued.add(it.destination)
                }
            }
        }

        return visited
    }
}