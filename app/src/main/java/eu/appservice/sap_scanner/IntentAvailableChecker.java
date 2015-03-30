package eu.appservice.sap_scanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by Lukasz on 18.02.14.
 * ﹕ SAP Skanner
 * @version 1.0
 * this class is checking the availability of Intent
 */
public class IntentAvailableChecker {
    private Context myContext;

    public IntentAvailableChecker(Context myContext) {
        this.myContext = myContext;
    }

    /**
     * This function is checking the availability of intent
     * @param checkedIntent Intent which we want to check
     * @return if intent is available this function return true
     */
    public boolean isIntentAvailable(Intent checkedIntent) {
        final PackageManager packageManager = myContext.getPackageManager();
        List resolveInfo =
                packageManager.queryIntentActivities(checkedIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() > 0;
    }
}
