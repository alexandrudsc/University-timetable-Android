package com.developer.alexandru.aplicatie_studenti.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Alexandru on 6/30/14.
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

    public String prof;
    public String fullProf;

    public String info;
    public String parity;
    public String parallelFaculties;

    private final int NUM_ELEM = 7;

    public Course(){
        // default constructor
    }

    public Course(String name, String fullName, String type, String location,
                  String time, String prof, String info) {
        this.name = name;
        this.fullName = fullName;
        this.type = type;
        this.info = info;
        this.time = time;
        this.location = location;
        this.prof = prof;
    }

    public Course(String name, String fullName, String type,
                  String location, String fullLocation,
                  String time, String prof, String parity, String info) {
        this.name = name;
        this.fullName = fullName;
        this.type = type;
        this.info = info;
        this.parity = parity;
        this.time = time;
        this.location = location;
        this.fullLocation = fullLocation;
        this.prof = prof;
    }

    public Course(Parcel in){
        String[] data;
       // ArrayList<String> parcelData = in.readArrayList(null);
        //in.readStringArray(data);
        data = (String[]) in.readSerializable();
        //in.readStringArray(data);
        try {
            this.name = data[0];
            this.fullName = data[1];
            this.type = data[2];
            this.info= data[3];
            this.time= data[4];
            this.location = data[5];
            this.prof = data[6];
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        /*this.name = parcelData.get(0);
        this.fullName = parcelData.get(1);
        this.type = parcelData.get(2);
        this.info= parcelData.get(3);
        this.time= parcelData.get(4);
        this.location = parcelData.get(5);
        this.prof = parcelData.get(6);*/
        /*this.name = c.name;
        this.fullName = c.fullName;
        this.type = c.type;
        this.info = c.info;
        this.time = c.time;
        this.location = c.location;
        this.prof = c.prof;*/
        if (D) Log.d(TAG, "PARSE");
    }


    @Override
    public String toString() {
        return fullName + "\n" + name + "\n" + type + "\n" + time + "\n" + fullLocation + "\n" + location
                + "\n" + fullProf + "\n" + prof + "\n" + parallelFaculties + "\n" + info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        String[] data={name, fullName, type, info, time, location, prof};
        //out.writeParcelable(c, flags);
        //out.writeStringArray(data);
        out.writeSerializable(data);
        /*ArrayList<String> data = new ArrayList<String>();
        data.add(name);
        data.add(fullName);
        data.add(type);
        data.add(info);
        data.add(time);
        data.add(location);
        data.add(prof);
        out.writeString(name);
        out.writeString(fullName);
        out.writeString(type);
        out.writeString(info);
        out.writeString(time);
        out.writeString(location);
        out.writeString(prof);*/
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
