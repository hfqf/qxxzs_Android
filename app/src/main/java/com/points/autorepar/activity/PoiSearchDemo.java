package com.points.autorepar.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.points.autorepar.R;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.PoiOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * ??????poi????????????
 */
public class PoiSearchDemo extends FragmentActivity implements
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap = null;
    /* ??????????????????????????? */
    private EditText editCity = null;
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;

    private String province;
    private String city;

    private LatLng center ;
    private int radius = 1000;
    private LatLng southwest;
    private LatLng northeast ;
    private LatLngBounds searchBound = new LatLngBounds.Builder().include(southwest).include(northeast).build();

    private int searchType = 0;  // ????????????????????????????????????

    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisearch);
        // ????????????????????????????????????????????????
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // ????????????????????????????????????????????????????????????
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        editCity = (EditText) findViewById(R.id.city);
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map))).getBaiduMap();

        /* ?????????????????????????????????????????????????????? */
        keyWorldsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* ??????????????????????????????????????????????????????onSuggestionResult()????????? */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(cs.toString())
                    .city(editCity.getText().toString()));
            }
        });

        requestCemera();
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true); // ??????gps
//        option.setCoorType("bd09ll"); // ??????????????????
//        option.setScanSpan(1000);
//        option.setIsNeedAddress(true);//?????????????????????????????????????????????????????????
//        option.setOpenGps(true);//???????????????false,??????????????????gps
//        option.setLocationNotify(true);
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//?????????????????????????????????????????????????????????????????????????????????
        option.setCoorType("gcj02");//???????????????bd09ll???gcj02???????????????????????????????????????
        option.setScanSpan(0);//???????????????0???????????????????????????????????????????????????????????????????????????1000ms???????????????
        option.setIsNeedAddress(true);//?????????????????????????????????????????????????????????
        option.setOpenGps(false);//???????????????false,??????????????????gps
        option.setLocationNotify(true);//???????????????false??????????????????gps???????????????1S1???????????????GPS??????
        option.setIsNeedLocationDescribe(true);//???????????????false??????????????????????????????????????????????????????BDLocation.getLocationDescribe?????????????????????????????????????????????????????????
        option.setIsNeedLocationPoiList(true);//???????????????false?????????????????????POI??????????????????BDLocation.getPoiList?????????
        option.setIgnoreKillProcess(false);//???????????????true?????????SDK???????????????SERVICE?????????????????????????????????????????????stop?????????????????????????????????????????????
        option.SetIgnoreCacheException(false);//???????????????false?????????????????????CRASH?????????????????????
        option.setEnableSimulateGps(false);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mLocClient.requestLocation();

    }

    private void requestCemera() {
        if (PermissionsUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            //???????????????????????????
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    //???????????????????????????????????????
                }


                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    //???????????????????????????????????????
                }
            }, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }


    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ???????????????????????????????????????
            if (location == null ) {
                return;
            }

            province = location.getProvince();
            city = location.getCity();

            editCity.setText(location.getCity());
            double mCurrentLat = location.getLatitude();
            double mCurrentLon = location.getLongitude();
            center = new  LatLng(mCurrentLat, mCurrentLon);

            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(data);

            LatLng latLng = new LatLng(location.getLatitude(),
                    location.getLongitude());

            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(msu);

            String icon = "icon_gcoding.png";

            LatLng latLng1 = new LatLng(mCurrentLat,mCurrentLon);
            Bundle bundle = new Bundle();
            bundle.putInt("index", 1);
            MarkerOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi(icon))
                    .extraInfo(bundle)
                    .position(latLng1);


            Marker marker = (Marker)mBaiduMap.addOverlay(options);
//            Bundle bundle = new Bundle();
//            //info???????????????????????????
//            bundle.putSerializable("info", info);
//            marker.setExtraInfo(bundle);
//            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
//            mBaiduMap.setOnMarkerClickListener(overlay);

            final String strAdd =  location.getAddrStr();
            final String strlat =  mCurrentLat+"";
            final String strlon =  mCurrentLon+"";

            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //???marker?????????info??????

                    new android.app.AlertDialog.Builder(PoiSearchDemo.this)
                            .setTitle("??????????????????????")
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {




                                    Intent data = new Intent(); //?????????????????? ?????????????????? ?????????????????????
                                    data.putExtra("province", province);
                                    data.putExtra("city", city);
                                    data.putExtra("addr", strAdd);
                                    data.putExtra("lon",strlon);
                                    data.putExtra("lat",strlat);

                                    setResult(RESULT_OK, data);
                                    finish();

//                            finishActivity(1);
                                }
                            })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    Toast.makeText(PoiSearchDemo.this,
                            "????????????",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });


