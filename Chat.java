import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Chat {
    private ServerSocket severSocket = null;
    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    ArrayList<Socket> scolist=new ArrayList<Socket>();
    ArrayList<Integer> numlist=new ArrayList<Integer>();
    Integer count=0;
    String str1=null;
    public Chat() {
    }

    public void createSocket(String str) {
    	str1=str;
     do{
    	try {
        	
            @SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(str));
            while (true) {
            	if(scolist.size()==0)
            	 createWriteThread();
            socket = serverSocket.accept();
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
                System.out.println("Connected");
                byte[] readBuffer = new byte[200];
                int num = inStream.read(readBuffer);
                byte[] arrayBytes = new byte[num];
                System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                String recvedMessage = new String(arrayBytes, "UTF-8");
                //System.out.println("first msg"+recvedMessage);
                String[] splited1 = recvedMessage.split("\\s+");
                //System.out.println(splited1[0]);
                if(splited1[0].equals("port"))
                {
//                	for(int i=0;i<scolist.size();i++)
//                	{
//                		if()
//                	}
                	//System.out.println("ip"+socket.getInetAddress().getHostAddress().toString()+"port"+Integer.parseInt(splited1[1]));
                	Socket socket123 = new Socket(socket.getInetAddress().getHostAddress().toString(), Integer.parseInt(splited1[1]));
                	//System.out.println("after");
                scolist.add(socket123);
                count++;
                numlist.add(count);
//                
                System.out.println("The connection to peer "+socket123.getInetAddress().getHostAddress().toString()+" is successfully established");

                //                outStream=socket123.getOutputStream();
				String Port="port "+str1;
//				System.out.println(Port);
//				 outStream.write(Port.getBytes("UTF-8"));
                createReadThread(socket123);
                }
                else
                {
                	System.out.println("Received message form:"+socket.getInetAddress().getHostAddress().toString()+"  msg  " + recvedMessage);
                }
//                else
//                {
//                	createReadThread(socket12);	
//                }

            	createReadThread(socket);	      
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
     }while(true);
    }

    public void createReadThread(Socket s) {
        Thread readThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        byte[] readBuffer = new byte[200];
                        int num = s.getInputStream().read(readBuffer);
                        if (num > 0) {
                            byte[] arrayBytes = new byte[num];
                            System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                            String recvedMessage = new String(arrayBytes, "UTF-8");
                            
                            System.out.println(recvedMessage);
                            if(recvedMessage.equals("terminated"))
                            {
                            	
                            	int p=0;
                                boolean flag=false;
                                for(int i=0;i<scolist.size();i++)
                                	{
                                	if(s.getInetAddress().getHostAddress().toString().equals(scolist.get(i)))
                                	{
                                		p=i;
                                		flag=true;
                                		break;
                                	}
                                	}
                                if(flag)
                                {
                                	scolist.remove(p);
        							numlist.remove(p);
                                }
                                s.shutdownInput();
                            }else
                            {
                            	System.out.println("Received message form:"+s.getInetAddress().getHostAddress().toString()+"  msg  " + recvedMessage);
                            }
                            
                        } else {
                            notify();
                        }
                        ;
                        //System.arraycopy();

                    } catch (SocketException se) {
                        System.exit(0);


                    } catch (IOException i) {
                        i.printStackTrace();
                    }


                }
            }
        };
        readThread.setPriority(Thread.MAX_PRIORITY);
        readThread.start();
    }

    public void createWriteThread() {
        Thread writeThread = new Thread() {
            public void run() {

                while (true) {
                    try {
                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                  
                        String typedMessage = inputReader.readLine();
                        //System.out.println("message1:"+typedMessage);
                        if (typedMessage != null && typedMessage.length() > 0) {
                      //System.out.println("1"); 
                        	//typedMessage = inputReader.readLine();
							String[] splited = typedMessage.split("\\s+");
						//int c = System.in.read();
					//System.out.println("message2:"+typedMessage);	
					
						switch (splited[0]) {
						case "help":
							System.out.println("myip:- display IP address");
							System.out.println("myport:- display port number");
							System.out.println("connect:- connect to another peer");
							System.out.println("send:- send messages to peers");
							System.out.println("terminate:- close the connection between peers");
							System.out.println("exit:- exit the program");

						break;
						case "myip":
						System.out.println("The IP address is "+InetAddress.getLocalHost().getHostAddress());	
						break;
						case "myport":
							System.out.println("The program runs on port number "+str1);
						break;
						case "list":
							System.out.println("Id:   IP Address     Port No.");
							for(int i=0;i<scolist.size();i++)
							System.out.println((i+1)+"  "+scolist.get(i).getInetAddress().getHostAddress()+"  "+scolist.get(i).getPort());
						break;
						case "connect":
						Socket	 socket2 = new Socket(splited[1], Integer.parseInt(splited[2]));
						outStream=socket2.getOutputStream();
						String Port=str1;
						//System.out.println(Port);
						 outStream.write(Port.getBytes("UTF-8"));
						System.out.println("The connection to peer"+ splited[1]+" is successfully established");
						scolist.add(socket2);
						 count++;
			                numlist.add(count);
						createReadThread(socket2);	
						break;
						case "send":
							//System.out.println("id="+splited[1]+"size"+scolist.size());
							//System.out.println(Integer.parseInt(splited[1])-1);
							Socket s1=scolist.get(Integer.parseInt(splited[1])-1);
							outStream=s1.getOutputStream();
							String string1=splited[2];
							for(int i=3;i<splited.length;i++)
								string1=string1+" "+splited[i];
							 outStream.write(string1.getBytes("UTF-8"));
							break;
						
							
						case "terminate":
							int p=numlist.indexOf(Integer.parseInt(splited[1]));
							Socket s2=scolist.get(p);

							outStream=s2.getOutputStream();
							String msg1="terminated";
							String msg2=s2.getInetAddress().getHostAddress().toString();
							outStream.write(msg1.getBytes("UTF-8"));

							//s2.getInputStream().close();
							//s2.close();
							System.out.println("connection with "+msg2+" closed");
							scolist.remove(p);
							numlist.remove(p);
							break;
							
						/*case "exit":
							for(Socket s:scolist)
							{
								s.close();
							}
							break;*/
                      }/* else {
                            notify();
                        }*/
                        ;
                        //System.arraycopy();
                        sleep(100);
                    }} catch (IOException i) {
                        i.printStackTrace();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }


                }
            }
        };
        writeThread.setPriority(Thread.MAX_PRIORITY);
        writeThread.start();

   }

    public static void main(String[] args) {
        Chat chatServer = new Chat();
        chatServer.createSocket(args[0]);

    }
}
