package cl.enlightened.op.dev.gogress.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by fran on 16-04-15.
 */
public class JSONParser extends AsyncTask<String,String,String>{

    private static final String URL = "http://api.recursos.datos.gob.cl/datastreams/invoke/FARMA-EN-TODO-CHILE?auth_key=3c81f633b4450d1a89d48b8d8b2df36b02390e37&output=json_array#sthash.EiybqL8W.dpuf";
    private static final String API_KEY = "3c81f633b4450d1a89d48b8d8b2df36b02390e37";
    private static final String LOG_TAG = "JSONParse Result:::";

    private void ExecuteAsyncTask() {}

    @Override
    public String doInBackground(String... params) {

        DefaultHttpClient dhttpClient = new DefaultHttpClient(
                new BasicHttpParams());

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Conten-Type", "application/json");

        InputStream inputStream = null;
        String result = null;

        try {

            HttpResponse response = dhttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "UTF-8"), 8);

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            result = stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject;

        try {

            Log.v(LOG_TAG, result);
            jsonObject = new JSONObject(result);
            JSONObject resultObject = jsonObject.getJSONObject("result");
            Log.v(LOG_TAG, resultObject.toString());

        } catch (Exception e) {
            // TODO: handle exception
        }

        return result;
    }

    @Override
    public void onPostExecute(String result) {}
}
