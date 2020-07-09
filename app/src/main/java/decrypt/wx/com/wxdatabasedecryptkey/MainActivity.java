package decrypt.wx.com.wxdatabasedecryptkey;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.threekilogram.objectbus.bus.ObjectBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import decrypt.wx.com.wxdatabasedecryptkey.adapter.ContactAdapter;
import decrypt.wx.com.wxdatabasedecryptkey.adapter.ListAdapter;
import decrypt.wx.com.wxdatabasedecryptkey.bean.BaseBean;
import decrypt.wx.com.wxdatabasedecryptkey.bean.ChatRoom;
import decrypt.wx.com.wxdatabasedecryptkey.bean.Contact;
import decrypt.wx.com.wxdatabasedecryptkey.bean.Message;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 微信语音消息，图片，视频，收藏的语音，图片视频都是存放在本地，对应而本地的路径规则是
 * Environment.getExternalStorageDirectory().getPath() + "/"+tencent/MicroMsg/+ Md5Utils.md5Encode("mm" + uid)
 * 这个是当前用户的所有资料，都可以打包，详细情况请看下面博客地址
 */
public class MainActivity extends AppCompatActivity {
    public static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";
    private static final String WX_DB_DIR_PATH = WX_ROOT_PATH + "MicroMsg";

    private static final String WX_DB_FILE_NAME = "EnMicroMsg.db";


    private String mCurrApkPath = Environment.getExternalStorageDirectory().getPath() + "/";
    private static final String COPY_WX_DATA_DB = "wx_data.db";
    //拷贝到sd卡目录上
    String copyFilePath = mCurrApkPath + COPY_WX_DATA_DB;

    private Button openWxDb;
    private RecyclerView rvList;
    private RecyclerView rvContactList;

    private String password;
    private ListAdapter adapter;
    private ContactAdapter contactAdapter;
    private Map<String, String> concatMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();

        openWxDb = findViewById(R.id.openWxDb);
        rvList = findViewById(R.id.rvList);
        rvContactList = findViewById(R.id.rvContactList);


        ObjectBus.newList().toPool(() -> {
            //获取root权限
            DecryptUtiles.execRootCmd("chmod -R 777 " + WX_ROOT_PATH);
            //获取root权限
            DecryptUtiles.execRootCmd("chmod -R 777 " + copyFilePath);
            password = DecryptUtiles.initDbPassword(MainActivity.this);
            String uid = DecryptUtiles.initCurrWxUin();
            try {
                String path = WX_DB_DIR_PATH + "/" + Md5Utils.md5Encode("mm" + uid) + "/" + WX_DB_FILE_NAME;
                Log.e("path", copyFilePath);
                Log.e("path", path);
                Log.e("path", password);
                //微信原始数据库的地址
                File wxDataDir = new File(path);
                //将微信数据库拷贝出来，因为直接连接微信的db，会导致微信崩溃
                FileUtiles.copyFile(wxDataDir.getAbsolutePath(), copyFilePath);
                initView();
            } catch (Exception e) {
                Log.e("path", e.getMessage());
                e.printStackTrace();
            }
        }).run();

    }

    private void initView() {
        concatMap = new HashMap<>();
        adapter = new ListAdapter(null);
        contactAdapter = new ContactAdapter();

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this));
        rvContactList.setAdapter(contactAdapter);
        rvList.setNestedScrollingEnabled(false);
        rvContactList.setNestedScrollingEnabled(false);

        openWxDb.setOnClickListener(v -> {
            //操作sd卡上数据库
            Observable<BaseBean> observable = FileUtiles.openWxDb(new File(copyFilePath), MainActivity.this, password);
            Disposable dis = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bean -> {
                                if (bean == null) return;
                                switch (bean.getMsgType()) {
                                    case "contact":
                                        Contact contact = (Contact) bean;
                                        concatMap.put(contact.getUserName(), contact.getNickName());
                                        contactAdapter.addData(contact);
                                        break;
                                    case "chatRoom":
                                        ChatRoom chatRoom = (ChatRoom) bean;
                                        break;
                                    case "message":
                                        Message message = (Message) bean;
                                        if (message.isSend().equals("0")) {
                                            String name = concatMap.get(message.getTalker());
                                            if (message.getTalker().equals("weixin")) {
                                                name = "微信团队";
                                            }
                                            message.setNickName(name == null ? "" : name);
                                        } else {
                                            message.setNickName("本人");
                                        }
                                        adapter.addData(message);
                                        break;
                                }
                            }

                    );

        });
    }

    private void requestPermissions() {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}


