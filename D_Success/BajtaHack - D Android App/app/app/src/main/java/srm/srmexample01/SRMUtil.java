package srm.srmexample01;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import srm.srmlib.SRMClient;

/**
 * @author Skupina D
 */
public class SRMUtil {


    public static void executeImageRetrieval(String imgURLpath, SRMClient mClient,
                                             final ImageView srmImageView, Activity activity,
                                             final MyCallback callback) {
        try {
            String imgStr = mClient.get(imgURLpath).content;

            // open file
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "srm_tmp.jpg");
            if (!file.exists()) {
                file.createNewFile();
            }

            // decode image byte array
            byte[] byteImg = Base64.decode(imgStr, Base64.DEFAULT);

            // create bitmap
            final Bitmap bmp = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // apply bitmap on image view component
                    srmImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 640, 480, false));
                    callback.executeActionOnFinish();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
