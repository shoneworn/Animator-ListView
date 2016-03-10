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
	// list1��adapter
	private LsAdapter1 mAdapter1 = null;
	// list2��adapter
	private LsAdapter2 mAdapter2 = null;
	// ֧�ֵ�ˢ��ͷ
	String[] arrSupportShua = { "����һ", "���ڶ�", "������", "������", "������", "������","������"};
	
	List<String> mList1 = new ArrayList<String>();
	List<String> mList2 = new ArrayList<String>();
	/** �Ƿ����ƶ�����������Ƕ���������Ž��е����ݸ��棬�����������Ϊ�˱������̫Ƶ����ɵ����ݴ��ҡ� */
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
				//��������ʱ��֮ǰ������û��������ô���õ���¼���Ч
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
							// ��ȡ�յ������
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
				//��������ʱ��֮ǰ������û��������ô���õ���¼���Ч
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
							// ��ȡ�յ������
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
		// ��ȡ���ݹ�����VIEW������
		moveView.getLocationInWindow(initLocation);
		// �õ�Ҫ�ƶ���VIEW,�������Ӧ��������
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
		
		//ʹ��ObjectAnimator����
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
		
	 //ʹ��TranslateAnimation�����沿�ֿ������ⲿ���滻
		/*	// �����ƶ�����
		TranslateAnimation moveAnimation = new TranslateAnimation(startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);// ����ʱ��
		// ��������
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);// ����Ч��ִ����Ϻ�View���󲻱�������ֹ��λ��
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
				// instanceof �����ж�2��ʵ���ǲ���һ�����жϵ������DragGrid����OtherGridView
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
	 * �����ƶ���ITEM��Ӧ��ViewGroup��������
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
	 * ��ȡ�����Item�Ķ�ӦView��
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
	 * ��ȡ�ƶ���VIEW�������ӦViewGroup��������
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
