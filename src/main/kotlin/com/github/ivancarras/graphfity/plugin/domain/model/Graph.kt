package com.github.ivancarras.graphfity.plugin.domain.model

interface Graph<T> {
    fun addDirectedEdge(source: Vertex<T>, destination: Vertex<T>)
    fun edges(source: Vertex<T>): List<Edge<T>>
}