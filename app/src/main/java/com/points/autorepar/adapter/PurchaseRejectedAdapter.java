package com.points.autorepar.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.InOutInfo;
import com.points.autorepar.bean.PurchaseRejectedInfo;

import java.util.List;

public class PurchaseRejectedAdapter extends BaseAdapter {
	private List<PurchaseRejectedInfo> list = null;
	private Context mContext;


	public PurchaseRejectedAdapter(Context mContext, List<PurchaseRejectedInfo> list) {
		this.mContext = mContext;
		this.list = list;
	}

	public void updateListView(List<PurchaseRejectedInfo> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {

		 ViewHolder viewHolder = null;

		final PurchaseRejectedInfo mContent = list.get(position);
		if (viewHolder == null) {
			viewHolder = new ViewHolder();

			view = LayoutInflater.from(mContext).inflate(R.layout.purchaserejected_item, null);
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			viewHolder.m_head = (ImageView) view.findViewById(R.id.headurl);
			viewHolder.price = (TextView) view.findViewById(R.id.price);
			viewHolder.num = (TextView) view.findViewById(R.id.num);
//			viewHolder.username = (TextView) view.findViewById(R.id.username);
//			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.name.setText(mContent.good_name);
		String _type = "";
//		String type = mContent.type;
//		if("1".equalsIgnoreCase(type)){
//			_type = "????????????";
//		}else if("2".equalsIgnoreCase(type)){
//			_type = "????????????";
//		}else if("3".equalsIgnoreCase(type)){
//			_type = "???????????????????????????";
//		}else if("4".equalsIgnoreCase(type)){
//			_type = "??????????????????";
//		}else {
//
//		}
		viewHolder.name.setText("????????????"+mContent.good_name+"  ?????????"+mContent.good_barcode);
		viewHolder.price.setText("???" +mContent.price+"(????????????)");
		viewHolder.num.setText("x"+mContent.num);
//		viewHolder.username.setText("?????????:"+mContent.dealer_username);
//
		final String url = MainApplication.consts(this.mContext).BOS_SERVER+mContent.good_headurl+".png";
		final BaseActivity activity = (BaseActivity) this.mContext;


		final  ViewHolder _viewHolder = viewHolder;
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

				activity.imageLoader.get(url, new ImageLoader.ImageListener() {
					@Override
					public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
						_viewHolder.m_head.setImageBitmap(imageContainer.getBitmap());
					}
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						_viewHolder.m_head.setImageResource(R.drawable.appicon);
					}
				},200,200);
			}
		});


		return view;

	}
	


	final static class ViewHolder {
		TextView name;
		TextView price;
		ImageView m_head;
		TextView  num;
		TextView  username;
	}



}