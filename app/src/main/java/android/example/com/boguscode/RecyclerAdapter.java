package android.example.com.boguscode;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * RecycleAdapter fills data into the recycle views.
 * It creates card view for each video.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VideoHolder> {
    private ArrayList<VideoModel> mVideos;

    public RecyclerAdapter(ArrayList<VideoModel> videos) {
        mVideos = videos;
    }

    @Override
    public RecyclerAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_card, parent, false);
        return new VideoHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.VideoHolder holder, int position) {
        VideoModel video = mVideos.get(position);
        holder.bindItem(video);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public static class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private VideoModel mVideo;
        private ImageView mImage;
        private TextView mName;
        private TextView mDescription;
        private TextView mDuration;
        private TextView mAuthor;

        public VideoHolder(View view) {
            super(view);

            mImage = (ImageView) view.findViewById(R.id.card_image);
            mName = (TextView) view.findViewById(R.id.card_name);
            mDescription = (TextView) view.findViewById(R.id.card_description);
            mDuration = (TextView) view.findViewById(R.id.card_duration);
            mAuthor = (TextView) view.findViewById(R.id.card_author);
            view.setOnClickListener(this);
        }

        public void bindItem(VideoModel video) {
            mVideo = video;
            Picasso.with(mImage.getContext()).load(mVideo.getUrl()).into(mImage);
            mName.setText(mVideo.getName());
            mDescription.setText(mVideo.getDescription());
            mDuration.setText(mVideo.getDuration());
            mAuthor.setText(mVideo.getArthor());
        }

        @Override
        public void onClick(View v) {
            Log.i("RecyclerAdapter", "recycle view CLICK!");
        }
    }

}
