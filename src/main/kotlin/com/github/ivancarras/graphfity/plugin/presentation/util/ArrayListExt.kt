package com.github.ivancarras.graphfity.plugin.presentation.util

fun<T> ArrayList<T>.notContains(element:T): Boolean=
    contains(element).not()