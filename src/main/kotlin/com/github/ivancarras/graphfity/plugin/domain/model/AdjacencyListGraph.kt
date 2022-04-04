package com.github.ivancarras.graphfity.plugin.domain.model

class AdjacencyListGraph<T>: Graph<T> {

    private val adjacencies: HashMap<Vertex<T>, ArrayList<Edge<T>>> = hashMapOf()

    override fun addDirectedEdge(source: Vertex<T>, destination: Vertex<T>) {
        val edge = Edge(source,destination)
        adjacencies
            .computeIfAbsent(source){ arrayListOf()}
            .add(edge)
    }

    override fun edges(source: Vertex<T>): List<Edge<T>> =
        adjacencies[source].orEmpty()
}