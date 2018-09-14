package com.example.administrator.nademo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbyInfo;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchFunctionType;
import com.amap.api.services.nearby.NearbySearchResult;
import com.amap.api.services.nearby.UploadInfo;
import com.amap.api.services.nearby.UploadInfoCallback;
import com.example.administrator.nademo.service.dataCollection.QueryDeviceData;

public class HomeActivity extends Activity implements NearbySearch.NearbyListener {

    private TextView mtxtShow;
    private ImageButton imgBtn_msg;
    private ImageButton imgBtn_task;
    private ImageButton imgBtn_setting;
    private MapView mMapView = null;
    AMap aMap = null;
    private MapView mapView = null;

    private UiSettings mUiSettings;//定义一个UiSettings对象
    NearbySearch mNearbySearch;//附近派单功能
    NearbySearch search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgBtn_msg = (ImageButton) findViewById(R.id.imageButton_msg);
        imgBtn_msg.setOnClickListener(msg);
        imgBtn_task = (ImageButton) findViewById(R.id.imageButton_task);
        imgBtn_task.setOnClickListener(task);
        imgBtn_setting = (ImageButton) findViewById(R.id.imageButton_setting);
        imgBtn_setting.setOnClickListener(setting);
        mtxtShow = (TextView) findViewById(R.id.mtxtShow);
        mMapView = (MapView) findViewById(R.id.map); //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        /**
         * 显示基本地图
         */
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
        //初始化地图控制器对象
        showMap();
        /**
         * 不显示缩放浮标, 定位按钮显示
         */
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);
        //aMap.setLocationSource(this);//通过aMap对象设置定位数据源的监听
        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
        /**
         * 定位蓝点Style的实现
         */
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.strokeColor(Color.BLACK);//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));//设置定位蓝点精度圆圈的填充颜色的方法。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.getMinZoomLevel();
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        /**
         * 显示固定点
         */
        showTrash();

        /**
         * 附近派单功能--检索
         */
        mNearbySearch = NearbySearch.getInstance(getApplicationContext());
        //设置附近监听
        NearbySearch.getInstance(getApplicationContext()).addNearbyListener(this);
        /**
         * 异步请求
         */
        //设置搜索条件
        NearbySearch.NearbyQuery query = new NearbySearch.NearbyQuery();
        //设置搜索的中心点
        query.setCenterPoint(new LatLonPoint(39, 114));
        //设置搜索的坐标体系
        query.setCoordType(NearbySearch.AMAP);
        //设置搜索半径
        query.setRadius(10000);
        //设置查询的时间
        query.setTimeRange(10000);
        //设置查询的方式驾车还是距离
        query.setType(NearbySearchFunctionType.DRIVING_DISTANCE_SEARCH);
        //调用异步查询接口
        NearbySearch.getInstance(getApplicationContext())
                .searchNearbyInfoAsyn(query);
        Log.d("+++++", "over2");
        //涉及到SDK的都必须开线程!!!!!!
        searchDeviceData();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    /**
     * 信息发送按钮--已完成
     * imgBtn_msg
     */
    public View.OnClickListener msg;

    {
        msg = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转页面--电话, 微信, 短信, QQ
                // Intent intent=new Intent(HomeActivity.this,HomeActivity.class);
                //startActivity(intent);
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setIcon(R.drawable.btm_msg_48);
                builder.setTitle("请选择通信方式");
                final String[] method = {"电话", "短信", "QQ", "微信"};
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                final int[] num = {0};
                builder.setSingleChoiceItems(method, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(HomeActivity.this, "选择 ：" + method[which], Toast.LENGTH_SHORT).show();
                        num[0] =which;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (num[0]) {
                            case 0://用intent启动拨打电
                                Log.d("111","0");
                                    Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "13815014565"));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for Activity#requestPermissions for more details.
                                            return;
                                        }
                                    }
                                    startActivity(intent1);
                                break;
                            case 1:
                                Log.d("111","1");
                                if (PhoneNumberUtils.isGlobalPhoneNumber("13815014565")) {
                                    Intent intent2 = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "13815014565"));
                                    intent2.putExtra("sms_body", "我是环卫工worker_001: \n");
                                    startActivity(intent2);
                                }
                                break;
                            case 2:
                                Log.d("111","2");
                                String url = "mqqwpa://im/chat?chat_type=wpa&uin=1638414737";//uin是发送过去的qq号码
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                break;
                            case 3:Log.d("111","3");
                                if (checkApkExist(getApplicationContext(),"com.tencent.mm")){
                                    Intent intent = new Intent();
                                    ComponentName cmp=new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
                                    intent.setAction(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setComponent(cmp);
                                    startActivity(intent);

                                }else{
                                   // ToastUtil.showShortToast("本机未安装微信应用");
                                }
                                break;
                            default:
                                break;

                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        };
    }

    /**
     * 任务列表按钮--查询状态
     * imgBtn_task
     */
    View.OnClickListener task=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //跳转页面--电话, 微信, 短信, QQ
            Intent intent=new Intent(HomeActivity.this,HomeActivity.class);
            startActivity(intent);
        }
    };
    /**
     * 个人中心按钮
     * imgBtn_setting
     */
    View.OnClickListener setting=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //跳转页面--电话, 微信, 短信, QQ
            Intent intent=new Intent(HomeActivity.this,MyActivity.class);
            startActivity(intent);
        }
    };

    /**
     * 显示基本地图
     */
    public void showMap(){
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));//放大倍数
        aMap = mapView.getMap();
    }



    //--------------------------------黄金分割线--------------------------------------------------------------//
    @Override
    protected void onDestroy(){
        super.onDestroy();//在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume(){
        super.onResume();//在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();//在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);//在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState); }

//---------------------检索回调---------------------------------

    /**
     * 清除位置信息
     * @param i
     */
    @Override
    public void onUserInfoCleared(int i) {
        String Id= String.valueOf(i);
        //获取附近实例，并设置要清楚用户的id
        NearbySearch.getInstance(getApplicationContext()).setUserID(Id);
        //调用异步清除用户接口
        NearbySearch.getInstance(getApplicationContext())
                .clearUserInfoAsyn();
    }

    /**
     * 解析返回结果
     * @param nearbySearchResult
     * @param i
     */
    @Override
    public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int i) {
        //搜索周边附近用户回调处理
        if(i == 1000){
            if (nearbySearchResult != null
                   && nearbySearchResult.getNearbyInfoList() != null
                    && nearbySearchResult.getNearbyInfoList().size() > 0) {
                NearbyInfo nearbyInfo = nearbySearchResult.getNearbyInfoList().get(0);
                showExitDialog("Info","周边搜索结果为size "+ nearbySearchResult.getNearbyInfoList().size() +
                                " first："+ nearbyInfo.getUserID() + "  " + nearbyInfo.getDistance()+ "  "
                                + nearbyInfo.getDrivingDistance() + "  "+ nearbyInfo.getTimeStamp() + "  "+
                                nearbyInfo.getPoint().toString());
                /*mResultText.setText("周边搜索结果为size "+ nearbySearchResult.getNearbyInfoList().size() + "
                        first："+ nearbyInfo.getUserID() + "  " + nearbyInfo.getDistance()+ "  "
                                + nearbyInfo.getDrivingDistance() + "  "+ nearbyInfo.getTimeStamp() + "  "+
                                nearbyInfo.getPoint().toString());*/
            } else {
                showExitDialog("tip","周边搜索结果为空");
                //mResultText.setText("周边搜索结果为空");
                Log.d("+++++","1");
            }
        }
        else{
            showExitDialog("tip","周边搜索出现异常");
            Log.d("+++++","2");
            //mResultText.setText("周边搜索出现异常，异常码为："+i);
        }
    }

    /**
     * 附近信息上传--主动调用
     * @param i
     */
    @Override
    public void onNearbyInfoUploaded(int i) {
        String Id= String.valueOf(i);
        mNearbySearch = NearbySearch.getInstance(getApplicationContext());
        Log.d("+++++","onNearbyInfoUploaded_start");
        search.startUploadNearbyInfoAuto(new UploadInfoCallback() {
            //设置自动上传数据和上传的间隔时间
            @Override
            public UploadInfo OnUploadInfoCallback() {
                UploadInfo loadInfo = new UploadInfo();
                loadInfo.setCoordType(NearbySearch.AMAP);
                //位置信息
                loadInfo.setPoint(new LatLonPoint(39, 114));
                //用户id信息
                loadInfo.setUserID("用户的id");
                return loadInfo;
            }
        }, 10000);
        Log.d("+++++", "onNearbyInfoUploaded_over");
    }

