package com.oruphones.nativediagnostic.models;


import java.io.Serializable;

public enum SummaryDisplayElement implements Serializable {
    TradeInEligibility,
    SuggestedFixes,
    DeviceInfo,
    PhysicalDamage,
    TestResults,

    BatteryTest,
    FivePointCheck,
    Notes,
    NetworkStatus

}
