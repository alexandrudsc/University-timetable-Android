package com.developer.alexandru.orarusv.data

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import java.io.Serializable

/**
 * Created by Alexandru on 6/30/14.
 * Class representing a course. Can be serialized.
 */
class Course : Parcelable, Serializable {


    var name: String = ""
    var fullName: String = ""

    var type: String = ""

    var location: String = ""
    var fullLocation: String = ""

    var time: String = ""
    // Start time and end time are used only in the database
    var startTime: Int = 0
    var endTime: Int = 0

    var day: Int = 0

    var prof: String = ""
    var profID: String = ""

    var info: String = ""
    var parity: String = ""
    var parallelFaculties: String? = null

    constructor() {
        // default constructor
    }

    constructor(name: String, fullName: String, type: String,
                location: String, fullLocation: String,
                time: String, prof: String, profID: String, parity: String, info: String) {
        this.name = name
        this.fullName = fullName
        this.type = type
        this.location = location
        this.fullLocation = fullLocation
        this.time = time
        this.prof = prof
        this.profID = profID
        this.parity = parity
        this.info = info
    }

    constructor(`in`: Parcel) {
        val data: Array<String>
        data = `in`.readSerializable() as Array<String>
        //in.readStringArray(data);
        try {
            this.name = data[0]
            this.fullName = data[1]
            this.type = data[2]
            this.location = data[3]
            this.fullLocation = data[4]
            this.time = data[5]
            this.prof = data[6]
            this.profID = data[7]
            this.parity = data[8]
            this.info = data[9]
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }

        if (D) Log.d(TAG, "PARSE")
    }


    override fun toString(): String {
        return "$fullName\n$name\n$type\nstart: $startTime\nstop: $endTime\n$fullLocation\n$location\n$prof\n$parallelFaculties\n$info"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        //String[] data={name, fullName, type, info, time, location, prof};
        val data = arrayOf(name, fullName, type, location, fullLocation, time, prof, profID, parity, info)
        out.writeSerializable(data)
    }

    companion object {
        // Debug
        private val D = true
        private val TAG = "COURSE"

        val CREATOR: Parcelable.Creator<Course> = object : Parcelable.Creator<Course> {

            override fun createFromParcel(`in`: Parcel): Course {
                return Course(`in`)
            }

            override fun newArray(size: Int): Array<Course?> {
                return arrayOfNulls(size)
            }
        }
    }

}
