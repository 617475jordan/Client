package wificommunication_client;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.Socket;

public class SendMessage {
	public static PrintWriter bufferToServer = null;	
	private static String msgHead;//ժȡ�����ͷ
	public static void sendMessage(String message,Context context){
    	if (ClientActivity.isConnecting && MyThread.mSocketClient!=null) 
		{   		
			try {				    	
				bufferToServer.print(message+"\r\n");//���͸�������
				bufferToServer.flush();
				int msgLength = message.length();
				if(msgLength > 9){
					msgHead = message.substring(0,10);
				}
				//���������ͻ����û������������ͨ�Ŵ�����ʾ
				if(!(message.equals("Reboot"))&&!(msgHead.equals("clientName")&&msgLength>8)){
					ClientActivity.clientMessage = message;//��Ϣ����
					Message msg = new Message();
					msg.what = 1;
					ClientActivity.messageHandler.sendMessage(msg);
				}					
			}catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(context, "�����쳣��" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	//�жϿͻ����Ƿ�����
	public static boolean isConnected(Socket socket){
        try{
        	socket.sendUrgentData(0xFF);
            return true;
        }catch(Exception e){
            return false;
        }
	}
}
