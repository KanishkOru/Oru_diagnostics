package com.oruphones.nativediagnostic.api;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*{
"body": "string",
"ccList": [
"string"
],
"subject": "string",
"toAddress": "string"
}*/
public class ReportAProblem {
    @SerializedName("body")
    private String body;
    @SerializedName("ccList")
    private List<String> ccList ;
    @SerializedName("subject")
    private String subject ;
    @SerializedName("toAddress")
    private String toAddress ;

    public ReportAProblem(@NonNull String body, @NonNull String subject, @NonNull String toAddress) {
        this.body = body;
        this.subject = subject;
        this.toAddress = toAddress;
    }

}
