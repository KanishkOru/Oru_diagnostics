package com.oruphones.nativediagnostic.util;



import com.oruphones.nativediagnostic.BuildConfig;
import com.oruphones.nativediagnostic.api.GlobalConfig;

import java.util.Arrays;

public class ProductFlowUtil {

   private static String companyName ;

    // TODO: 9/3/21 this has to set for all products.  
    public static void setCompanyName(String companyName) {
        ProductFlowUtil.companyName = companyName;
    }

    /*TRADE-IN*/
    public static boolean isTradein(){
        return "tradein".equalsIgnoreCase(BuildConfig.FLAVOR_flav) || GlobalConfig.getInstance().isTradeIn();
    }

    public static boolean isBell(){
        return "bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }
    public static boolean isAdvancedTestFlow(){
        return isBell() /*|| isTradein()*/;
    }


    /*D2d*/
    public static boolean isO2UK(){
        return "o2uk".equalsIgnoreCase(companyName);
    }


    /*SSD */
    public static boolean isCustomerClaroPeru(){
        return ("ClaroPeru".equalsIgnoreCase(companyName));
    }
    public static boolean isCustomerEntel(){
        return ("EntelPeru".equalsIgnoreCase(companyName));
    }

    public static boolean isCustomerTelefonicaO2UK(){
        return ("TelefonicaO2UK".equalsIgnoreCase(companyName));
    }
    public static boolean isCountryGermany(){
        return "TelefonicaGermany".equalsIgnoreCase(companyName);
    }

    public static boolean isCustomerTelefonicaColombia(){
        return "TelefonicaColombia".equalsIgnoreCase(companyName);
    }

    public static boolean isCustomerTelefonicaEcuador(){
        return "TelefonicaEcuador".equalsIgnoreCase(companyName);
    }

    public static boolean isCustomerVivoBrazil(){
        return ("VIVOBrazil".equalsIgnoreCase(companyName) ||
                "VIVOBrazilTest".equalsIgnoreCase(companyName) ||
                "DEMOWAD".equalsIgnoreCase(companyName));
    }

    public static boolean isCustomerSergio(){
        return "Sergio".equalsIgnoreCase(companyName);
    }

    public static boolean isMobilicisSSDStore(){
        return ("MobilicisSSDStore".equalsIgnoreCase(companyName));
    }

    public static boolean isMobilicisStore(){
        return ("MobilicisStore".equalsIgnoreCase(companyName));
    }

    public static boolean isTelofonicaLatam(){
        String[] telefonicaLatam = { "TelefonicaArgentina", "TelefonicaChile", "TelefonicaColombia", "TelefonicaIndia", "TelefonicaMexico", "TelefonicaPeru","TelefonicaEcuador" ,"TelefonicaUruguay"};
        return Arrays.asList(telefonicaLatam).contains(companyName);
    }
    public static boolean hideEmailSummeryStoreInfo(){
        return isTelofonicaLatam() || isCustomerEntel() || isCustomerClaroPeru() || isTradein()
                || isCustomerVivoBrazil() || isDemoSSD() || isTessaB();
    }
    public static boolean showCSATScreen() {
        return GlobalConfig.getInstance().isCSATEnabled();
    }

    public static boolean captureIMEI() {
        return !isMobilicisStore();
    }

    public static boolean enableCaptureIMEI() {
        return false;//!isMobilicisSSDStore();
    }

    public static boolean isQuickBatteryRequired(){
        return  "ssd".equalsIgnoreCase(BuildConfig.FLAVOR_flav) ||
                "telephonica".equalsIgnoreCase(BuildConfig.FLAVOR_flav) ||
                "claro".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean isDemoSSD(){
        return ("MobilicisWirelessDemo".equalsIgnoreCase(companyName));
    }

    public static boolean isTessaB() {
        return ("TessaB".equalsIgnoreCase(companyName));
    }

    public static boolean promptTradeInSelection() {
        return GlobalConfig.getInstance().isDiagTradeInEnabled();
    }

    public static boolean needToSkipResolutions(){
        return isTradein();
    }
}
