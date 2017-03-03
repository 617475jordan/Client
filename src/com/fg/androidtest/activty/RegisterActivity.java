package com.fg.androidtest.activty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

public class RegisterActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private Button registerBack;
	private Button registerCheck;
	private Button registerBtn;
	private EditText registerId;
	private EditText registerPassword;
	private EditText registerAuth;
	private EditText turePassword;
	private TextView registerBackText;
	private TextView registerIdText;
	private TextView registerPwText;
	private TextView turePwText;
	private TextView registerAuthText;
	private ImageView registerAuthimg;
	private String isPhone, isPassword, isTruePassword, Autecode, Autecodeimg;
	private ProgressDialog mDialog;
	private String responseMsg = "";
	private static final int REQUEST_TIMEOUT = 5 * 1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000;  //设置等待数据超时时间10秒钟
	private static final int LOGIN_OK = 1;
    private String value;
	private Boolean phoneFlag,passwordFlag,authFlag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		initView();
		value = IPUtils.getServerIpAddress(RegisterActivity.this);
	}

	private void initView() {
		registerBack = (Button) findViewById(R.id.registerBack);
		registerBack.setOnClickListener(this);
		registerCheck = (Button) findViewById(R.id.registerCheck);
		registerCheck.setOnClickListener(this);
		registerBtn = (Button) findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(this);
		registerBackText = (TextView) findViewById(R.id.registerBackText);
		registerBackText.setOnClickListener(this);

		registerId = (EditText) findViewById(R.id.registerId);
		registerId.setOnFocusChangeListener(this);
		registerPassword = (EditText) findViewById(R.id.registerPassword);
		registerPassword.setOnFocusChangeListener(this);
		registerAuth = (EditText) findViewById(R.id.registerAuth);
		registerAuth.setOnFocusChangeListener(this);
		registerAuth.setOnClickListener(this);
		turePassword = (EditText) findViewById(R.id.turePassword);
		turePassword.setOnFocusChangeListener(this);

		registerAuthimg = (ImageView) findViewById(R.id.registerAuthimg);
		registerAuthimg.setImageBitmap(Autjcode.getInstance().createBitmap());

		registerIdText = (TextView) findViewById(R.id.registerIdText);
		registerPwText = (TextView) findViewById(R.id.registerPwText);
		turePwText = (TextView) findViewById(R.id.turePwText);
		registerAuthText = (TextView) findViewById(R.id.registerAuthText);
        authFlag=false;
		passwordFlag=false;
		phoneFlag=false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registerBack:
			RegisterActivity.this.finish();
			break;
		case R.id.registerBackText:
			RegisterActivity.this.finish();
			break;
		case R.id.registerAuth:
			registerAuth.setFocusable(true);
			registerAuth.setFocusableInTouchMode(true);
			registerAuth.requestFocus();
			registerAuth.findFocus();
			break;
		case R.id.registerCheck:
			registerAuthimg.setImageBitmap(Autjcode.getInstance()
					.createBitmap());
			break;
		case R.id.registerBtn:
			String newusername = registerId.getText().toString();
			String newpassword = registerPassword.getText().toString();
			String confirmpwd = turePassword.getText().toString();
			if (newpassword.equals(confirmpwd)&&phoneFlag==true&&passwordFlag==true&&authFlag==true) {
					SharedPreferences sp = getSharedPreferences("userdata", 0);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("username", newusername);
					editor.putString("password", newpassword);
					editor.commit();
					mDialog = new ProgressDialog(RegisterActivity.this);
					mDialog.setTitle("登陆");
					mDialog.setMessage("正在登陆服务器，请稍后...");
					mDialog.show();
					Thread loginThread = new Thread(new RegisterThread());
					loginThread.start();

				} else {
					Toast.makeText(getApplicationContext(), "您两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
				  break;
				}
			break;
		default:
			break;
		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		isPhone = registerId.getText().toString();
		isPassword = registerPassword.getText().toString();
		isTruePassword = turePassword.getText().toString();
		Autecode = registerAuth.getText().toString();
		Autecodeimg = Autjcode.getInstance().getCode().toUpperCase();
		switch (v.getId()) {
		case R.id.registerId:
			if (hasFocus == false) {
				// 手机号码的正则判断
				Pattern pattern = Pattern.compile("^1[3,5,8]\\d{9}$");
				Matcher matcher = pattern.matcher(isPhone);
				if (matcher.find()) {
					registerIdText.setVisibility(View.INVISIBLE);
					phoneFlag=true;
				} else {
					if (registerId.length() != 0) {
						registerIdText.setVisibility(View.VISIBLE);
						phoneFlag=false;
					}
				}
			}
			break;
		case R.id.registerPassword:
			if (hasFocus == false) {
				if ((isPassword.length() < 6 || isPassword.length() > 20)
						&& isPassword.length() != 0) {
					registerPwText.setVisibility(View.VISIBLE);
				} else {
					registerPwText.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case R.id.turePassword:
			if (hasFocus == false) {
				if (isTruePassword.equals(isPassword)) {
					turePwText.setVisibility(View.INVISIBLE);
					passwordFlag=true;
				} else {
					if (turePassword.length() != 0) {
						turePwText.setVisibility(View.VISIBLE);
						passwordFlag=false;
					}
				}
			}
			break;
		case R.id.registerAuth:
			if (hasFocus == false) {
				// 判断验证码是否正确，toUpperCase()是不区分大小写
				if (Autecode.toUpperCase().equals(Autecodeimg)) {
					registerAuthText.setVisibility(View.INVISIBLE);
					authFlag=true;
				} else {
					if (registerAuth.length() != 0) {
						registerAuthText.setVisibility(View.VISIBLE);
						authFlag=false;
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

	private boolean registerServer(String username, String password)
	{
		boolean loginValidate = false;
		//使用apache HTTP客户端实现
	//	data ip = new data();
	//	String Ip = ip.reader();
		String urlStr = "http://"+value+":8080/Servlet/RegisterServlet";
		HttpPost request = new HttpPost(urlStr);
		//如果传递参数多的话，可以丢传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//添加用户名和密码
		params.add(new BasicNameValuePair("username",username));
		params.add(new BasicNameValuePair("password",password));
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
					showDialog("注册成功！");
					break;
				case 1:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "注册失败，用户已存在", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
					break;

			}

		}
	};
	//RegisterThread线程类
	class RegisterThread implements Runnable
	{

		@Override
		public void run() {
			String username = registerId.getText().toString();
			String password = registerPassword.getText().toString();

			//URL合法，但是这一步并不验证密码是否正确
			boolean registerValidate = registerServer(username, password);
			//System.out.println("----------------------------bool is :"+registerValidate+"----------response:"+responseMsg);
			Message msg = handler.obtainMessage();
			if(registerValidate)
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
		builder.setTitle("注册");
		builder.setMessage(str);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
