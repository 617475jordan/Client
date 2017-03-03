package com.fg.androidtest.activty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.*;
import com.example.androidtest.R;
import com.fg.androidtest.utils.Autjcode;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private Button changeBack;
	private Button changeCheck;
	private Button changeBtn;
	private EditText registerPwd;
	private EditText changeId;
	private EditText changePassword;
	private EditText changeAuth;
	private EditText turePassword;
	private TextView registerText;
	private TextView changeBackText;
	private TextView changeIdText;
	private TextView changePwText;
	private TextView truePwText;
	private TextView changeAuthText;
	private ImageView changeAuthimg;
	private String register,isPhone, isPassword, isTruePassword, Autecode, Autecodeimg;
	private int phoneflag, passwordflag, autecodeflag,registerflag;
	private ProgressDialog mDialog;
	private String responseMsg = "";
	private static final int REQUEST_TIMEOUT = 5 * 1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000;  //设置等待数据超时时间10秒钟
	private static final int LOGIN_OK = 1;
    private String value;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_changepassword);
		initView();
		value = IPUtils.getServerIpAddress(ChangePasswordActivity.this);
	}

	private void initView() {
		changeBack = (Button) findViewById(R.id.changeBack);
		changeBack.setOnClickListener(this);
		changeCheck = (Button) findViewById(R.id.changeCheck);
		changeCheck.setOnClickListener(this);
		changeBtn = (Button) findViewById(R.id.changeBtn);
		changeBtn.setOnClickListener(this);
		changeBackText = (TextView) findViewById(R.id.changeBackText);
		changeBackText.setOnClickListener(this);
		changeId = (EditText) findViewById(R.id.changeId);
		changeId.setOnFocusChangeListener(this);
		changePassword = (EditText) findViewById(R.id.changePassword);
		changePassword.setOnFocusChangeListener(this);
		changeAuth = (EditText) findViewById(R.id.changeAuth);
		changeAuth.setOnFocusChangeListener(this);
		changeAuth.setOnClickListener(this);
		turePassword = (EditText) findViewById(R.id.turePassword);
		turePassword.setOnFocusChangeListener(this);
		changeAuthimg = (ImageView) findViewById(R.id.changeAuthimg);
		changeAuthimg.setImageBitmap(Autjcode.getInstance().createBitmap());
		changeIdText = (TextView) findViewById(R.id.changeIdText);
		changePwText = (TextView) findViewById(R.id.changePwText);
		truePwText = (TextView) findViewById(R.id.turePwText);
		changeAuthText = (TextView) findViewById(R.id.changeAuthText);
        registerPwd = (EditText)findViewById(R.id.registerPassword);
		registerPwd.setOnClickListener(this);
		registerPwd.setOnFocusChangeListener(this);
		registerText = (TextView)findViewById(R.id.registerPwText);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.changeBack:
			ChangePasswordActivity.this.finish();
			break;
		case R.id.changeBackText:
			ChangePasswordActivity.this.finish();
			break;
		case R.id.changeAuth:
			changeAuth.setFocusable(true);
			changeAuth.setFocusableInTouchMode(true);
			changeAuth.requestFocus();
			changeAuth.findFocus();
			break;
		case R.id.changeCheck:
			changeAuthimg.setImageBitmap(Autjcode.getInstance()
					.createBitmap());
			break;
		case R.id.changeBtn:
		//	if(autecodeflag==1&&passwordflag==1&&phoneflag==1&&registerflag==1)
		//	{
				mDialog = new ProgressDialog(ChangePasswordActivity.this);
				mDialog.setTitle("登陆");
				mDialog.setMessage("正在登陆服务器，请稍后...");
				mDialog.show();
				Thread loginThread = new Thread(new changeThread());
				loginThread.start();
		//	}
			break;
		default:
			break;
		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		isPhone = changeId.getText().toString();
		register = registerPwd.getText().toString();
		isPassword = changePassword.getText().toString();
		isTruePassword = turePassword.getText().toString();
		Autecode = changeAuth.getText().toString();
		Autecodeimg = Autjcode.getInstance().getCode().toUpperCase();
		switch (v.getId()) {
		case R.id.changeId:
			if (hasFocus == false) {
				// 手机号码的正则判断
				Pattern pattern = Pattern.compile("^1[3,5,8]\\d{9}$");
				Matcher matcher = pattern.matcher(isPhone);
				if (matcher.find()) {
					changeIdText.setVisibility(View.INVISIBLE);
					phoneflag = 1;
				} else {
					if (changeId.length() != 0) {
						changeIdText.setVisibility(View.VISIBLE);
					}
				}
			}
			break;
			case R.id.registerPassword:
				if (hasFocus == false) {
					if ((register.length() < 6 || register.length() > 20)
							&& register.length() != 0) {
						changePwText.setVisibility(View.VISIBLE);
						registerflag=0;
					} else {
						changePwText.setVisibility(View.INVISIBLE);
						registerflag = 1;
					}
				}
				break;
		case R.id.changePassword:
			if (hasFocus == false) {
				if ((isPassword.length() < 6 || isPassword.length() > 20)
						&& isPassword.length() != 0) {
					changePwText.setVisibility(View.VISIBLE);
				} else {
					changePwText.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case R.id.turePassword:
			if (hasFocus == false) {
				if (isTruePassword.equals(isPassword)) {
					truePwText.setVisibility(View.INVISIBLE);
					passwordflag = 1;
				} else {
					if (turePassword.length() != 0) {
						truePwText.setVisibility(View.VISIBLE);
						passwordflag=0;
					}
				}
			}
			break;
		case R.id.changeAuth:
			if (hasFocus == false) {
				// 判断验证码是否正确，toUpperCase()是不区分大小写
				if (Autecode.toUpperCase().equals(Autecodeimg)) {
					changeAuthText.setVisibility(View.INVISIBLE);
					autecodeflag = 1;
				} else {
					if (changeAuth.length() != 0) {
						changeAuthText.setVisibility(View.VISIBLE);
						autecodeflag=0;
					}
				}
			}
			break;
		default:
			break;
		}
	}
	//初始化HttpClient，并设置超时
	public HttpClient getHttpClient()
	{
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	private boolean changeServer(String username, String oldpassword,String newpassword)
	{
		boolean loginValidate = false;
		//使用apache HTTP客户端实现
		String urlStr = "http://"+value+":8080/Servlet/ChangePasswordServlet";
		HttpPost request = new HttpPost(urlStr);
		//如果传递参数多的话，可以丢传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//添加用户名和密码
		params.add(new BasicNameValuePair("username",username));
		params.add(new BasicNameValuePair("oldpassword",oldpassword));
		params.add(new BasicNameValuePair("newpassword",newpassword));
		try
		{
			//设置请求参数项
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpClient client = getHttpClient();
			//执行请求返回相应
			HttpResponse response = client.execute(request);

			//判断是否请求成功
			if(response.getStatusLine().getStatusCode()==200)
			{
				loginValidate = true;
				//获得响应信息
				responseMsg = EntityUtils.toString(response.getEntity());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return loginValidate;
	}

	//Handler
	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case 0:
					mDialog.cancel();
					showDialog("修改成功！");
					break;
				case 1:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
					break;

			}

		}
	};
	//changeThread线程类
	class changeThread implements Runnable
	{

		@Override
		public void run() {
			String username = changeId.getText().toString();
			String oldpassword = registerPwd.getText().toString();
			String newpassword = changePassword.getText().toString();

			//URL合法，但是这一步并不验证密码是否正确
			boolean changeValidate = changeServer(username,oldpassword,newpassword);
			//System.out.println("----------------------------bool is :"+changeValidate+"----------response:"+responseMsg);
			Message msg = handler.obtainMessage();
			if(changeValidate)
			{
				if(responseMsg.equals("success"))
				{
					msg.what = 0;
					handler.sendMessage(msg);
				}else
				{
					msg.what = 1;
					handler.sendMessage(msg);
				}

			}else
			{
				msg.what = 2;
				handler.sendMessage(msg);
			}
		}

	}
	private void showDialog(String str)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("修改密码");
		builder.setMessage(str);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(ChangePasswordActivity.this, LoginActivity.class);
				Bundle bundle2 = new Bundle();
				bundle2.putString("key",value);
				intent.putExtras(bundle2);
				startActivity(intent);
				finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
