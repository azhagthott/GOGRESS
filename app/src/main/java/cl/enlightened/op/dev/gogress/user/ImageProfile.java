package cl.enlightened.op.dev.gogress.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;

/**
 * Created by Francisco Barrios on 17-04-15.
 *
 */


public class ImageProfile extends AsyncTask<String, Void, Bitmap>{

    ImageView bmImage;

    public ImageProfile(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    @Override
    protected Bitmap doInBackground(String... url) {

        String urldisplay = url[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result){ bmImage.setImageBitmap(result);}
}