//            mLocClient.stop();

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }




    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param v    ??????Button
     */
    public void searchButtonProcess(View v) {
        searchType = 1;

        String citystr = editCity.getText().toString();
        String keystr = keyWorldsView.getText().toString();

        mPoiSearch.searchInCity((new PoiCitySearchOption())
            .city(citystr)
            .keyword(keystr)
            .pageNum(loadIndex)
            .scope(1));
    }

    /**
     * ????????????????????????????????????
     *
     * @param v    ??????Button
     */
    public void  searchNearbyProcess(View v) {
        searchType = 2;
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
            .keyword(keyWorldsView.getText().toString())
            .sortType(PoiSortType.distance_from_near_to_far)
            .location(center)
            .radius(radius)
            .pageNum(loadIndex)
            .scope(1);

        mPoiSearch.searchNearby(nearbySearchOption);
    }

    public void goToNextPage(View v) {
        loadIndex++;
        searchButtonProcess(null);
    }

    /**
     * ????????????????????????????????????
     *
     * @param v    ??????Button
     */
    public void searchBoundProcess(View v) {
        searchType = 3;
        mPoiSearch.searchInBound(new PoiBoundSearchOption()
            .bound(searchBound)
            .keyword(keyWorldsView.getText().toString())
            .scope(1));

    }


    /**
     * ??????POI?????????????????????searchInCity???searchNearby???searchInBound?????????????????????
     *
     * @param result    Poi???????????????????????????????????????????????????????????????
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(PoiSearchDemo.this, "???????????????", Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);

            if(result.getAllPoi()!=null && result.getAllPoi().size()>0)
            {
                PoiInfo info = result.getAllPoi().get(0);

                province = info.getProvince();
                city = info.getCity();


            }
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();

            switch( searchType ) {
                case 2:
                    showNearbyArea(center, radius);
                    break;
                case 3:
                    showBound(searchBound);
                    break;
                default:
                    break;
            }

            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            String strInfo = "???";

            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }

            strInfo += "????????????";
//            Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG).show();
//
//            new android.app.AlertDialog.Builder(this)
//                    .setTitle("?????????????????????1?")
//                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    })
//                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    })
//                    .show();



        }
    }

    /**
     * ??????POI???????????????????????????searchPoiDetail?????????????????????
     * V5.2.0???????????????????????????????????????{@link #onGetPoiDetailResult(PoiDetailSearchResult)}??????
     * @param result    POI??????????????????
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(PoiSearchDemo.this, "????????????????????????", Toast.LENGTH_SHORT).show();
        } else {
          final String strAdd =  result.getAddress()+result.getName();
            final String strlat =  result.getLocation().latitude+"";
            final String strlon =  result.getLocation().longitude+"";
            new android.app.AlertDialog.Builder(this)
                    .setTitle("??????????????????????")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {




                            Intent data = new Intent(); //?????????????????? ?????????????????? ?????????????????????
                            data.putExtra("province", province);
                            data.putExtra("city", city);
                            data.putExtra("addr", province+city+strAdd);
                            data.putExtra("lon",strlon);
                            data.putExtra("lat",strlat);

                            setResult(RESULT_OK, data);
                            finish();

//                            finishActivity(1);
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            Toast.makeText(PoiSearchDemo.this,
                 result.getAddress()+":"+result.getName(),
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        if (poiDetailSearchResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(PoiSearchDemo.this, "????????????????????????", Toast.LENGTH_SHORT).show();
        } else {
            List<PoiDetailInfo> poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (null == poiDetailInfoList || poiDetailInfoList.isEmpty()) {
                Toast.makeText(PoiSearchDemo.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < poiDetailInfoList.size(); i++) {
                PoiDetailInfo poiDetailInfo = poiDetailInfoList.get(i);
                if (null != poiDetailInfo) {

//                    new android.app.AlertDialog.Builder(this)
//                            .setTitle("??????????????????????")
//                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .show();
//                    Toast.makeText(PoiSearchDemo.this,
//                        poiDetailInfo.getName() + ": " + poiDetailInfo.getAddress(),
//                        Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * ???????????????????????????????????????requestSuggestion?????????????????????
     *
     * @param res    Sug????????????
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }

        List<String> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }

        sugAdapter = new ArrayAdapter<>(PoiSearchDemo.this, android.R.layout.simple_dropdown_item_1line,
            suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    private class MyPoiOverlay extends PoiOverlay {
        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            // }
            return true;
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param center    ???????????????????????????
     * @param radius    ??????????????????????????????
     */
    public void showNearbyArea(LatLng center, int radius) {
//        BitmapDescriptor centerBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
//        MarkerOptions ooMarker = new MarkerOptions().position(center).icon(centerBitmap);
//        mBaiduMap.addOverlay(ooMarker);
//
//        OverlayOptions ooCircle = new CircleOptions().fillColor( 0xCCCCCC00 )
//            .center(center)
//            .stroke(new Stroke(5, 0xFFFF00FF ))
//            .radius(radius);
//
//        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * ????????????????????????????????????
     *
     * @param bounds     ????????????????????????
     */
    public void showBound( LatLngBounds bounds) {
//        BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);
//
//        OverlayOptions ooGround = new GroundOverlayOptions()
//            .positionFromBounds(bounds)
//            .image(bdGround)
//            .transparency(0.8f)
//            .zIndex(1);
//
//        mBaiduMap.addOverlay(ooGround);
//
//        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(bounds.getCenter());
//        mBaiduMap.setMapStatus(u);
//
//        bdGround.recycle();
    }
}
