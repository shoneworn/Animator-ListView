package com.example.testlistanimator;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity {

	// ListView1
	private ListView mLv1 = null;
	// ListView2
	private ListView mLv2 = null;
	// list1的adapter
	private LsAdapter1 mAdapter1 = null;
	// list2的adapter
	private LsAdapter2 mAdapter2 = null;
	// 支持的刷卡头
	String[] arrSupportShua = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期天"};
	
	List<String> mList1 = new ArrayList<String>();
	List<String> mList2 = new ArrayList<String>();
	/** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
	boolean isMove = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		initListener();
	}

	private void initView() {
		mLv1 = (ListView) findViewById(R.id.list1);
		mLv2 = (ListView) findViewById(R.id.list2);
	}

	private void makeList() {

		for (String shua : arrSupportShua) {
			mList2.add(shua);
		}
	}

	private void initData() {
		makeList();
		mAdapter1 = new LsAdapter1(MainActivity.this, mList1);
		mAdapter2 = new LsAdapter2(MainActivity.this, mList2);

		mLv1.setAdapter(mAdapter1);
		mLv2.setAdapter(mAdapter2);
	}

	private void initListener() {
		mLv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int location, long arg3) {
				//如果点击的时候，之前动画还没结束，那么就让点击事件无效
				if(isMove){
					return;
				}
				final ImageView img = getView(view);
				TextView mtv = (TextView) view.findViewById(R.id.item_tv);
				final int[] startLocation = new int[2];
				mtv.getLocationInWindow(startLocation);
				final String mShua = mList1.get(location);
				mAdapter2.setVisible(false);
				mAdapter2.addItem(mShua);
				
				new Handler().postDelayed(new Runnable() {
					public void run() {
						try {
							int[] endLocation = new int[2];
							// 获取终点的坐标
							mLv2.getChildAt(mLv2.getLastVisiblePosition()).getLocationInWindow(endLocation);
							MoveAnim(img, startLocation, endLocation, mShua, 1);
							mAdapter1.setRemove(location);
						} catch (Exception localException) {
						}
					}
				}, 50L);
				
			}
		});
		
		mLv2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int location, long arg3) {
				//如果点击的时候，之前动画还没结束，那么就让点击事件无效
				if(isMove){
					return;
				}
				final ImageView img = getView(view);
				TextView mtv = (TextView) view.findViewById(R.id.item_tv);
				final int[] startLocation = new int[2];
				mtv.getLocationInWindow(startLocation);
				final String mShua = mList2.get(location);
				mAdapter1.setVisible(false);
				mAdapter1.addItem(mShua);

				new Handler().postDelayed(new Runnable() {
					public void run() {
						try {
							int[] endLocation = new int[2];
							// 获取终点的坐标
							mLv1.getChildAt(mLv1.getLastVisiblePosition()).getLocationInWindow(endLocation);
							MoveAnim(img, startLocation, endLocation, mShua, 2);
							mAdapter2.setRemove(location);
						} catch (Exception localException) {
						}
					}
				}, 50L);

			}
		});
	}

	private void MoveAnim(ImageView moveView, int[] startLocation, int[] endLocation, String mShua, final int code) {

		int[] initLocation = new int[2];
		// 获取传递过来的VIEW的坐标
		moveView.getLocationInWindow(initLocation);
		// 得到要移动的VIEW,并放入对应的容器中
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
		
		//使用ObjectAnimator动画
		ObjectAnimator  mAnimator  = ObjectAnimator.ofFloat(mMoveView, "translationY", startLocation[1],endLocation[1]);
		mAnimator.setDuration(300);
		mAnimator.start();
		isMove = true;
		mAnimator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				isMove = true;
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				moveViewGroup.removeView(mMoveView);
				if(code==1){
					mAdapter2.setVisible(true);
					mAdapter2.notifyDataSetChanged();
					mAdapter1.remove();
					isMove = false;
				}else{
					mAdapter1.setVisible(true);
					mAdapter1.notifyDataSetChanged();
					mAdapter2.remove();
					isMove = false;
				}
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});
		
	 //使用TranslateAnimation。上面部分可以用这部分替换
		/*	// 创建移动动画
		TranslateAnimation moveAnimation = new TranslateAnimation(startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);// 动画时间
		// 动画配置
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);// 动画效果执行完毕后，View对象不保留在终止的位置
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isMove = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(mMoveView);
				// instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
				if(code==1){
					mAdapter2.setVisible(true);
					mAdapter2.notifyDataSetChanged();
					mAdapter1.remove();
					isMove = false;
				}else{
					mAdapter1.setVisible(true);
					mAdapter1.notifyDataSetChanged();
					mAdapter2.remove();
					isMove = false;
				}
			
			}
		});*/
		
	}

	/**
	 * 创建移动的ITEM对应的ViewGroup布局容器
	 */
	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(this);
		moveLinearLayout
				.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}

	/**
	 * 获取点击的Item的对应View，
	 * 
	 * @param view
	 * @return
	 */
	private ImageView getView(View view) {
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(cache);
		return iv;
	}

	/**
	 * 获取移动的VIEW，放入对应ViewGroup布局容器
	 * 
	 * @param viewGroup
	 * @param view
	 * @param initLocation
	 * @return
	 */
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}
}
