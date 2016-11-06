package com.lorentzos.swipecards.data;

import android.content.Context;
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

		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.card, viewGroup, false);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		Member member = getItem(i);

		viewHolder.name.setText(member.login);
		Glide.with(context)
				.load(member.avatarUrl)
				.centerCrop()
				.into(viewHolder.avatar);

		return view;
	}

	private static class ViewHolder {
		TextView name;
		ImageView avatar;

		ViewHolder(View convertView) {
			name = (TextView) convertView.findViewById(R.id.name);
			avatar = (ImageView) convertView.findViewById(R.id.avatar);
		}
	}

}