//---------------------系统调用-------------------------------------------
    /**
     *  调用系统自带弹框
     *  */
    private void showExitDialog(String title,String msg){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", null)
                .show();
    }

    /**
     * 判断应用是否存在
     * @param context
     * @param packageName
     * @return
     */
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    /**
     * 打点
     * tip: LatLan Constant类
     */
    public void addMarker_green(String LatLan, String title){
        aMap.addMarker(new MarkerOptions().position(new LatLng(12, 12))
                .title(title).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.trash_green))));
    }
    public void addMarker_red(String LatLan, String title){
        aMap.addMarker(new MarkerOptions().position(new LatLng(12,12))
                .title(title).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.trash_red))));
    }
    /**
     * 显示垃圾桶
     */
    public void showTrash(){
        addMarker_green("12,12","Point1");
        addMarker_red("12,12", "Point1");
    }
    //---------------------------------SDK_______________--------------------------------

    /**
     * 查询单个设备
     * 修改: 带参数: deviceId
     */
    public void searchDeviceData(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                Log.d("111","进入run方法");
                QueryDeviceData qd=new QueryDeviceData();
                try {
                    final String msg=qd.hello();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showExitDialog("Tip",msg);
                        }
                    });
                    //showExitDialog("",msg);
                    Log.d("111", "ok");
                } catch (Exception e) {
                    Log.d("111","Ex : "+e.toString());
                    //showExitDialog("Warning !","返回体为空!");
                }
            }
        }).start();

    }
}
