package wificommunication_client;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.Socket;

public class SendMessage {
	public static PrintWriter bufferToServer = null;	
	private static String msgHead;//摘取命令标头
	public static void sendMessage(String message,Context context){
    	if (ClientActivity.isConnecting && MyThread.mSocketClient!=null) 
		{   		
			try {				    	
				bufferToServer.print(message+"\r\n");//发送给服务器
				bufferToServer.flush();
				int msgLength = message.length();
				if(msgLength > 9){
					msgHead = message.substring(0,10);
				}
				//“重启、客户端用户名”等命令不在通信窗口显示
				if(!(message.equals("Reboot"))&&!(msgHead.equals("clientName")&&msgLength>8)){
					ClientActivity.clientMessage = message;//消息换行
					Message msg = new Message();
					msg.what = 1;
					ClientActivity.messageHandler.sendMessage(msg);
				}					
			}catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(context, "发送异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	//判断客户端是否在线
	public static boolean isConnected(Socket socket){
        try{
        	socket.sendUrgentData(0xFF);
            return true;
        }catch(Exception e){
            return false;
        }
	}
}
