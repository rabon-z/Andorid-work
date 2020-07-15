package com.example.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    /*用来记录录像存储路径*/
    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/video.mp4");//设置录像存储路径
    Uri uri = Uri.fromFile(file);//文件转成Uri格式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button takePhoto = (Button) findViewById(R.id.take_photo);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        Button takeVideo = (Button) findViewById(R.id.take_video);
        Button showVideo = (Button) findViewById(R.id.show_videos);

        findViewById(R.id.permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermissionAllGranted(mPermissionsArrays)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(mPermissionsArrays, REQUEST_PERMISSION);
                    } else {

                    }
                } else {
                    Toast.makeText(MainActivity.this, "已经获取所有所需权限", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //对照相功能的响应
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在新的Intent里面打开，并且传递TAKE_PHOTO选项
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, image_album_show.class);//也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("id", TAKE_PHOTO);//使用显式Intent传递参数，用以区分功能
                intent.putExtras(bundle);

                MainActivity.this.startActivity(intent);//启动新的Intent
            }
        });


        //设置相册选择的响应
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在新的Intent里面打开，并且传递CHOOSE_PHOTO选项
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, image_album_show.class);//也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("id", CHOOSE_PHOTO);
                intent.putExtras(bundle);

                MainActivity.this.startActivity(intent);
            }
        });
        //设置录像选择的响应
        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存录像到指定的路径
                // File file = new File("storage/sdcard1/video.mp4" );//设置录像存储路径
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 激活系统的照相机进行录像，通过Intent激活相机并实现录像功能
                Intent intent = new Intent();
                intent.setAction("android.media.action.VIDEO_CAPTURE");
                intent.addCategory("android.intent.category.DEFAULT");

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 0);
            }
        });
        showVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //initVideoPath();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Second_Activity.class);//也可以这样写intent.setClass(MainActivity.this, OtherActivity.class);
                MainActivity.this.startActivity(intent);
                setContentView(R.layout.activity_second_);
            }
        });
    }
    private String[] mPermissionsArrays = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private final static int REQUEST_PERMISSION = 123;

    private boolean checkPermissionAllGranted(String[] permissions) {
        // 6.0以下不需要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            Toast.makeText(this, "已经授权" + Arrays.toString(permissions), Toast.LENGTH_LONG).show();
        }
    }
}
