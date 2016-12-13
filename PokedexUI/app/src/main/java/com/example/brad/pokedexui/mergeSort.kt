package com.example.brad.pokedexui

/**
 * Created by Brad on 2016-12-07.
 */
fun <T:Comparable<T>>mergesort(items:MutableList<T>):MutableList<T>{
    if (items.isEmpty()){
        return items
    }

    fun merge(left:MutableList<T>, right:MutableList<T>):MutableList<T>{
        var merged: MutableList<T> = arrayListOf<T>()
        while(!left.isEmpty() && !right.isEmpty()){
            val temp:T
            if (left.first() < right.first()) {
                temp = left.removeAt(0)
            } else {
                temp = right.removeAt(0)
            }
            merged.add(temp)
        }
        if (!left.isEmpty()) merged.addAll(left)
        if (!right.isEmpty()) merged.addAll(right)

        return merged
    }

    val pivot = items.count()/2

    var left  = mergesort(items.subList(0, pivot))
    var right = mergesort(items.subList(pivot, items.count()-1))

    return merge(left, right)
}
