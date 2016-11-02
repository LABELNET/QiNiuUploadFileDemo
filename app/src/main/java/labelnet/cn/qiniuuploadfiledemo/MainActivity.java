package labelnet.cn.qiniuuploadfiledemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String keyName;
    private String fileName = "image.jpg";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0:
                    tvProcess.setText("上传失败");
                    showToast("上传失败");
                    break;
                case 1:
                    tvProcess.setText("上传成功");
                    showToast("上传成功 : "+(msg.obj).toString());
                    try {
                        JSONObject jsonObj=new JSONObject((String) msg.obj);
                        keyName= (String) jsonObj.get("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    };

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
     * 加水印的图片地址
     * @return
     */
    private String getOtherImageURL(){
        String fix="?watermark/2/text/5Y6f5bCP5piO/font/5a6L5L2T/fontsize/700/fill/IzE3RTcyOA==/dissolve/100/gravity/Center";
        String URL="http://og0g4mpae.bkt.clouddn.com/";
        return URL+keyName+fix;
    }

    /**
     * 加载
     */
    private void onLoadImage() {
        if(keyName!=null) {
            Glide.with(this).load(getOtherImageURL()).into(ivImage);
        }else{
            showToast("请先拍照！后上传,再加载");
        }
    }

    /**
     * 上传
     */
    private void onUploadImage() {
        if(bm!=null) {
            new Thread(new UploadRunnable(fileUtil.getStorageDirectory() + File.separator + fileName,handler)).start();
        }else{
            showToast("请先拍照！后上传,再加载");
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


    /**
     * 上传的runnable接口
     */
    private class UploadRunnable implements Runnable {

        private String path;
        private Handler handler;

        public UploadRunnable(String path, Handler handler) {
            this.path = path;
            this.handler = handler;
        }

        @Override
        public void run() {
            Response res = null;
            Message msg=handler.obtainMessage();
            try {
                res = uploadManager.put(path,null, getToken());
                Log.v("MAIN", res.bodyString());

                if(res.isOK()){
                    msg.what=1;
                    msg.obj=res.bodyString();
                }else{
                    msg.what=0;
                }

            } catch (QiniuException e) {
                e.printStackTrace();
                try {
                    Log.v("MAIN", e.response.bodyString());
                    msg.what=0;
                } catch (QiniuException e1) {

                }
            }
            handler.sendMessage(msg);

        }
    }


}
