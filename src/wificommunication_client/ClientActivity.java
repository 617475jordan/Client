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
	//定义属性
	private String serverIP = null;//服务器IP地址
	private int serverPort = 0;//服务器PORT
	private Thread mThreadClient = null;//创建Socet连接的子线程
	public static int serverNum = 0;
	public static boolean connectflag0=false,connectflag1=false;
	//定义控件
	private static Button connect1;
	private static Button connect2;
	private static Button login;
	private static TextView dialog_info;
	private EditText ipAddress1,ipAddress2;
	private static LinearLayout dialog;
	//定义公共属性
	public static boolean isConnecting = false;//连接状态标识位
	public static String clientMessage = "";//客户端发送的消息
	public static String serverMessage = "";//客户端接收的消息
	public static String clientName = null;//客户端用户名
	public static String serverName = null;//服务端用户名
	//记录上次输入的IP
	private SharedPreferences ipLogged_1 = null;
	private SharedPreferences ipLogged_2 = null;
	private SharedPreferences.Editor ipLogEditor_1 = null;
	private SharedPreferences.Editor ipLogEditor_2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        setContentView(R.layout.client);
        //上次IP记录初始化
        ipLogged_1 = getSharedPreferences("lastIP_1",MODE_PRIVATE);
        ipLogEditor_1 = ipLogged_1.edit();
        ipLogged_2 = getSharedPreferences("lastIP_2",MODE_PRIVATE);
        ipLogEditor_2 = ipLogged_2.edit();
        //获取控件
        connect1 = (Button)findViewById(R.id.connect1);
        connect2 = (Button)findViewById(R.id.connect2);
		login = (Button)findViewById(R.id.login);
        dialog_info = (TextView)findViewById(R.id.dialog_info);
        ipAddress1 = (EditText)findViewById(R.id.ipAddress1);
        ipAddress2 = (EditText)findViewById(R.id.ipAddress2);
        dialog = (LinearLayout)findViewById(R.id.dialog);
        //控件监听
        connect1.setOnClickListener(this);
        connect2.setOnClickListener(this);
		login.setOnClickListener(this);

        //获取用户名及密码

    }
    //创建链接
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
				connect1.setText("连接");
				connect1.setBackgroundResource(R.drawable.connect1);
				connectflag0 = false;
			}else if(serverNum == 2){
				connect2.setText("连接");
				connect2.setBackgroundResource(R.drawable.connect1);
				connectflag1 = false;
			}
			dialog_info.setText("信息:\n");
			serverNum = 0;
    	}else{  		
    		MyThread myThread = new MyThread(IP,Port,ClientActivity.this);
    		mThreadClient = new Thread(myThread);
    		mThreadClient.start();	   			  				  		  		
    	}
    }
    //将收发的信息显示到文本框中
    public static Handler messageHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  if(msg.what == 1){
				  if(clientMessage.equals("Server online")){
				  }else{
					  dialog_info.append(clientName + ": "+clientMessage + "\n");	// 刷新
				  }
			  }else if(msg.what == 0){
				  dialog_info.append(serverName + ": " + serverMessage + "\n");	// 刷新
			  }
		  }									
	 };
	 //根据是否连接成功来更改按键的显示状态
	 public static Handler connectSuccess = new Handler(){
		 
		public void handleMessage(Message msg){
			 super.handleMessage(msg);
			 if(msg.what == 0){
				 if(serverNum == 1){
	    				connect1.setText("断开");
	    				connect1.setBackgroundResource(R.drawable.connect2);
					    connectflag0 = true;
	    			}else if(serverNum == 2){
	    				connect2.setText("断开");
	    				connect2.setBackgroundResource(R.drawable.connect2);
					    connectflag1 = true;
	    			} 
			  }else if(msg.what == 1){
				  if(serverNum == 1){
	    				connect1.setText("连接");
	    				connect1.setBackgroundResource(R.drawable.connect1);
					    connectflag0 = false;
				  }else if(serverNum == 2){
	    				connect2.setText("连接");
	    				connect1.setBackgroundResource(R.drawable.connect1);
					    connectflag1 = false;
				  }
				  serverNum = 0;
			  }
		  }	
	 };
	 //按键监听
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.connect1:
			if(serverNum != 2){
				if(ipAddress1.getText().toString().equals("")){
					Toast.makeText(this, "IP地址为空,程序将使用上次IP", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "已存在连接，请断开已有连接后再重新尝试", Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.connect2:
			if(serverNum != 1){
				if(ipAddress2.getText().toString().equals("")){
					Toast.makeText(this, "IP地址为空,程序将使用上次IP", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "已存在连接，请断开已有连接后再重新尝试", Toast.LENGTH_LONG).show();
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
					Toast.makeText(getApplicationContext(), "网络未连接成功！", Toast.LENGTH_SHORT).show();
				}
		default:
			break;
		}
	}
	//对话文本框自动滚动到最新信息
/*	public static void scroll2Bottom(final ScrollView scroll, final View inner) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (scroll == null || inner == null) {
					return;
				}
				// 内层高度超过外层
				int offset = inner.getMeasuredHeight()
						- scroll.getMeasuredHeight();
				if (offset < 0) {
					System.out.println("定位...");
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}
*/
	protected void dialog() {
	    Builder builder = new Builder(ClientActivity.this);
	    builder.setMessage("确定退出?"); 
	    builder.setTitle("提示"); 
	    builder.setPositiveButton("确认",new DialogInterface.OnClickListener() { 
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
