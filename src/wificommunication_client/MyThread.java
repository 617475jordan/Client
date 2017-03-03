package wificommunication_client;

import android.content.Context;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyThread implements Runnable{

	public static Socket mSocketClient = null;//�ͻ���Socket	
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
			//���ӷ�����
			mSocketClient = new Socket(IP, Port);
			//ȡ�����롢�����
			ReceiveMessage.bufferFromServer = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));//��ȡ����˷�������Ϣ				
			SendMessage.bufferToServer = new PrintWriter(mSocketClient.getOutputStream(), true);//������Ϣ�������
			ClientActivity.isConnecting = true;
			SendMessage.sendMessage("clientName" + ClientActivity.clientName, context);//���ӳɹ��󽫱����û������͸������
			//�����Ƿ����ӳɹ������İ�������ʾ״̬
			Message connectSuccess = new Message();			
			connectSuccess.what = 0;		
			ClientActivity.connectSuccess.sendMessage(connectSuccess);           
		}catch(Exception e){			
			ClientActivity.isConnecting = false;
			//�����Ƿ����ӳɹ������İ�������ʾ״̬
			Message connectSuccess = new Message();
			connectSuccess.what = 1;
			ClientActivity.connectSuccess.sendMessage(connectSuccess);
			//���շ�����Ϣ��ʾ���ı�����
			ClientActivity.clientMessage = "����IP�쳣:" + e.toString() + e.getMessage();//��Ϣ����
			Message connectFailmsg = new Message();
			connectFailmsg.what = 1;
            ClientActivity.messageHandler.sendMessage(connectFailmsg);
			return;
		}		
		ReceiveMessage.receiveMessage();//������Ϣ
	}   	
}
