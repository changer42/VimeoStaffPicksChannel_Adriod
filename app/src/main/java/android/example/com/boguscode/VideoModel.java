package android.example.com.boguscode;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * This the model class of Video data.
 * It takes the JSON object in, then parse it.
 * For Scalability, there can be more parameters in the class since the api can provide us more information.
 * However, it's not used in this project currently. So others are not included. They can be added easily later.
 */
public class VideoModel implements Serializable {
    // More paramenters can be retrieved from JSON, but they are currently useless for this project
    // So they are not included for this version
    private String mUrl;
    private String mName;
    private String mDescription;
    private String mLink;
    private int mDuration;
    private String mArthor;

    public VideoModel(JSONObject video){
        mName = video.optString("name", "");
        try {
            mUrl = video.optJSONObject("pictures").optJSONArray("sizes")
                    .getJSONObject(video.optJSONObject("pictures").optJSONArray("sizes").length() - 1)
                    .optString("link", "");
            mDescription = video.optString("description", "");
            mLink = video.optString("link","");
            mDuration = video.optInt("duration");
            mArthor = video.optJSONObject("user").optString("name","");
        } catch (Exception e) {
            Log.d("VideoModel", "Can't parse json");
            e.printStackTrace();
        }
        if(mDescription == null || mDescription.equalsIgnoreCase("null")) mDescription = "";
        if(mName == null || mName.equalsIgnoreCase("null"))  mName= "No Name";
        if(mArthor == null || mArthor.equalsIgnoreCase("null"))  mArthor= "No Name";
    }

    public String getDescription() {
        return mDescription;
    }

    public String getLink() {
        return mLink;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }
    public String getDuration(){
        final int second = mDuration % 60;
        final int minute = mDuration / 60;
        if(second > 9) return minute+":"+second;
        else return minute+":0"+second;
    }

    public String getArthor() {
        return mArthor;
    }
}
