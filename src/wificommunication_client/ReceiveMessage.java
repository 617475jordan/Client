package wificommunication_client;

import android.os.Message;

import java.io.BufferedReader;

public class ReceiveMessage {
	
	private static String content;
	public static BufferedReader bufferFromServer = null;
	private static String msgHead =  "";//匹配信息头标
	public ReceiveMessage(){
		
	}
	public static void receiveMessage() {
		// TODO Auto-generated method stub
		while (ClientActivity.isConnecting){
			try{
				if((content = bufferFromServer.readLine())!= null){
					ClientActivity.serverMessage = content;
					int msgLength = content.length();//获取服务端消息的长度
					if(msgLength > 9){
						msgHead = content.substring(0, 10);//摘取信息头标
					}
					if(msgHead.equals("serverName")&&msgLength >8){
						//服务端收到来自服务端用户名的信息，更新客户端的界面
						ClientActivity.serverName = content.substring(10);//摘取服务端用户名
						//通知主线程更改UI，将服务端用户名显示出来
						ClientActivity.clientMessage = "Server online";			
						Message connectSuccessmsg = new Message();
						connectSuccessmsg.what = 1;
				        ClientActivity.messageHandler.sendMessage(connectSuccessmsg);
					}else{//用户通信信息才会被显示
						Message serverMessageReceive= new Message();
						serverMessageReceive.what = 0;
						ClientActivity.messageHandler.sendMessage(serverMessageReceive);
					}	
				}
			}catch(Exception e){
				ClientActivity.clientMessage = "接收异常:" + e.getMessage();//消息换行
				Message serverMessageReceive = new Message();
				serverMessageReceive.what = 1;
				ClientActivity.messageHandler.sendMessage(serverMessageReceive);
			}
		}
	}
}
