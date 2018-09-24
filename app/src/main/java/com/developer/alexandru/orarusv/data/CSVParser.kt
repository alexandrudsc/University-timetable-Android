package com.developer.alexandru.orarusv.data

import java.io.BufferedReader
import java.io.IOException

/**
 * Created by alexandru on 8/14/16.
 */
abstract class CSVParser(private val br: BufferedReader) {
    private var wasSuccessful: Boolean = false

    fun parse() {
        var line: String?
        try {
            do {
            //while (() != null) {
                line = br.readLine()
                if (line == null)
                    break
                val len = line.length
                val array = line.toCharArray()

                // The char within the string is part of data, not part of a HTML tag
                var isData = false
                var start = 0
                var stop: Int
                for (j in 1..len - 1 - 1) {

                    val buffer: String
                    try {
                        // HTML tag end, data begin
                        if (array[j] == '>' && array[j + 1] != '<') {
                            isData = true
                            start = j + 1
                            continue
                        }
                        // data end, HTML tag begin
                        if (array[j] == '<') {
                            if (isData) {
                                stop = j - 1

                                buffer = String(array, start, stop - start)

                                val data = buffer.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                                handleData(data)
                            }

                            isData = false
                        }
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        e.printStackTrace()
                        wasSuccessful = false
                    }

                    wasSuccessful = true
                }
            }
            while (true);
        } catch (e: IOException) {
            e.printStackTrace()
            wasSuccessful = false
        }

    }

    fun wasSuccessful(): Boolean {
        return wasSuccessful
    }

    abstract fun handleData(data: Array<String>): Boolean
}
