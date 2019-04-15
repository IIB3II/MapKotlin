package com.example.map.utils

interface MapAction<T> {
    fun call(t: T)
}