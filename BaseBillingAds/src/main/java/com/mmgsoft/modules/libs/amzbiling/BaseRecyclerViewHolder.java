package com.mmgsoft.modules.libs.amzbiling;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;



/**
 * Created by khoahihi on 1/2/2018.
 * SavvyCom
 * dangkhoait1989@gmail.com
 */

public abstract class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {
	protected OnItemClickListener<T> mItemClickListener;
	public void setmItemClickListener(OnItemClickListener<T> itemClickListener){
		this.mItemClickListener = itemClickListener;
	}
	protected T mModel;
	protected BaseRecyclerAdapter<T> mAdapter;

	public BaseRecyclerViewHolder(View itemView) {
		super(itemView);
	}

	public T getmModel() {
		return mModel;
	}

	public void setmModel(T mModel) {
		this.mModel = mModel;
	}

	public BaseRecyclerAdapter<T> getmAdapter() {
		return mAdapter;
	}
	public void setmAdapter(BaseRecyclerAdapter<T> mAdapter){
		this.mAdapter = mAdapter;
	}

	public abstract void bindDataToViewHolder(int position);
}
