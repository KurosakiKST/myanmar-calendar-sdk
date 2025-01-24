package com.ryan.myanmarcalendar.core.kernel

internal object BinarySearchUtil {
    fun search(key: Double, array: Array<IntArray>): Int {
        var low = 0
        var high = array.size - 1

        while (high >= low) {
            val mid = ((low + high) / 2.0).toInt()
            when {
                array[mid][0] > key -> high = mid - 1
                array[mid][0] < key -> low = mid + 1
                else -> return mid
            }
        }
        return -1
    }

    fun search(key: Double, array: IntArray): Int {
        var low = 0
        var high = array.size - 1

        while (high >= low) {
            val mid = ((low + high) / 2.0).toInt()
            when {
                array[mid] > key -> high = mid - 1
                array[mid] < key -> low = mid + 1
                else -> return mid
            }
        }
        return -1
    }
}