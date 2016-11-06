package com.lorentzos.swipecards.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lorentzos.swipecards.R;

import java.util.List;

/**
 *
 */

public class MemberAdapter extends BaseAdapter {

	private final LayoutInflater layoutInflater;
	private final Context context;
	private final List<Member> list;

	public MemberAdapter(Context context, List<Member> list) {
		layoutInflater = LayoutInflater.from(context);
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Member getItem(int i) {
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		View child = (View) layoutInflater.inflate(R.layout.item, viewGroup, false);

		Member member = getItem(i);

		((TextView) child.findViewById(R.id.name)).setText(member.login);
		ImageView imageView = (ImageView) child.findViewById(R.id.image_placeholder);

		Log.wtf("MemberAdapter", "getView " + member.avatarUrl);
		Glide.with(context)
				.load(member.avatarUrl)
				.centerCrop()
				.into(imageView);

		return child;
	}
}
