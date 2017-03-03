package wificommunication_client;

import android.os.Message;

import java.io.BufferedReader;

public class ReceiveMessage {
	
	private static String content;
	public static BufferedReader bufferFromServer = null;
	private static String msgHead =  "";//ƥ����Ϣͷ��
	public ReceiveMessage(){
		
	}
	public static void receiveMessage() {
		// TODO Auto-generated method stub
		while (ClientActivity.isConnecting){
			try{
				if((content = bufferFromServer.readLine())!= null){
					ClientActivity.serverMessage = content;
					int msgLength = content.length();//��ȡ�������Ϣ�ĳ���
					if(msgLength > 9){
						msgHead = content.substring(0, 10);//ժȡ��Ϣͷ��
					}
					if(msgHead.equals("serverName")&&msgLength >8){
						//������յ����Է�����û�������Ϣ�����¿ͻ��˵Ľ���
						ClientActivity.serverName = content.substring(10);//ժȡ������û���
						//֪ͨ���̸߳���UI����������û�����ʾ����
						ClientActivity.clientMessage = "Server online";			
						Message connectSuccessmsg = new Message();
						connectSuccessmsg.what = 1;
				        ClientActivity.messageHandler.sendMessage(connectSuccessmsg);
					}else{//�û�ͨ����Ϣ�Żᱻ��ʾ
						Message serverMessageReceive= new Message();
						serverMessageReceive.what = 0;
						ClientActivity.messageHandler.sendMessage(serverMessageReceive);
					}	
				}
			}catch(Exception e){
				ClientActivity.clientMessage = "�����쳣:" + e.getMessage();//��Ϣ����
				Message serverMessageReceive = new Message();
				serverMessageReceive.what = 1;
				ClientActivity.messageHandler.sendMessage(serverMessageReceive);
			}
		}
	}
}
