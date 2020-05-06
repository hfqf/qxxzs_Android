package com.points.autorepar.http;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import android.widget.Toast;
import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.points.autorepar.MainApplication;
import com.points.autorepar.utils.LoggerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by points on 16/11/30.
 */
public class HttpManager {

    private final String TAG = "HttpManager";
    private MainApplication MainApplication;
    //volley请求线程池
    private RequestQueue mVolleyRequestQueue;

    private static HttpManager instance;

    public static synchronized HttpManager getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null)
                    instance = new HttpManager(context);
            }
        }
        return instance;
    }

    public HttpManager(Context context) {
        mVolleyRequestQueue = Volley.newRequestQueue(context);
        Activity hander = (Activity) context;
        MainApplication = (MainApplication) hander.getApplication();
    }

    //POST请求共有方法
    private void startNormalGet(String url,
                                Map paras,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        Log.e(TAG, "startNormalPost:url" + url + "paras" + LoggerUtil.jsonFromObject(paras));
        JSONObject jsonPara = new JSONObject(paras);
        String postUrl = MainApplication.consts().HTTP_URL + url;
        XZSJsonObjectRequest request = new XZSJsonObjectRequest(Request.Method.GET, postUrl, jsonPara, listener, errorListener);
        Log.e(TAG, postUrl);
        Log.e(TAG, request.toString());
        mVolleyRequestQueue.add(request);
    }

    //POST请求共有方法
    private void startNormalPost(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        Log.e(TAG, "startNormalPost:url" + url + "paras" + LoggerUtil.jsonFromObject(paras));
        JSONObject jsonPara = new JSONObject(paras);
        String postUrl = MainApplication.consts().HTTP_URL + url;
        XZSJsonObjectRequest request = new XZSJsonObjectRequest(Request.Method.POST, postUrl, jsonPara, listener, errorListener);
        Log.e(TAG, postUrl);
        Log.e(TAG, request.toString());
        mVolleyRequestQueue.add(request);
    }

    //POST请求共有方法
    public void post(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        Log.e(TAG, "startNormalPost:url" + url + "paras" + LoggerUtil.jsonFromObject(paras));
        JSONObject jsonPara = new JSONObject(paras);
        String postUrl = MainApplication.consts().HTTP_URL + url;
        XZSJsonObjectRequest request = new XZSJsonObjectRequest(Request.Method.POST, postUrl, jsonPara, listener, errorListener);
        Log.e(TAG, postUrl);
        Log.e(TAG, request.toString());
        mVolleyRequestQueue.add(request);
    }

    private void startNormalPostWithJSONObject(String url,
                                               JSONObject jsonObject,
                                               Response.Listener<JSONObject> listener,
                                               Response.ErrorListener errorListener) {
        Log.e(TAG, "startNormalPost:url" + url + "paras" + LoggerUtil.jsonFromObject(jsonObject));
        String postUrl = MainApplication.consts().HTTP_URL + url;
        XZSJsonObjectRequest request = new XZSJsonObjectRequest(Request.Method.POST, postUrl, jsonObject, listener, errorListener);
        Log.e(TAG, postUrl);
        Log.e(TAG, request.toString());
        mVolleyRequestQueue.add(request);
    }


    public void startNormalCommonPost(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        Log.e(TAG, "startNormalPost:url" + url + "paras" + LoggerUtil.jsonFromObject(paras));
        JSONObject jsonPara = new JSONObject(paras);
        String postUrl = MainApplication.consts().HTTP_URL + url;
        XZSJsonObjectRequest request = new XZSJsonObjectRequest(Request.Method.POST, postUrl, jsonPara, listener, errorListener);
        Log.e(TAG, postUrl);
        Log.e(TAG, request.toString());
        mVolleyRequestQueue.add(request);
    }


    //POST请求共有方法
    public void startNormalFilePost(
            String url,
            String fileName,
            File file,
            Map paras,
            final Response.Listener<JSONObject> succeedListener,
            final Response.ErrorListener errorListener) {
        Log.e(TAG, "startNormalFilePost1:url" + url + "paras" + LoggerUtil.jsonFromObject(paras));
        //构造参数列表
        List<Part> partList = new ArrayList<Part>();
        partList.add(new StringPart("fileName", fileName));
        try {
            partList.add(new FilePart("file", file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//生成请求
        String postUrl = MainApplication.consts().HTTP_URL + url;

        MultipartRequest profileUpdateRequest = new MultipartRequest(postUrl, partList.toArray(new Part[partList.size()]), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //处理成功返回信息
                Log.e(TAG, "startNormalFilePost2:url"  + response);

                try {
                    JSONObject result = new JSONObject(response);

                    mVolleyRequestQueue.getCache().clear();

                    succeedListener.onResponse(result);
                } catch (JSONException e) {
                    errorListener.onErrorResponse(new VolleyError(e.getMessage()));
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //处理失败错误信息
                Log.e(TAG, "startNormalFilePost3:url"  + error.getMessage());
                errorListener.onErrorResponse(error);
            }
        });
//将请求加入队列
        mVolleyRequestQueue.add(profileUpdateRequest);
    }


    /*-------------------------------------------2.1api-------------------------------------------------------*/

    /**
     * 增加新用户
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void addNewUser(String url,
                           Map paras,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 登录
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void startLogin(String url,
                           Map paras,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**************************************************客户相关接口*******************************************************/

    /**
     * 添加客户
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void addContact(String url,
                           Map paras,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 更新某个客户
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void updateContact(String url,
                              Map paras,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 删除某个客户
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void deleteOneContact(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 删除某个客户
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void getTopImage(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 上传所有本地联系人数据
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void uploadAllContacts(String url,
                                  JSONObject paras,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        startNormalPostWithJSONObject(url, paras, listener, errorListener);
    }

    /**
     * 同步所有联系人数据
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void queryAllContacts(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**************************************************维修记录相关接口*******************************************************/

    /**
     * 增加新的维修记录
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void addNewRepair(String url,
                             Map paras,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 删除一条维修记录
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void delOneRepair(String url,
                             Map paras,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 删除某个客户的维修记录
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void delAllRepair(String url,
                             Map paras,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 更新某个客户的维修记录
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void updateOneRepair(String url,
                                Map paras,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 上传所有本地维修记录数据
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void uploadAllRepairs(String url,
                                 JSONObject paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        startNormalPostWithJSONObject(url, paras, listener, errorListener);
    }


    /**
     * 同步服务器上的所有维修记录数据
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void queryAllRepair(String url,
                               Map paras,
                               Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /****************************************3.0Api*************************************************/

    /**
     * 获取所有已到期的维修记录
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void queryAllTipedRepair(String url,
                                    Map paras,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    public void queryCustomerOrders(String url,
                                    Map paras,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 获取某个客户的所有维修记录
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void queryContactAllRepair(String url,
                                      Map paras,
                                      Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 新增收费条目
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void addRepairItem(String url,
                              Map paras,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 删除某条收费条目
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void deleteRepairItem(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 删除某个客户的所有收费条目
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void deleteRepairItems(String url,
                                  Map paras,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 获取某个维修详情的所有收费条目
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void queryAllRepairItem(String url,
                                   Map paras,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 更新头像
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void updateHeadUrl(String url,
                              Map paras,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 更新用户名
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void updateName(String url,
                           Map paras,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 获取当然维修记录统计
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void queryTodayServiceInfo(String url,
                                      Map paras,
                                      Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 检查最新版本
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void checkupVersion(String url,
                               Map paras,
                               Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {
        startNormalGet(url, paras, listener, errorListener);
    }


    /*****************************************客户预约********************************************/

    /**
     * 获取客户预约列表
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void getCustomerOrdersList(String url,
                                      Map paras,
                                      Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 更新客户预约状态
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void updateCustomerOrderState(String url,
                                      Map paras,
                                      Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    /**
     * 删除客户预约
     *
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void delCustomerOrder(String url,
                                         Map paras,
                                         Response.Listener<JSONObject> listener,
                                         Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }
    //员工相关

    /**
     * 员工登录
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void employeeLogin(String url,
                                 Map paras,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 获取未完成服务
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void getMyUnFinishedServicesList(String url,
                              Map paras,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }

    public void repairerCommit(String url,
                                            Map paras,
                                            Response.Listener<JSONObject> listener,
                                            Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**
     * 获取验证码
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void getVeirifyCode(String url,
                                            Map paras,
                                            Response.Listener<JSONObject> listener,
                                            Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }


    /**************************************3.4.0*********************************************/


    /**
     * 获取服务管理列表
     * @param url
     * @param paras
     * @param listener
     * @param errorListener
     */
    public void getAllServiceTypePreviewList(String url,
                               Map paras,
                               Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {
        startNormalPost(url, paras, listener, errorListener);
    }
}

