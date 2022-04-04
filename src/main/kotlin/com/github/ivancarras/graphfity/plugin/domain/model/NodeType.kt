package com.github.ivancarras.graphfity.plugin.domain.model

data class NodeType(
    val name: String,
    val regex: String,
    val isEnabled: Boolean,
    val shape: String,
    val fillColor: String
)