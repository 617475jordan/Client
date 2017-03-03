package wificommunication_client;

import android.content.Context;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyThread implements Runnable{

	public static Socket mSocketClient = null;//客户端Socket	
	private String IP;
	private int Port;
	private Context context;
	public MyThread(String IP,int Port,Context context){
		this.IP = IP;
		this.Port = Port;
		this.context = context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			//连接服务器
			mSocketClient = new Socket(IP, Port);
			//取得输入、输出流
			ReceiveMessage.bufferFromServer = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));//读取服务端发来的消息				
			SendMessage.bufferToServer = new PrintWriter(mSocketClient.getOutputStream(), true);//发送消息给服务端
			ClientActivity.isConnecting = true;
			SendMessage.sendMessage("clientName" + ClientActivity.clientName, context);//连接成功后将本机用户名发送给服务端
			//根据是否连接成功来更改按键的显示状态
			Message connectSuccess = new Message();			
			connectSuccess.what = 0;		
			ClientActivity.connectSuccess.sendMessage(connectSuccess);           
		}catch(Exception e){			
			ClientActivity.isConnecting = false;
			//根据是否连接成功来更改按键的显示状态
			Message connectSuccess = new Message();
			connectSuccess.what = 1;
			ClientActivity.connectSuccess.sendMessage(connectSuccess);
			//将收发的信息显示到文本框中
			ClientActivity.clientMessage = "连接IP异常:" + e.toString() + e.getMessage();//消息换行
			Message connectFailmsg = new Message();
			connectFailmsg.what = 1;
            ClientActivity.messageHandler.sendMessage(connectFailmsg);
			return;
		}		
		ReceiveMessage.receiveMessage();//接收消息
	}   	
}
