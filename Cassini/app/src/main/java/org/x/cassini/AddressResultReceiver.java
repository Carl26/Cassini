package org.x.cassini;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by shuangyang on 20/8/17.
 */

public class AddressResultReceiver extends ResultReceiver {

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    public String mLocation;

    private Receiver mReceiver;

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setLocation(String location) { location = mLocation;}

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        mLocation = resultData.getString(NewEntryActivity.FetchAddressIntentService.Constants.RESULT_DATA_KEY);
    }
}
