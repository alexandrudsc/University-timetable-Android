package com.developer.alexandru.orarusv.data

/**
 * Created by alexandru on 2/25/2017.
 * Class holding data about a timetable: usually id and name;
 */
class Timetable private constructor() {
    var id: Int = 0
    var name: String = ""
    var type: Type = Type.Student

    override fun toString(): String {
        return name
    }

    object Creator {
        @Throws(NumberFormatException::class, IndexOutOfBoundsException::class)
        fun create(data: Array<String>): Timetable {
            val timetable = Timetable()
            timetable.type = Type.values()[Integer.valueOf(data[0])]
            timetable.id = Integer.valueOf(data[1])
            timetable.name = data[2]
            return timetable
        }
    }

    enum class Type {
        Student,
        Professor
    }
}
