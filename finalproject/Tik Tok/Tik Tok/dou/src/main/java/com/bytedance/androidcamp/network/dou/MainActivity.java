package com.bytedance.androidcamp.network.dou;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.model.GetVideosResponse;
import com.bytedance.androidcamp.network.dou.model.PostVideoResponse;
import com.bytedance.androidcamp.network.dou.model.Video;
import com.bytedance.androidcamp.network.dou.util.ResourceUtils;
import com.bytedance.androidcamp.network.lib.util.ImageHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int addTool = 1808;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_IMAGE2 = 10;
    private static final int PICK_VIDEO = 2;
    private int PERMISSION_REQUEST_CODE = 1028;
    private static final String TAG = "MainActivity";
    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private Button mBtnRefresh;
    private FloatingActionButton fab;
    private RecyclerView.Adapter mAdapter;
    private Button btn_my;
    private int chooseVedio;
    private String studentID;

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(IMiniDouyinService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get_permissions();
        initRecyclerView();
        initBtns();
    }

    private void initBtns() {
        //初始化一些值
        chooseVedio = 0;//不筛选
        Intent intent = getIntent();
        String str = intent.getStringExtra("data");
        studentID = str;

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent,addTool);
            }
        });

        btn_my = findViewById(R.id.Mine);
        btn_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chooseVedio == 0){
                    chooseVedio = 1;//筛选我的视频
                    btn_my.setText("All Videos");
                }
                else if(chooseVedio == 1)
                {
                    chooseVedio = 0;//筛选我的视频
                    btn_my.setText("My Videos");
                }
                mBtnRefresh.callOnClick();//刷新
            }
        });

        mBtn = findViewById(R.id.Btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    //@TODO 1填充选择图片的功能
                    Toast.makeText(MainActivity.this,"Select Cover",Toast.LENGTH_LONG)
                            .show();
                    chooseImage(PICK_IMAGE);
                } else if (getString(R.string.select_a_video).equals(s)) {
                    //@TODO 2填充选择视频的功能
                    chooseVideo();
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        //@TODO 3调用上传功能
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = "
                                + mSelectedVideo
                                + ", mSelectedImage = "
                                + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });

        mBtnRefresh = findViewById(R.id.Refresh);
        mBtnRefresh.callOnClick();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }

        public void bind(final Activity activity, final Video video) {
            ImageHelper.displayWebImage(video.imageUrl, img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoActivity.launch(activity, video.videoUrl);
                }
            });
        }
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new MyViewHolder(
                        LayoutInflater.from(MainActivity.this)
                                .inflate(R.layout.video_item_view, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
                final Video video = mVideos.get(i);
                viewHolder.bind(MainActivity.this, video);
            }

            @Override
            public int getItemCount() {
                return mVideos.size();
            }
        };
        mRv.setAdapter(mAdapter);
    }

    public void chooseImage(int pick) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                pick);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = ["
                + requestCode
                + "], resultCode = ["
                + resultCode
                + "], data = ["
                + data
                + "]");

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            }
            else if (requestCode == PICK_IMAGE2) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                Toast.makeText(MainActivity.this,"Uploading",Toast.LENGTH_SHORT)
                        .show();
                postVideo();
            }
            else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
            else if(requestCode == addTool)
            {
                mSelectedVideo = data.getData();
                Toast.makeText(MainActivity.this,"Choose a cover for your video!",Toast.LENGTH_LONG)
                    .show();
                chooseImage(PICK_IMAGE2);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(MainActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        mBtn.setText("Uploading");
        mBtn.setEnabled(false);
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);
        //@TODO 4下面的id和名字替换成自己的
        miniDouyinService.postVideo(studentID, "XXX", coverImagePart, videoPart).enqueue(
                new Callback<PostVideoResponse>() {
                    @Override
                    public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                        if (response.body() != null) {
                            Toast.makeText(MainActivity.this, "Successful!" +  response.body().toString(), Toast.LENGTH_SHORT)
                                    .show();
                            mBtnRefresh.callOnClick();
                        }
                        mBtn.setText(R.string.select_an_image);
                        mBtn.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                        mBtn.setText(R.string.select_an_image);
                        mBtn.setEnabled(true);
                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                if (response.body() != null && response.body().videos != null) {
                    mVideos = response.body().videos;
                    //@TODO  5服务端没有做去重，拿到列表后，可以在端侧根据自己的id，做列表筛选。
                    if(chooseVedio == 1){
                        List<Video> tempVedio = new ArrayList<>();
                        for(int i=0;i<mVideos.size();i++){
                            if((mVideos.get(i).studentId).equals(studentID)){
                                tempVedio.add(mVideos.get(i));
                            }
                        }
                        mVideos = tempVedio;
                        Toast.makeText(MainActivity.this, "Uploader id：" + mVideos.get(0).studentId, Toast.LENGTH_SHORT).show();
                    }
                    mRv.getAdapter().notifyDataSetChanged();
                }
                mBtnRefresh.setText(R.string.refresh_feed);
                mBtnRefresh.setEnabled(true);
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                mBtnRefresh.setText(R.string.refresh_feed);
                mBtnRefresh.setEnabled(true);
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void get_permissions()//申请所有需要的权限
    {
        String[] permissions_camera = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this,permissions_camera,PERMISSION_REQUEST_CODE);
    }
}
