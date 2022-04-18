package com.github.ivancarras.graphfity.plugin.presentation.util

fun<T> List<T>.notContains(element:T): Boolean=
    contains(element).not()