package com.developer.alexandru.orarusv.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Alexandru on 6/30/14.
 * Class representing a course. Can be serialized.
 */
public class Course implements Parcelable, Serializable {
    // Debug
    private static final boolean D = true;
    private static final String TAG = "COURSE";


    public String name;
    public String fullName;

    public String type;

    public String location;
    public String fullLocation;

    public String time;
    // Start time and end time are used only in the database
    public int startTime;
    public int endTime;

    public String prof;
    public String fullProf;
    public String profID;

    public String info;
    public String parity;
    public String parallelFaculties;

    public Course(){
        // default constructor
    }

    public Course(String name, String fullName, String type,
                  String location, String fullLocation,
                  String time, String prof, String profID, String parity, String info) {
        this.name = name;
        this.fullName = fullName;
        this.type = type;
        this.location = location;
        this.fullLocation = fullLocation;
        this.time = time;
        this.prof = prof;
        this.profID = profID;
        this.parity = parity;
        this.info = info;
    }

    public Course(Parcel in){
        String[] data;
        data = (String[]) in.readSerializable();
        //in.readStringArray(data);
        try {
            this.name = data[0];
            this.fullName = data[1];
            this.type = data[2];
            this.location = data[3];
            this.fullLocation = data[4];
            this.time= data[5];
            this.prof = data[6];
            this.profID = data[7];
            this.parity = data[8];
            this.info= data[9];
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        if (D) Log.d(TAG, "PARSE");
    }


    @Override
    public String toString() {
        return fullName + "\n" + name + "\n" + type + "\nstart: " + startTime + "\nstop: " + endTime + "\n" + fullLocation + "\n" + location
                + "\n" + fullProf + "\n" + prof + "\n" + parallelFaculties + "\n" + info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        //String[] data={name, fullName, type, info, time, location, prof};
        String[] data = {name, fullName, type, location, fullLocation, time, prof, profID, parity, info};
        out.writeSerializable(data);
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {

        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

}
