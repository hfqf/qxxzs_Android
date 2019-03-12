package com.points.autorepar.lib.sortlistview;

import java.util.List;

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
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.MainApplication;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private List<SortModel> list = null;
	private Context mContext;


	public SortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
	}

	public void updateListView(List<SortModel> list){
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

		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.m_head = (ImageView) view.findViewById(R.id.contact_list_cell_show_content_head);
			viewHolder.m_tel = (TextView) view.findViewById(R.id.contact_list_cell_show_content_tel);
			viewHolder.m_carType = (TextView) view.findViewById(R.id.contact_list_cell_show_content_cartype);
			viewHolder.m_carCode = (TextView) view.findViewById(R.id.contact_list_cell_show_carcode);
			viewHolder.m_isBind = (TextView) view.findViewById(R.id.contact_list_cell_show_content_isbind);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		int section = getSectionForPosition(position);
		
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		Contact con =  mContent.contact;

		if("1".equalsIgnoreCase(con.getisVip())) {
			viewHolder.tvTitle.setText(this.list.get(position).getName()+"(会员)");
			viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_orange));
		}else{
			viewHolder.tvTitle.setText(this.list.get(position).getName());
			viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.black));
		}

		viewHolder.m_tel.setText(con.getTel());
		viewHolder.m_carType.setText(con.getCarType());
		viewHolder.m_isBind.setText(con.getIsbindweixin().equals("1")?"已绑定微信":"未绑定微信");
		viewHolder.m_carCode.setText(con.getCarCode());

		final String url = MainApplication.consts(this.mContext).BOS_SERVER+con.getHeadurl();
		final BaseActivity activity = (BaseActivity) this.mContext;

		Log.e("sortadapter",url);
		Log.e("sortadapter",con.getName());

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
		TextView tvLetter;
		TextView tvTitle;
		ImageView m_head;
		TextView  m_tel;
		TextView  m_carType;
		TextView  m_carCode;
		TextView m_isBind;
	}


	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}


	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	

	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}