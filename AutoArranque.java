package org.lalalab.databiographyclient.databiographyclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by diegodiaz on 17/5/17.
 */

public class AutoArranque extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context,  LogService.class);
        context.startService(service);
    }
}
