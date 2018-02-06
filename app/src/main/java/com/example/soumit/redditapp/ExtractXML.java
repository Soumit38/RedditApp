package com.example.soumit.redditapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Soumit on 2/3/2018.
 */

public class ExtractXML {

    private static final String TAG = "ExtractXML";

    private String tag;
    private String xml;
    private String endTag;

    public ExtractXML(String tag, String xml) {
        this.tag = tag;
        this.xml = xml;
        this.endTag="NONE";
    }

    public ExtractXML(String tag, String xml, String endTag) {
        this.tag = tag;
        this.xml = xml;
        this.endTag = endTag;
    }

    public List<String> start(){
        List<String> result = new ArrayList<>();
        String[] splitXML = null;
        String  marker = null;

        if(endTag.equals("NONE")){
            marker = "\"";
            splitXML = xml.split(tag + marker);
        }else {
            marker = endTag;
            splitXML = xml.split(tag);
        }

        int count = splitXML.length;

        for(int i=1;i<count;i++){
            String temp = splitXML[i];
            int index = temp.indexOf(marker);
            Log.d(TAG, "Started: index of \" : " + index);
            Log.d(TAG, "Started: start: extracted : " + temp);

            temp = temp.substring(0, index);
            Log.d(TAG, "start: snipped : " + temp);
            result.add(temp);
        }

        return result;
    }

}






























