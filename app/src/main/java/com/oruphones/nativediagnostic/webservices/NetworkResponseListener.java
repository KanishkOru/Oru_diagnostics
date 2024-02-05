package com.oruphones.nativediagnostic.webservices;

public interface NetworkResponseListener<Response> {

    void onResponseReceived(Response response);

    void onError();
}

