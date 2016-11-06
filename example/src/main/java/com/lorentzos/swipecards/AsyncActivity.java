package com.lorentzos.swipecards;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.lorentzos.flingswipe.OnExitListener;
import com.lorentzos.flingswipe.OnRecenterListener;
import com.lorentzos.flingswipe.OnScrollListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.lorentzos.flingswipe.internal.Direction;
import com.lorentzos.swipecards.data.GitHubService;
import com.lorentzos.swipecards.data.Member;
import com.lorentzos.swipecards.data.MemberAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.newThread;

public class AsyncActivity extends Activity {

	private final List<Member> list = new ArrayList<>();

	@InjectView(R.id.frame)
	SwipeFlingAdapterView flingContainer;
	private Disposable disposable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		ButterKnife.inject(this);

		final MemberAdapter memberAdapter = new MemberAdapter(this, list);

		flingContainer.setAdapter(memberAdapter);
		flingContainer.setOnExitListener(new OnExitListener() {
			@Override
			public void onExit(View view, @Direction int direction) {
				Log.i("MyActivity", "onExit " + direction);
				list.remove(0);
				memberAdapter.notifyDataSetChanged();
			}
		});

		// Optionally add an OnItemClickListener
		flingContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("MyActivity", "onItemClick " + position);
			}
		});

		flingContainer.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(View view, float scrollProgressPercent) {
				Log.i("MyActivity", "onScroll " + scrollProgressPercent);
			}
		});
		flingContainer.setOnRecenterListener(new OnRecenterListener() {
			@Override
			public void onRecenter(View view) {
				Log.i("MyActivity", "onRecenter");
			}
		});

		disposable = GitHubService.create().listOrgMembers("ReactiveX")
				.subscribeOn(newThread())
				.observeOn(mainThread())
				.subscribe(new Consumer<List<Member>>() {
					@Override
					public void accept(List<Member> members) throws Exception {
						list.addAll(members);
						memberAdapter.notifyDataSetChanged();
					}
				});

	}

	@Override
	protected void onDestroy() {
		disposable.dispose();
		super.onDestroy();
	}

	@OnClick(R.id.right)
	public void swipeRight() {
		//Trigger the right event manually.
		flingContainer.swipeRight();
	}

	@OnClick(R.id.left)
	public void swipeLeft() {
		//Trigger the right event manually.
		flingContainer.swipeLeft();
	}

}
