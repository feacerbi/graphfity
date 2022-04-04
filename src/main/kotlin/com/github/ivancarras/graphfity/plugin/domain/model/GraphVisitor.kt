package com.github.ivancarras.graphfity.plugin.domain.model

import com.github.ivancarras.graphfity.plugin.presentation.util.notContains
import sun.misc.Queue
import kotlin.collections.ArrayList

class GraphVisitor<T>(private val graph: Graph<T>) {
    fun breadthFirstSearch(source: Vertex<T>): ArrayList<Vertex<T>> {
        val queued = Queue<Vertex<T>>()
        val visited = arrayListOf<Vertex<T>>()

        queued.enqueue(source)
        visited.add(source)

        while (queued.isEmpty.not()){
            val vertex = queued.dequeue()
            val neighborsEdges = graph.edges(vertex)
            neighborsEdges.forEach {
                if(visited.notContains(it.destination)){
                    queued.enqueue(it.destination)
                }
            }
        }

        return visited
    }
}