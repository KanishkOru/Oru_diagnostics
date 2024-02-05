package com.oruphones.nativediagnostic.models;


import java.io.Serializable;

public enum AbortReasons implements Serializable {
    END_SESSION,
    APP_CRASHED,
    TIMED_OUT,
}
