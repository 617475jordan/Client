package com.fg.androidtest.activty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.example.androidtest.R;


public class LoadActivity extends Activity{
	private ImageView loading_item;
	private String Ipdata;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
		initView();
	}

	//试图初始化
	private void initView() {
		loading_item=(ImageView) findViewById(R.id.splash_loading_item);
		Animation animation=AnimationUtils.loadAnimation(this, R.anim.splash_loading);
		loading_item.setAnimation(animation);//添加读取条动画
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
				Bundle bundle0 = new Bundle();
				bundle0.putString("key", Ipdata);
				intent.putExtras(bundle0);
				startActivity(intent);
				finish();
			}
		});
	}
}
