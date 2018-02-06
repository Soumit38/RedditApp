package com.example.soumit.redditapp.comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Soumit on 2/6/2018.
 */

public class CheckComment {

    @SerializedName("success")
    @Expose
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "CheckComment{" +
                "success='" + success + '\'' +
                '}';
    }
}
