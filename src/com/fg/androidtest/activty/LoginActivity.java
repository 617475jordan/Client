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
	private static final int REQUEST_TIMEOUT = 5*1000;//��������ʱ10����
	private static final int SO_TIMEOUT = 10*1000;  //���õȴ����ݳ�ʱʱ��10����
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
	// �ؼ��ĳ�ʼ��
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
		//��ʼ������
		LoadUserdata();
	}

	// �ؼ��ĵ���¼�
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			String newusername = loginId.getText().toString();
			String newpassword = loginPassword.getText().toString();
				mDialog = new ProgressDialog(LoginActivity.this);
				mDialog.setTitle("��½");
				mDialog.setMessage("���ڵ�½�����������Ժ�...");
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
				//������ס����ѡ��
				SharedPreferences.Editor editor = sp.edit();
						if(saveInfoItem.isChecked())
						{
							//��ȡ�Ѿ����ڵ��û���������
							String realUsername = sp.getString("username", "");
							String realPassword = sp.getString("password", "");
							editor.putBoolean("checkstatus", true);
							editor.commit();

							if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
							{
								//��������
								loginId.setText("");
								loginPassword.setText("");
								//��������ֵ
								loginId.setText(realUsername);
								loginPassword.setText(realPassword);
							}
						}else {
							editor.putBoolean("checkstatus", false);
							editor.commit();
							//��������
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
					// �ֻ�����������ж�
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
		//ʹ��apache HTTP�ͻ���ʵ��
		String urlStr = "http://"+value+":8080/Servlet/LoginServlet";
		System.out.println(urlStr);
		HttpPost request = new HttpPost(urlStr);
		//������ݲ�����Ļ������Զ����ݵĲ������з�װ
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//����û���������
		params.add(new BasicNameValuePair("username",username));
		params.add(new BasicNameValuePair("password",password));
		try
		{
			//�������������
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpClient client = getHttpClient();
			//ִ�����󷵻���Ӧ
			HttpResponse response = client.execute(request);

			//�ж��Ƿ�����ɹ�
			if(response.getStatusLine().getStatusCode()==200)
			{
				loginValidate = true;
				//�����Ӧ��Ϣ
				responseMsg = EntityUtils.toString(response.getEntity());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return loginValidate;
	}
	//��ʼ��HttpClient�������ó�ʱ
	public HttpClient getHttpClient()
	{
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}
	//�ж��Ƿ��ס���룬Ĭ�ϼ�ס
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
	//��ʼ���û�����
	private void LoadUserdata()
	{
		boolean checkstatus = sp.getBoolean("checkstatus", false);
		if(checkstatus)
		{
			//saveInfoItem.setChecked(true);
			//�����û���Ϣ
			//��ȡ�Ѿ����ڵ��û���������
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
					Toast.makeText(getApplicationContext(), "��¼�ɹ���", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
					break;
				case 1:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "�û������������", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "URL��֤ʧ��", Toast.LENGTH_SHORT).show();
					break;
			}

		}
	};

	//LoginThread�߳���
	class LoginThread implements Runnable
	{

		@Override
		public void run() {
			String username = loginId.getText().toString();
			String password = loginPassword.getText().toString();
			boolean checkstatus = sp.getBoolean("checkstatus", false);
			if(checkstatus)
			{
				//��ȡ�Ѿ����ڵ��û���������
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

			//URL�Ϸ���������һ��������֤�����Ƿ���ȷ
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
		builder.setMessage("ȷ���˳�?");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
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
		builder.setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {
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
