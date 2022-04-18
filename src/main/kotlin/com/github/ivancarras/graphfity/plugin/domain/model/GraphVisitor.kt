package com.github.ivancarras.graphfity.plugin.domain.model

import com.github.ivancarras.graphfity.plugin.presentation.util.notContains
import java.util.*
import kotlin.collections.ArrayList

class GraphVisitor<T>(private val graph: Graph<T>) {
    fun breadthFirstSearch(source: Vertex<T>): ArrayList<Vertex<T>> {
        val queued: Queue<Vertex<T>> = LinkedList()
        val visited = arrayListOf<Vertex<T>>()
        queued.add(source)

        while (queued.isNotEmpty()){
            val vertex = queued.poll()
            visited.add(vertex)
            val neighborsEdges = graph.edges(vertex)
            neighborsEdges.forEach { neighborEdge->
                if(visited.notContains(neighborEdge.destination) &&
                    queued.toList().notContains(neighborEdge.destination)){
                    queued.add(neighborEdge.destination)
                }
            }
        }

        return visited
    }
}