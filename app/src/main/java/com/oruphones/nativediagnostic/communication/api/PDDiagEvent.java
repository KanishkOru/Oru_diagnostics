package com.oruphones.nativediagnostic.communication.api;

public class PDDiagEvent {
    private String eventname;
    private Object eventdata;
    public String getEventname() {
        return eventname;
    }
    public void setEventname(String eventname) {
        this.eventname = eventname;
    }
    public Object getEventdata() {
        return eventdata;
    }
    public void setEventdata(Object eventdata) {
        this.eventdata = eventdata;
    }
}
