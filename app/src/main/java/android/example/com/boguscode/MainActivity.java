package android.example.com.boguscode;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DataRequester.DataRequesterResponse{
    public static final String BASE_URL = "https://api.vimeo.com/channels/staffpicks/videos?";
    public static final String PER_PAGE = "&per_page=12";
    public static final String PAGE_NUMBER = "page=";
    private final Activity mActivity = this;
    private final ArrayList<VideoModel> mVideoList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerAdapter mAdapter;
    private DataRequester mDataRequester;

    private int pageNumberCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataRequester = new DataRequester(mActivity);

        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mGridLayoutManager = new GridLayoutManager(this, 2);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
        }else{
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        }

        mAdapter = new RecyclerAdapter(mVideoList);
        mRecyclerView.setAdapter(mAdapter);

        setRecyclerViewScrollListener();

        pageNumberCount = 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mVideoList.size() == 0) {
            mDataRequester.requestData(pageNumberCount);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mAdapter.notifyDataSetChanged();
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void receivedNewPhoto(final VideoModel newVideo) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVideoList.add(newVideo);
                mAdapter.notifyItemInserted(mVideoList.size());
            }
        });
    }
    private int getLastVisibleItemPosition() {
        int itemCount;
        if (mRecyclerView.getLayoutManager().equals(mLinearLayoutManager)) {
            itemCount = mLinearLayoutManager.findLastVisibleItemPosition();
        } else {
            itemCount = mGridLayoutManager.findLastVisibleItemPosition();
        }
        return itemCount;
    }

    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                pageNumberCount++;
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                if (!mDataRequester.isLoading() && (getLastVisibleItemPosition() + 1) > totalItemCount/2) {
                    mDataRequester.requestData(pageNumberCount);
                }
            }
        });
    }
}
