package com.lorentzos.swipecards;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.lorentzos.flingswipe.OnExitListener;
import com.lorentzos.flingswipe.OnRecenterListener;
import com.lorentzos.flingswipe.OnScrollListener;
import com.lorentzos.flingswipe.SwipeAdapterView;
import com.lorentzos.flingswipe.internal.Direction;
import com.lorentzos.swipecards.data.GitHubService;
import com.lorentzos.swipecards.data.Member;
import com.lorentzos.swipecards.data.MemberAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.newThread;

/**
 * Activity with example of a network call to obtain the data and load the avatars in the image
 * views.
 */
public class AsyncActivity extends AppCompatActivity {

	public static final String MEMBERS = "MEMBERS";
	@InjectView(R.id.frame)
	SwipeAdapterView flingContainer;
	@InjectView(R.id.coordinator_layout)
	CoordinatorLayout coordinatorLayout;
	private MemberAdapter memberAdapter;
	private Disposable disposable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);

		memberAdapter = new MemberAdapter(this);
		if (savedInstanceState == null) {
			disposable = GitHubService.createOrgs().listOrgMembers("ReactiveX")
					.subscribeOn(newThread())
					.retry() //naive retry
					.observeOn(mainThread())
					.subscribe(new Consumer<List<Member>>() {
						@Override
						public void accept(List<Member> members) throws Exception {
							memberAdapter.addAll(members);
							memberAdapter.notifyDataSetChanged();
						}
					});
		} else {
			List<Member> members = savedInstanceState.getParcelableArrayList(MEMBERS);
			memberAdapter.addAll(members);
		}

		flingContainer.setAdapter(memberAdapter);
		flingContainer.setOnExitListener(new OnExitListener() {
			@Override
			public void onExit(View view, @Direction int direction) {
				Log.i("MyActivity", "onExit " + direction);
				memberAdapter.remove(0);
				memberAdapter.notifyDataSetChanged();
			}
		});

		// Optionally add an onItemClickListener
		flingContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("MyActivity", "onItemClick " + position);
			}
		});

		// Optionally add an onScrollListener
		flingContainer.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(View view, float scrollProgressPercent, int direction) {
				Log.i("MyActivity", "onScroll " + direction + ": " + scrollProgressPercent);
			}
		});

		// Optionally add an onRecenterListener
		flingContainer.setOnRecenterListener(new OnRecenterListener() {
			@Override
			public void onRecenter(View view) {
				Log.i("MyActivity", "onRecenter");
			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(MEMBERS, memberAdapter.getAll());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		if (disposable != null) {
			disposable.dispose();
		}
		super.onDestroy();
	}

	@OnClick(R.id.up)
	public void swipeTop() {
		if (memberAdapter.isEmpty()) {
			Snackbar.make(coordinatorLayout, "Cannot swipe up. Adapter is empty.", Snackbar.LENGTH_SHORT).show();
			return;
		}
		//Trigger the up event manually.
		flingContainer.swipeUp();
	}

	@OnClick(R.id.left)
	public void swipeLeft() {
		if (memberAdapter.isEmpty()) {
			Snackbar.make(coordinatorLayout, "Cannot swipe left. Adapter is empty.", Snackbar.LENGTH_SHORT).show();
			return;
		}
		//Trigger the right event manually.
		flingContainer.swipeLeft();
	}

	@OnClick(R.id.right)
	public void swipeRight() {
		if (memberAdapter.isEmpty()) {
			Snackbar.make(coordinatorLayout, "Cannot swipe right. Adapter is empty.", Snackbar.LENGTH_SHORT).show();
			return;
		}
		//Trigger the right event manually.
		flingContainer.swipeRight();
	}

	@OnClick(R.id.down)
	public void swipeDown() {
		if (memberAdapter.isEmpty()) {
			Snackbar.make(coordinatorLayout, "Cannot swipe down. Adapter is empty.", Snackbar.LENGTH_SHORT).show();
			return;
		}
		//Trigger the down event manually.
		flingContainer.swipeDown();
	}
}
