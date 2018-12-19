package com.manan.dev.shineymca.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.content.res.ResourcesCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.manan.dev.shineymca.R;

import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.WHITE;

/**
 * Created by neha on 7/17/2018.
 */

public class UserQRCodeGenerator {
    private Context context;
    private Bitmap userTicket;

    public Bitmap GenerateClick(String qrCodeData, Context mContext,int width , int height) {
        context = mContext;
        try {

            int smallestDimension = width < height ? width : height;
            //setting parameters for qr code
            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            userTicket =  CreateQRCode(qrCodeData, charset, hintMap, smallestDimension, smallestDimension);

        } catch (Exception ex) {
        }
        return userTicket;
    }

    private Bitmap CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {


        try {
            //generating qr code in bitmatrix type
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(context.getResources(), R.color.black, null) : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (Exception er) {
            return null;
        }
    }
}