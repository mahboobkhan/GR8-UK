package com.Gr8niteout.pub;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
/**
 * Created by riteshs on 1/18/2017.
 */

public class SingleMediaScanner  implements MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private String mFile;

    public SingleMediaScanner(Context context, String f) {
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile, null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
    }

}
