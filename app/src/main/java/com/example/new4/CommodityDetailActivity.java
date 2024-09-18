package com.example.new4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.new4.adapter.AllCommodityAdapter;
import com.example.new4.bean.Head;
import com.google.gson.Gson;
import com.google.gson.ToNumberStrategy;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommodityDetailActivity extends AppCompatActivity {

    ImageView ivCommodity,ivUserHead;
    TextView title,price,address,outTime,tUserName;


    public int position,goodsId,userId,sellerId,goodPrice;
    Gson gson = new Gson();
    Head head = new Head();
    Button btn_buy ;






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_commodity);

        ivCommodity = findViewById(R.id.iv_commodity);
        ivUserHead = findViewById(R.id.iv_tuserHead);
        title = findViewById(R.id.tv_title);
        price = findViewById(R.id.tv_price);
        address =findViewById(R.id.tv_addr);
        tUserName = findViewById(R.id.tv_tuserName);
        outTime = findViewById(R.id.tv_time);
        btn_buy=findViewById(R.id.btn_buy);



        Bundle b = getIntent().getExtras();
        if( b != null) {

            goodsId=b.getInt("goodsId");
            position = b.getInt("position");
            userId = b.getInt("userId");

        }


        //返回
        ImageView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        get();


        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });


    }


    public void loadData(Data data) {


        if (isFinishing()) {
            return;  // Activity 已经结束，不再执行
        }
        title.setText(data.getContent());
        price.setText("¥"+data.getPrice()+"");
        address.setText(data.getAddr());
        tUserName.setText(data.getUsername());
        outTime.setText(data.getCreateTime());

        if (isFinishing()) {
            //不加载
        }else if (data.getImageUrlList() != null && !data.getImageUrlList().isEmpty()) {
            Glide.with(this)
                    .load(data.getImageUrlList().get(0))
                    .into(ivCommodity);
        } else {
            // 处理无图片数据时的逻辑，比如显示默认图片
            ivCommodity.setImageResource(R.drawable.icon_take_photo);
        }

        if (isFinishing()) {
            //不加载
        }else if (data.getAvatar() != null && !data.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(data.getAvatar())
                    .into(ivUserHead);
        }else{
            // 处理无图片数据时的逻辑，比如显示默认图片
            ivUserHead.setImageResource(R.drawable.icon_user_photo);
        }


    }

    private void get(){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/details?goodsId="+goodsId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .get()
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态

            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if(dataResponseBody.getMsg().equals("成功")){
                    Toast.makeText(CommodityDetailActivity.this,"成功获取到详细商品数据",Toast.LENGTH_SHORT).show();
                    Data data = new Data();
                    data = dataResponseBody.getData();
                    loadData(data);
                    goodPrice=data.getPrice();
                    sellerId=data.gettUserId();


                }
            });
        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody <T> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private Data data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public Data getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    public class Data {
        // 数据的 ID
        private String id;

        // 与数据关联的应用密钥
        private String appKey;

        // 数据相关的用户 ID
        private int tUserId;

        // 与数据相关的图像代码
        private String imageCode;

        // 数据的内容
        private String content;

        // 数据的价格
        private int price;

        // 与数据关联的地址
        private String addr;

        // 数据的类型 ID
        private int typeId;

        // 数据的类型名称
        private String typeName;

        // 数据的状态
        private int status;

        // 数据创建时间
        private String createTime;

        // 与数据相关的用户名
        private String username;

        // 与用户相关的头像
        private String avatar;

        // 与数据关联的图像 URL 列表
        private List<String> imageUrlList;
        // 数据的应用分享状态
        private int appIsShare;

        // 添加适当的注释以及相应的获取器和设置器

        // Getters and setters for all the fields


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public int gettUserId() {
            return tUserId;
        }

        public void settUserId(int tUserId) {
            this.tUserId = tUserId;
        }

        public String getImageCode() {
            return imageCode;
        }

        public void setImageCode(String imageCode) {
            this.imageCode = imageCode;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public List<String> getImageUrlList() {
            return imageUrlList;
        }

        public void setImageUrlList(List<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
        }

        public int getAppIsShare() {
            return appIsShare;
        }

        public void setAppIsShare(int appIsShare) {
            this.appIsShare = appIsShare;
        }
    }



    private void post(){
        new Thread(() -> {
            Head head = new Head();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/trading";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("buyerId", userId);
            bodyMap.put("goodsId", goodsId);
            bodyMap.put("price", goodPrice);
            bodyMap.put("sellerId", sellerId);
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback2);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback2 = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody2<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody2<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if(dataResponseBody.getMsg().equals("成功")){
                    Toast.makeText(CommodityDetailActivity.this,"购买成功",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CommodityDetailActivity.this,dataResponseBody.getMsg(),Toast.LENGTH_SHORT).show();
                }

            });
        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody2 <T> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private T data;

        public ResponseBody2(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public T getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

}
