package com.oruphones.nativediagnostic.util;



import com.oruphones.nativediagnostic.api.GlobalConfig;

import java.util.Arrays;

public class HybridTestUtils {

    private GlobalConfig mGlobalConfig;
    public HybridTestUtils(){
        mGlobalConfig = GlobalConfig.getInstance();
    }

    public boolean isHybridTestsEnabled() {
        return mGlobalConfig.getHybridTests().length > 0;
    }

    public boolean isHybridTest(String testName) {
        return Arrays.asList(mGlobalConfig.getHybridTests()).contains(testName);
    }

}
