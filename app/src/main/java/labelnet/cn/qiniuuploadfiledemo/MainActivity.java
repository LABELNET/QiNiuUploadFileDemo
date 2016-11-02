package labelnet.cn.qiniuuploadfiledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

public class MainActivity extends AppCompatActivity {


    private static final String ACCESS_KEY = "7NlihBpbgq2sZlghFhfXQaDdbSbNylnc2fPBl7Os";
    private static final String SECRET_KEY = "HqAtbk1UFvwJWx5FkcidyLKeYvkzIGlnUnLcirj1";
    private static final String BUCKET_NAME = "og0g4mpae.bkt.clouddn.com";

    private UploadManager uploadManager;

    //---
    private Button btnUp, btnLoad,btnCamera;
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
        btnCamera= (Button) findViewById(R.id.btn_camera);
        tvProcess = (TextView) findViewById(R.id.tv_process);
        ivImage = (ImageView) findViewById(R.id.iv_image);
    }

    /**
     * 初始化变量
     */
    private void initData() {
        uploadManager = new UploadManager();
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
    }


}
