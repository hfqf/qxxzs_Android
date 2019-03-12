package com.points.autorepar.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.InOutInfo;
import com.points.autorepar.lib.sortlistview.SortModel;

import java.util.List;

public class InOutAdapter extends BaseAdapter {
	private List<InOutInfo> list = null;
	private Context mContext;


	public InOutAdapter(Context mContext, List<InOutInfo> list) {
		this.mContext = mContext;
		this.list = list;
	}

	public void updateListView(List<InOutInfo> list){
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

		final InOutInfo mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();

			view = LayoutInflater.from(mContext).inflate(R.layout.inout_item, null);
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			viewHolder.m_head = (ImageView) view.findViewById(R.id.headurl);
			viewHolder.type = (TextView) view.findViewById(R.id.type);
			viewHolder.time = (TextView) view.findViewById(R.id.time);
			viewHolder.username = (TextView) view.findViewById(R.id.username);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.name.setText(mContent.good_name);
		String _type = "";
		int type = Integer.parseInt(mContent.type) ;
		if(1==type){
			_type = "采购入库";
		}else if(2 == type){
			_type = "开单出库";
		}else if(3 == type){
			_type = "工单删除入库";
		}else if(4 == type){
			_type = "采购退货出库";
		}else {

		}
		viewHolder.name.setText("配件名："+mContent.good_name+"  编码："+mContent.good_barcode);
		viewHolder.type.setText("操作："+_type+" 数量："+mContent.num);
		viewHolder.time.setText("操作时间:"+mContent.time);
		viewHolder.username.setText("操作人:"+mContent.dealer_username);

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
		TextView type;
		ImageView m_head;
		TextView  time;
		TextView  username;
	}



}