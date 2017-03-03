package wificommunication_client;

import android.app.Activity;
import android.app.AlertDialog.Builder;
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
import com.fg.androidtest.activty.LoginActivity;
import java.io.IOException;



public class ClientActivity extends Activity implements OnClickListener{
	//��������
	private String serverIP = null;//������IP��ַ
	private int serverPort = 0;//������PORT
	private Thread mThreadClient = null;//����Socet���ӵ����߳�
	public static int serverNum = 0;
	public static boolean connectflag0=false,connectflag1=false;
	//����ؼ�
	private static Button connect1;
	private static Button connect2;
	private static Button login;
	private static TextView dialog_info;
	private EditText ipAddress1,ipAddress2;
	private static LinearLayout dialog;
	//���幫������
	public static boolean isConnecting = false;//����״̬��ʶλ
	public static String clientMessage = "";//�ͻ��˷��͵���Ϣ
	public static String serverMessage = "";//�ͻ��˽��յ���Ϣ
	public static String clientName = null;//�ͻ����û���
	public static String serverName = null;//������û���
	//��¼�ϴ������IP
	private SharedPreferences ipLogged_1 = null;
	private SharedPreferences ipLogged_2 = null;
	private SharedPreferences.Editor ipLogEditor_1 = null;
	private SharedPreferences.Editor ipLogEditor_2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//���ر���
        setContentView(R.layout.client);
        //�ϴ�IP��¼��ʼ��
        ipLogged_1 = getSharedPreferences("lastIP_1",MODE_PRIVATE);
        ipLogEditor_1 = ipLogged_1.edit();
        ipLogged_2 = getSharedPreferences("lastIP_2",MODE_PRIVATE);
        ipLogEditor_2 = ipLogged_2.edit();
        //��ȡ�ؼ�
        connect1 = (Button)findViewById(R.id.connect1);
        connect2 = (Button)findViewById(R.id.connect2);
		login = (Button)findViewById(R.id.login);
        dialog_info = (TextView)findViewById(R.id.dialog_info);
        ipAddress1 = (EditText)findViewById(R.id.ipAddress1);
        ipAddress2 = (EditText)findViewById(R.id.ipAddress2);
        dialog = (LinearLayout)findViewById(R.id.dialog);
        //�ؼ�����
        connect1.setOnClickListener(this);
        connect2.setOnClickListener(this);
		login.setOnClickListener(this);

        //��ȡ�û���������

    }
    //��������
    private void connectServer(String IP,int Port,int Num){
    	serverNum = Num;
    	if(isConnecting){
    		SendMessage.sendMessage("Reboot\r\n", ClientActivity.this);			
    		isConnecting = false; 		
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
			mThreadClient.interrupt();	
			if(serverNum == 1){
				connect1.setText("����");
				connect1.setBackgroundResource(R.drawable.connect1);
				connectflag0 = false;
			}else if(serverNum == 2){
				connect2.setText("����");
				connect2.setBackgroundResource(R.drawable.connect1);
				connectflag1 = false;
			}
			dialog_info.setText("��Ϣ:\n");
			serverNum = 0;
    	}else{  		
    		MyThread myThread = new MyThread(IP,Port,ClientActivity.this);
    		mThreadClient = new Thread(myThread);
    		mThreadClient.start();	   			  				  		  		
    	}
    }
    //���շ�����Ϣ��ʾ���ı�����
    public static Handler messageHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  if(msg.what == 1){
				  if(clientMessage.equals("Server online")){
				  }else{
					  dialog_info.append(clientName + ": "+clientMessage + "\n");	// ˢ��
				  }
			  }else if(msg.what == 0){
				  dialog_info.append(serverName + ": " + serverMessage + "\n");	// ˢ��
			  }
		  }									
	 };
	 //�����Ƿ����ӳɹ������İ�������ʾ״̬
	 public static Handler connectSuccess = new Handler(){
		 
		public void handleMessage(Message msg){
			 super.handleMessage(msg);
			 if(msg.what == 0){
				 if(serverNum == 1){
	    				connect1.setText("�Ͽ�");
	    				connect1.setBackgroundResource(R.drawable.connect2);
					    connectflag0 = true;
	    			}else if(serverNum == 2){
	    				connect2.setText("�Ͽ�");
	    				connect2.setBackgroundResource(R.drawable.connect2);
					    connectflag1 = true;
	    			} 
			  }else if(msg.what == 1){
				  if(serverNum == 1){
	    				connect1.setText("����");
	    				connect1.setBackgroundResource(R.drawable.connect1);
					    connectflag0 = false;
				  }else if(serverNum == 2){
	    				connect2.setText("����");
	    				connect1.setBackgroundResource(R.drawable.connect1);
					    connectflag1 = false;
				  }
				  serverNum = 0;
			  }
		  }	
	 };
	 //��������
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.connect1:
			if(serverNum != 2){
				if(ipAddress1.getText().toString().equals("")){
					Toast.makeText(this, "IP��ַΪ��,����ʹ���ϴ�IP", Toast.LENGTH_SHORT).show();
					serverIP = ipLogged_1.getString("ip", "none");
					ipAddress1.setText(serverIP);
				}else{
					serverIP = ipAddress1.getText().toString();
					ipLogEditor_1.clear().commit();
					ipLogEditor_1.putString("ip", serverIP);
					ipLogEditor_1.commit();
					connectServer(serverIP ,serverPort,1);
				}
				serverPort = 8080;

			}else{
				Toast.makeText(this, "�Ѵ������ӣ���Ͽ��������Ӻ������³���", Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.connect2:
			if(serverNum != 1){
				if(ipAddress2.getText().toString().equals("")){
					Toast.makeText(this, "IP��ַΪ��,����ʹ���ϴ�IP", Toast.LENGTH_SHORT).show();
					serverIP = ipLogged_2.getString("ip", "none");
					ipAddress2.setText(serverIP);
				}else{
					serverIP = ipAddress2.getText().toString();
					ipLogEditor_2.clear().commit();
					ipLogEditor_2.putString("ip", serverIP);
					ipLogEditor_2.commit();
				}
				serverIP = ipAddress2.getText().toString();
				serverPort = 8080;
				connectServer(serverIP ,serverPort,2);
			}else{
				Toast.makeText(this, "�Ѵ������ӣ���Ͽ��������Ӻ������³���", Toast.LENGTH_LONG).show();
			}
			
			break;
			case R.id.login:
                if(connectflag0==true||connectflag1==true)
				{
					Intent n = new Intent(ClientActivity.this, LoginActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("key",serverIP);
					n.putExtras(bundle);
					startActivity(n);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "����δ���ӳɹ���", Toast.LENGTH_SHORT).show();
				}
		default:
			break;
		}
	}
	//�Ի��ı����Զ�������������Ϣ
/*	public static void scroll2Bottom(final ScrollView scroll, final View inner) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (scroll == null || inner == null) {
					return;
				}
				// �ڲ�߶ȳ������
				int offset = inner.getMeasuredHeight()
						- scroll.getMeasuredHeight();
				if (offset < 0) {
					System.out.println("��λ...");
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}
*/
	protected void dialog() {
	    Builder builder = new Builder(ClientActivity.this);
	    builder.setMessage("ȷ���˳�?"); 
	    builder.setTitle("��ʾ"); 
	    builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() { 
	                @Override
	                public void onClick(DialogInterface dialog, int which) { 
	                        dialog.dismiss(); 
	                    	SendMessage.sendMessage("Reboot\r\n", ClientActivity.this);	
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
	                		if(mThreadClient != null){
	                			mThreadClient.interrupt();	
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
