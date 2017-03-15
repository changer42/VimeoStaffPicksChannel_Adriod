package android.example.com.boguscode;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * This class is responsible for data request. It calls certain API given corresponding url.
 */

public class DataRequester {
    private static final String TOKEN = "bearer b8e31bd89ba1ee093dc6ab0f863db1bd";
    private static final String AUTHORIZATION = "Authorization";

    private Activity mListeningActivity;
    private DataRequesterResponse mResponseListener;
    private String mBaseUrl;
    private String mPerPage;
    private String mPageNumber;
    private boolean mLoadingFlag = false;
    private boolean mWifiFlag = true;

    public DataRequester(Activity listeningActivity){
        mListeningActivity = listeningActivity;
        mResponseListener = (DataRequesterResponse) listeningActivity;
        if(listeningActivity.getClass() == MainActivity.class){
            mBaseUrl = MainActivity.BASE_URL;
            mPerPage = MainActivity.PER_PAGE;
            mPageNumber = MainActivity.PAGE_NUMBER;
        }
    }

    public void requestData(int pageNumber){
        mLoadingFlag = true;
        new StaffPicksAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pageNumber);
    }

    public boolean isLoading(){
        return mLoadingFlag;
    }
    private class StaffPicksAsyncTask extends AsyncTask<Integer, Void, ArrayList<JSONObject>> {

        @Override
        protected ArrayList<JSONObject> doInBackground(Integer... pageNumber) {
            String url = mBaseUrl+mPageNumber+pageNumber[0]+mPerPage;
            Log.d("dataRequester", "my url: "+url);
            final ArrayList<JSONObject> videos = new ArrayList<>();

            final HttpClient client = new DefaultHttpClient();
            final HttpGet httpGet = new HttpGet(url);
            final StringBuilder sb = new StringBuilder();
            httpGet.addHeader(AUTHORIZATION, TOKEN);

            try {
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject object = new JSONObject(sb.toString());
                JSONArray data = object.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    videos.add(data.getJSONObject(i));
                }
            } catch (ClientProtocolException e) {
                Log.i("Debug", "ClientProtocolException");
                e.printStackTrace();
            } catch (IOException e) {
                mWifiFlag = false;
                Log.i("Debug", "IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.i("Debug", "JSONException");
                e.printStackTrace();
            }

            Log.i("Hilda", "videos size after calling api: " + videos.size());

            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> result) {
            if (result.size() == 0 && !mWifiFlag){
                Toast.makeText(mListeningActivity,
                        "Please check if your device connects to wifi successfully.",
                        Toast.LENGTH_LONG).show();
            }
            for(JSONObject item : result){
                VideoModel newVideo = new VideoModel(item);
                mResponseListener.receivedNewPhoto(newVideo);
            }
            mLoadingFlag = false;
        }
    }

    public interface DataRequesterResponse {
        void receivedNewPhoto(VideoModel newVideo);
    }
}
