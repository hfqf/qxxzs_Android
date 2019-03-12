package com.points.autorepar.lib.sortlistview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import com.points.autorepar.bean.EmployeeInfo;

import java.util.List;

public class EmployeeSortAdapter extends BaseAdapter {
	private List<EmployeeInfo> list = null;
	private Context mContext;


	public EmployeeSortAdapter(Context mContext, List<EmployeeInfo> list) {
		this.mContext = mContext;
		this.list = list;
	}

	public void updateListView(List<EmployeeInfo> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public void setDate(List<EmployeeInfo> list)
	{
		this.list = list;
	}
	public int getCount() {
		if(this.list == null)
			return  0 ;
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

		final EmployeeInfo mContent = this.list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.employee_item, null);
			viewHolder.tv_type = (TextView) view.findViewById(R.id.tv_type);
			viewHolder.employee_list_cell_name = (TextView) view.findViewById(R.id.employee_list_cell_name);
			viewHolder.m_head = (ImageView) view.findViewById(R.id.contact_list_cell_show_content_head);
			viewHolder.employee_list_cell_status = (TextView) view.findViewById(R.id.employee_list_cell_status);
			viewHolder.employee_list_cell_phone = (TextView) view.findViewById(R.id.employee_list_cell_phone);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		if("1".equalsIgnoreCase(mContent.roletype))
		{
			viewHolder.tv_type.setText("技师");
		}else if("2".equalsIgnoreCase(mContent.roletype))
		{
			viewHolder.tv_type.setText("仓库保管员");
		}else if("3".equalsIgnoreCase(mContent.roletype))
		{
			viewHolder.tv_type.setText("店长");
		}

		viewHolder.employee_list_cell_name.setText(mContent.username);
		viewHolder.employee_list_cell_phone.setText(mContent.tel);

		if("1".equalsIgnoreCase(mContent.state))
		{
			viewHolder.employee_list_cell_status.setText("在职");
		}else if("2".equalsIgnoreCase(mContent.roletype))
		{
			viewHolder.employee_list_cell_status.setText("离职");
		}

		final String url = MainApplication.consts(this.mContext).BOS_SERVER+mContent.headurl+".png";
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
		TextView tv_type;
		TextView employee_list_cell_name;
		ImageView m_head;
		TextView  employee_list_cell_phone;
		TextView  employee_list_cell_status;
	}



}