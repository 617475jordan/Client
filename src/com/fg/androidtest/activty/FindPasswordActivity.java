package com.fg.androidtest.activty;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.androidtest.R;
import com.fg.androidtest.utils.Autjcode;
import com.fg.androidtext.sqlite.MyloginCursor;


public class FindPasswordActivity extends Activity implements OnClickListener {
	private Button findPasswordBack;
	private Button findPasswordCheck;
	private Button findPasswordBtn;
	private EditText findPasswId;
	private EditText findPasswAuth;
	private ImageView findPasswordimg;
	private TextView findPasswordText;
	private TextView findPasswAuthText;
	private TextView findPasswIdText;
	private String Autecode, Autecodeimg;
	private String Id;
	private int myflagId, myflagAutn;
	private SQLiteOpenHelper helper;
	private String value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_findpassword);
		initView();
		value = IPUtils.getServerIpAddress(FindPasswordActivity.this);
	}
	/**
	 * �ؼ��ĳ�ʼ��
	 */
	private void initView() {
		findPasswordBack = (Button) findViewById(R.id.findPasswordBack);
		findPasswordBack.setOnClickListener(this);
		findPasswordCheck = (Button) findViewById(R.id.findPasswordCheck);
		findPasswordCheck.setOnClickListener(this);
		findPasswordBtn = (Button) findViewById(R.id.findPasswordBtn);
		findPasswordBtn.setOnClickListener(this);
		findPasswordText = (TextView) findViewById(R.id.findPasswordText);
		findPasswordText.setOnClickListener(this);
		findPasswIdText = (TextView) findViewById(R.id.findPasswIdText);
		findPasswAuthText = (TextView) findViewById(R.id.findPasswAuthText);
		findPasswId = (EditText) findViewById(R.id.findPasswId);
		findPasswId.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Id = findPasswId.getText().toString();
				if (hasFocus == false) {
					if (new MyloginCursor(FindPasswordActivity.this.helper
							.getReadableDatabase()).find(Id).size() == 0
							&& findPasswId.length() != 0) {
						// �ؼ��Ŀɼ���
						findPasswIdText.setVisibility(View.VISIBLE);
					} else {
						findPasswIdText.setVisibility(View.INVISIBLE);
						myflagId = 1;
					}
				}
			}
		});
		findPasswAuth = (EditText) findViewById(R.id.findPasswAuth);
		// Edittext�Ľ����¼��ļ���
		findPasswAuth.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == false) {
					Autecode = findPasswAuth.getText().toString();
					Autecodeimg = Autjcode.getInstance().getCode()
							.toUpperCase();
					if (Autecode.toUpperCase().equals(Autecodeimg)) {
						findPasswAuthText.setVisibility(View.INVISIBLE);
						myflagAutn = 1;
					} else {
						if (findPasswAuth.length() != 0) {
							findPasswAuthText.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		});
		findPasswAuth.setOnClickListener(this);
		findPasswordimg = (ImageView) findViewById(R.id.findPasswordimg);
		// ������֤��
		findPasswordimg.setImageBitmap(Autjcode.getInstance().createBitmap());
	}

	/**
	 * �ؼ��ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.findPasswordBack:
			FindPasswordActivity.this.finish();
			break;
		case R.id.findPasswordText:
			FindPasswordActivity.this.finish();
			break;
		case R.id.findPasswordCheck:
			findPasswordimg.setImageBitmap(Autjcode.getInstance()
					.createBitmap());
			break;
		case R.id.findPasswAuth:
			// EditText���»�ȡ����
			findPasswAuth.setFocusable(true);
			findPasswAuth.setFocusableInTouchMode(true);
			findPasswAuth.requestFocus();
			findPasswAuth.findFocus();
			break;
		case R.id.findPasswordBtn:
			findPasswAuth.setFocusable(false);
			if (myflagId == 1 && myflagAutn == 1) {
				Id = findPasswId.getText().toString();
				String phonecon = new MyloginCursor(
						FindPasswordActivity.this.helper.getReadableDatabase())
						.find(Id).toString();
				String content[] = phonecon.split(",");
				String phonenum = findPasswId.getText().toString().trim();
				// ���Ͷ���
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phonenum, null, "�����˺�����Ϊ"
						+ content[2], null, null);
			}
			break;
		default:
			break;
		}

	}
}
