package com.points.autorepar.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.just.agentweb.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.points.autorepar.R;
import com.points.autorepar.common.Consts;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.points.autorepar.utils.MatrixImageView;

@SuppressLint("UseSparseArrays")
public class ImgDisplayActivity extends BaseActivity implements OnClickListener {
	private final static String TAG = "ImgDisplayActivity";
	/** 首先默认个文件保存路径 */
	private static final String SAVE_PIC_PATH= Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ?
			Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
	private static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/good/savePic";//保存的确切位置
	
	private final static int ISPRAISE = 1;
	private final static int NOTPRAISE = 0;
	private boolean isShowButton;

	public DisplayImageOptions defaultOptions,defaultOptionsGroup,defaultOptionsPhoto,defaultOptionsAlbum;


	private ViewPager vPager;
	private List imageList;
	private View[] mListViews;
	private DisplayImageOptions clazzImageOption;
	private LinearLayout buttonLinear;
	private ImageLoader mLoader;
	private TextView talkDynamic, supportNum, replyNum;
	private Button back;
	public ImageLoader imageLoader,imageLoader_group;
	private HashMap<Long, Integer> talkSplitPraise;
	private HashMap<Long, Integer> talkSplitPraiseNum;
	
	private int getPosition;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_display);
		imageList = (ArrayList) getIntent()
				.getBundleExtra("bundle").getSerializable("images");
		getPosition = getIntent().getIntExtra("position", 0);
//		isShowButton = getIntent().getBooleanExtra("isShowButton", false);
		mListViews = new View[imageList.size()];
		clazzImageOption = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appicon)
				.showImageForEmptyUri(R.drawable.appicon)
				.showImageOnFail(R.drawable.appicon).resetViewBeforeLoading() // default
																				// 设置图片在加载前是否重置、复位
				.delayBeforeLoading(500) // 下载前的延迟时间
				.cacheInMemory() // default 设置下载的图片是否缓存在内存中
				.cacheOnDisc() // default 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
																		// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default 设置图片的解码类型
				.displayer(new SimpleBitmapDisplayer()) // default 还可以设置圆角图片new
														// RoundedBitmapDisplayer(20)
				.handler(new Handler()) // default
				.build();


		defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appicon)
				.showImageForEmptyUri(R.drawable.appicon)
				.showImageOnFail(R.drawable.appicon).build();

		defaultOptionsGroup = new DisplayImageOptions.Builder().cacheOnDisc()
				.showStubImage(R.drawable.appicon)
				.showImageForEmptyUri(R.drawable.appicon)
				.showImageOnFail(R.drawable.appicon).build();

		defaultOptionsPhoto = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory()
				.showStubImage(R.drawable.appicon)
				.showImageForEmptyUri(R.drawable.appicon)
				.showImageOnFail(R.drawable.appicon).build();

		defaultOptionsAlbum = new DisplayImageOptions.Builder().cacheOnDisc().cacheInMemory()
				.showStubImage(R.drawable.appicon)
				.showImageForEmptyUri(R.drawable.appicon)
				.showImageOnFail(R.drawable.appicon).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).build();
		imageLoader = ImageLoader.getInstance();
		imageLoader_group = ImageLoader.getInstance();
			imageLoader.init(config);
		ImageLoaderConfiguration config1 = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptionsGroup).build();
		imageLoader_group.init(config1);

		mLoader = ImageLoader.getInstance();
		talkSplitPraise = new HashMap<Long, Integer>();
		talkSplitPraiseNum = new HashMap<Long, Integer>();

		init();
	}


	

	
	
	private void init() {
		vPager = (ViewPager) findViewById(R.id.vPager);
		buttonLinear = (LinearLayout) findViewById(R.id.button_linear);
		talkDynamic = (TextView) findViewById(R.id.talk_dynamic);
		back = (Button) findViewById(R.id.common_navi_back);
		back.setOnClickListener(this);
		if(!isShowButton){
			buttonLinear.setVisibility(View.GONE);
		}
		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(final int arg0) {

				setTitle("照片详情 (" + (arg0 + 1) + "/" + imageList.size() + ")");
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		vPager.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View initView;
				if (mListViews[position] == null) {
					initView = View.inflate(ImgDisplayActivity.this,
							R.layout.item_img_display, null);
					mListViews[position] = initView;
				} else {
					initView = mListViews[position];
				}
				final MatrixImageView imgView = (MatrixImageView) initView
						.findViewById(R.id.display_img);
				String url = (String)imageList.get(position);
				mLoader.displayImage(url, imgView,
						clazzImageOption);
				mLoader.loadImage(url,
						clazzImageOption, new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								imgView.setEnabled(false);
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
                                                        FailReason arg2) {
							}

							@Override
							public void onLoadingComplete(String arg0, View arg1,
                                                          Bitmap arg2) {
								imgView.setEnabled(true);
							}

							@Override
							public void onLoadingCancelled(String arg0, View arg1) {
							}
						});
				if (isShowButton) {

				}
				container.addView(initView, 0);
				return initView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				((ViewPager) container).removeView(mListViews[position]);
			}

			@Override
			public int getCount() {
				return imageList.size();
			}
			
		});
		vPager.setCurrentItem(getPosition, false);
		vPager.setOffscreenPageLimit(4);



		setTitle("照片详情 (" + (getPosition + 1) + "/" + imageList.size() + ")");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_navi_back:
			finish();
			break;
		}
	}
	


	private File saveFile(Bitmap bm, String fileName, String path) throws IOException {
		String subForder = SAVE_REAL_PATH + path;
		File foder = new File(subForder);
		if (!foder.exists()) {
			foder.mkdirs();
		}
		File myCaptureFile = new File(subForder, fileName);
		if (!myCaptureFile.exists()) {
			myCaptureFile.createNewFile();
		}
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
		return myCaptureFile;
	}
	
	/**
	 * 根据图片的url路径获得Bitmap对象
	 * @param url
	 * @return
	 */
	private Bitmap returnBitmap(String url) {
		URL fileUrl = null;
		Bitmap bitmap = null;

		try {
			fileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		try {
			 ConnectivityManager connectivity = (ConnectivityManager) this
		                .getSystemService(Context.CONNECTIVITY_SERVICE);
		        if (connectivity != null) {  
		            NetworkInfo info = connectivity.getActiveNetworkInfo();
		            if (info != null && info.isConnected()) {
		                // 当前网络是连接的 
		            	if (info.isAvailable()) {
		                    // 当前所连接的网络可用  
		                	HttpURLConnection conn = (HttpURLConnection) fileUrl
		        					.openConnection();
		        			conn.setDoInput(true);
		        			conn.connect();
		        			InputStream is = conn.getInputStream();
		        			bitmap = BitmapFactory.decodeStream(is);
		        			is.close();
						}
		            }  
		        }  
		}catch (IOException e1) {
			e1.printStackTrace();
		}
		return bitmap;

	}
	
	@SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	super.handleMessage(msg);
	    	if (msg.getData().getInt("value")==0) {

			}else if (msg.getData().getInt("value")==1) {

			}
	    }
	};
	
	private void sendMessage(int value){
		Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("value", value);  
        msg.setData(data);  
		handler.sendMessage(msg);
	}
}
