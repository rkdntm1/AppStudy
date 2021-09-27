package net.e4net.firstapp.Utils;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

import net.e4net.firstapp.Activities.QRActivity;

public class QRUtils {
    public static void startQRScan(Activity activity){
        IntentIntegrator li = new IntentIntegrator(activity);
        li.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        li.setPrompt("QR이미지를 스캔해주세요");
        li.setOrientationLocked(true);
        li.setCaptureActivity(QRActivity.class);
        li.initiateScan();
    }
}
