package labelnet.cn.qiniuuploadfiledemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.File;
import java.io.IOException;

import labelnet.cn.qiniuuploadfiledemo.Util.FileUtil;


public class MainActivity extends AppCompatActivity {


    private static final String ACCESS_KEY = "7NlihBpbgq2sZlghFhfXQaDdbSbNylnc2fPBl7Os";
    private static final String SECRET_KEY = "HqAtbk1UFvwJWx5FkcidyLKeYvkzIGlnUnLcirj1";
    private static final String BUCKET_NAME = "labelnet";

    private UploadManager uploadManager;
    private FileUtil fileUtil;
    private Bitmap bm;
    private String keyName = "image";
    private String fileName = "image.jpg";

    //---
    private Button btnUp, btnLoad, btnCamera;
    private TextView tvProcess;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        initEvents();
    }

    /**
     * 初始化事件
     */

    private void initEvents() {

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePhoto();
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadImage();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadImage();
            }
        });
    }


    /**
     * 初始化布局
     */
    private void initView() {
        btnUp = (Button) findViewById(R.id.btn_up);
        btnLoad = (Button) findViewById(R.id.btn_load);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        tvProcess = (TextView) findViewById(R.id.tv_process);
        ivImage = (ImageView) findViewById(R.id.iv_image);
    }

    /**
     * 初始化变量
     */
    private void initData() {
        uploadManager = new UploadManager();
        fileUtil = new FileUtil(this);
    }


    /**
     * 1.生成token
     * 正式开发不可以使用，安全性差；
     */
    private String getToken() {
        return Auth.create(ACCESS_KEY, SECRET_KEY).uploadToken(BUCKET_NAME);
    }


    /**
     * 拍照
     */
    private void onTakePhoto() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, 1);
    }

    /**
     * 加载
     */
    private void onLoadImage() {

    }

    /**
     * 上传
     */
    private void onUploadImage() {
        if(bm!=null) {
            new Thread(new UploadRunnable(fileUtil.getStorageDirectory() + File.separator + fileName)).start();
        }else{
            showToast("请先拍照！后上传");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        bm = (Bitmap) extras.get("data");
        ivImage.setImageBitmap(bm);
        try {
            fileUtil.savaBitmap(fileName, bm);
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("MAIN", "保存失败");
        }
    }


    private class UploadRunnable implements Runnable {

        private String path;

        public UploadRunnable(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            Response res = null;
            try {
                res = uploadManager.put(path, "image", getToken());
                Log.v("MAIN", res.bodyString());
                if(res.isOK()){
                    showToast("上传成功");
                }else{
                    showToast("上传失败");
                }

            } catch (QiniuException e) {
                e.printStackTrace();
                try {
                    Log.v("MAIN", e.response.bodyString());
                    showToast("上传失败");
                } catch (QiniuException e1) {

                }
            }

        }
    }


}
