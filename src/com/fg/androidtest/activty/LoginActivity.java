package com.fg.androidtest.activty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import slidingmenu.MainActivity;
import wificommunication_client.ClientActivity;
import wificommunication_client.MyThread;
import wificommunication_client.SendMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements OnClickListener,View.OnFocusChangeListener {

	private EditText loginId;
	private EditText loginPassword;
	private Button loginBtn;
	private Button loginMissps;
	private Button loginNewUser;
	private Button loginChangePw;
   // private Button backclient;
	//private TextView backclientText;
	private ProgressDialog mDialog;
	private String responseMsg = "";
	private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
	private static final int LOGIN_OK = 1;
	private SharedPreferences sp;
	private CheckBox saveInfoItem;
	private  String value;
	private String isPhone, isPassword;
	private Boolean phoneFlag,passwordFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initView();
		Intent i = super.getIntent();
		String Id = i.getStringExtra("myId");
		loginId.setText(Id);
        value = IPUtils.getServerIpAddress(LoginActivity.this);
	}
	// 控件的初始化
	private void initView() {
		loginId = (EditText) findViewById(R.id.loginId);
		loginPassword = (EditText) findViewById(R.id.loginPassword);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		loginMissps = (Button) findViewById(R.id.loginMissps);
		loginMissps.setOnClickListener(this);
		loginNewUser = (Button) findViewById(R.id.loginNewUser);
		loginNewUser.setOnClickListener(this);
		loginChangePw = (Button) findViewById(R.id.loginChangePw);
		loginChangePw.setOnClickListener(this);
		sp = getSharedPreferences("userdata",0);
		saveInfoItem = (CheckBox)findViewById(R.id.saveInfoItem);
		saveInfoItem.setOnClickListener(this);
	/*	backclient = (Button)findViewById(R.id.backclient);
		backclient.setOnClickListener(this);
		backclientText = (TextView) findViewById(R.id.backclientText);
		backclientText.setOnClickListener(this);*/
		//初始化数据
		LoadUserdata();
	}

	// 控件的点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			String newusername = loginId.getText().toString();
			String newpassword = loginPassword.getText().toString();
				mDialog = new ProgressDialog(LoginActivity.this);
				mDialog.setTitle("登陆");
				mDialog.setMessage("正在登陆服务器，请稍后...");
				mDialog.show();
				Thread loginThread = new Thread(new LoginThread());
				loginThread.start();

			break;
		case R.id.loginMissps:
			Intent a = new Intent(LoginActivity.this,
					FindPasswordActivity.class);
			startActivity(a);
			break;
		case R.id.loginNewUser:
			Intent i = new Intent(LoginActivity.this, RegisterActivity.class);

			startActivity(i);
			break;
		case R.id.loginChangePw:
			Intent l = new Intent(LoginActivity.this,
					ChangePasswordActivity.class);
			startActivity(l);
			break;
			case R.id.saveInfoItem:
				//监听记住密码选项
				SharedPreferences.Editor editor = sp.edit();
						if(saveInfoItem.isChecked())
						{
							//获取已经存在的用户名和密码
							String realUsername = sp.getString("username", "");
							String realPassword = sp.getString("password", "");
							editor.putBoolean("checkstatus", true);
							editor.commit();

							if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
							{
								//清空输入框
								loginId.setText("");
								loginPassword.setText("");
								//设置已有值
								loginId.setText(realUsername);
								loginPassword.setText(realPassword);
							}
						}else {
							editor.putBoolean("checkstatus", false);
							editor.commit();
							//清空输入框
							loginId.setText("");
							loginPassword.setText("");
						}
				break;
		/*	case R.id.backclient:
				Intent backClient = new Intent(LoginActivity.this,
						ClientActivity.class);
				startActivity(backClient);
			case R.id.backclientText:
				Intent backClientText = new Intent(LoginActivity.this,
						ClientActivity.class);
				startActivity(backClientText);*/
		default:
			break;
		}
	}
	public void onFocusChange(View v, boolean hasFocus) {
		isPhone = loginId.getText().toString();
		isPassword = loginPassword.getText().toString();
		switch (v.getId()) {
			case R.id.loginId:
				if (hasFocus == false) {
					// 手机号码的正则判断
					Pattern pattern = Pattern.compile("^1[3,5,8]\\d{9}$");
					Matcher matcher = pattern.matcher(isPhone);
					if (matcher.find()) {
						phoneFlag=true;
					} else {
						if (loginId.length() != 0) {
							phoneFlag=false;
						}
					}
				}
				break;
			case R.id.loginPassword:
				if (hasFocus == false) {
					if ((isPassword.length() < 6 || isPassword.length() > 20)
							&& isPassword.length() != 0) {
						  passwordFlag=true;
					} else {
						passwordFlag=false;
					}
				}
				break;
			default:
				break;
		}
	}
	private boolean loginServer(String username, String password)
	{
		boolean loginValidate = false;
		//使用apache HTTP客户端实现
		String urlStr = "http://"+value+":8080/Servlet/LoginServlet";
		System.out.println(urlStr);
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
	//初始化HttpClient，并设置超时
	public HttpClient getHttpClient()
	{
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}
	//判断是否记住密码，默认记住
	private boolean isRemembered() {
		try {
			if (saveInfoItem.isChecked()) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//初始化用户数据
	private void LoadUserdata()
	{
		boolean checkstatus = sp.getBoolean("checkstatus", false);
		if(checkstatus)
		{
			//saveInfoItem.setChecked(true);
			//载入用户信息
			//获取已经存在的用户名和密码
			String realUsername = sp.getString("username", "");
			String realPassword = sp.getString("password", "");
			if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
			{
				loginId.setText("");
				loginPassword.setText("");
				loginId.setText(realUsername);
				loginPassword.setText(realPassword);
			}
		}else
		{
			//saveInfoItem.setChecked(false);
			loginId.setText("");
			loginPassword.setText("");
		}

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
					Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
					break;
				case 1:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
					break;
			}

		}
	};

	//LoginThread线程类
	class LoginThread implements Runnable
	{

		@Override
		public void run() {
			String username = loginId.getText().toString();
			String password = loginPassword.getText().toString();
			boolean checkstatus = sp.getBoolean("checkstatus", false);
			if(checkstatus)
			{
				//获取已经存在的用户名和密码
				String realUsername = sp.getString("username", "");
				String realPassword = sp.getString("password", "");
				if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
				{
					if(username.equals(realUsername)&&password.equals(realPassword))
					{
						username = loginId.getText().toString();
						password = loginPassword.getText().toString();
					}
				}
			}
			System.out.println("username="+username+":password="+password);

			//URL合法，但是这一步并不验证密码是否正确
			boolean loginValidate = loginServer(username, password);
			System.out.println("----------------------------bool is :"+loginValidate+"----------response:"+responseMsg);
			Message msg = handler.obtainMessage();
			if(loginValidate)
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
	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setMessage("确定退出?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SendMessage.sendMessage("Reboot\r\n", LoginActivity.this);
				try {
					if(MyThread.mSocketClient!=null){
						MyThread.mSocketClient.close();
						MyThread.mSocketClient = null;
						SendMessage.bufferToServer.close();
						SendMessage.bufferToServer = null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent=new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				System.exit(0);
			}
		});
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return false;
		}
		return false;
	}
}
