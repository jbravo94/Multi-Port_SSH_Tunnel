package app;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JTextArea;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.jcraft.jsch.*;



public class App {

	private JFrame frame;
	private JTextField textFieldRemotePort;
	private JTextField textFieldLocalPort;
	private JTextArea textAreaPlainCommand;
	private Map<String, String> credentials = new HashMap<String, String>();
	String dir = System.getProperty("user.dir")+"/src/app/";
	JSONArray portforwardlist;
	JList<String> listListofPorts;
	private boolean tunnel_enabled = false;
	private JSch jsch = new JSch();
	private Session session = new Session();
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					App window = new App();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	public App() {
		initialize();
	}

	
	  public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		    public String getPassword(){ return passwd; }
		    
		    public void setPassword(String passwd) {this.passwd = passwd;}
		    
		    public void setPromptYesNo(String choice) {promptYesNo = choice;}
		    
		    String promptYesNo = "undefined";
		    
		    public boolean promptYesNo(String str){
		    	if (promptYesNo.equals("yes"))
		    	{
		    		return true;
		    	}
		    	else if (promptYesNo.equals("no"))
		    	{
		    		return false;
		    	}
		    	else
		    	{
			      Object[] options={ "yes", "no" };
			      int foo=JOptionPane.showOptionDialog(null, 
			             str,
			             "Warning", 
			             JOptionPane.DEFAULT_OPTION, 
			             JOptionPane.WARNING_MESSAGE,
			             null, options, options[0]);
			      System.out.println(foo==0);
			       return foo==0;
		    	}
		    }
		  
		    String passwd = "";
		    JTextField passwordField=(JTextField)new JPasswordField(20);

		    public String getPassphrase(){ return passwd; }
		    public boolean promptPassphrase(String message){ return true; }
		    
		    public boolean promptPassword(String message){
		    	
		    	if (passwd.equals(""))
		    	{
		    		 Object[] ob={passwordField}; 
				      int result=
					  JOptionPane.showConfirmDialog(null, ob, message,
									JOptionPane.OK_CANCEL_OPTION);
				      if(result==JOptionPane.OK_OPTION){
					passwd=passwordField.getText();
					return true;
				      }
				      else{ return false; }
		    	}
		    	else
		    	{
		    		return true;
		    	}
		      
		    }
		    
		    public void showMessage(String message){
		      JOptionPane.showMessageDialog(null, message);
		    }
		    final GridBagConstraints gbc = 
		      new GridBagConstraints(0,0,1,1,1,1,
		                             GridBagConstraints.NORTHWEST,
		                             GridBagConstraints.NONE,
		                             new Insets(0,0,0,0),0,0);
		    private Container panel;

			@Override
			public String[] promptKeyboardInteractive(String destination,
                    String name,
                    String instruction,
                    String[] prompt,
                    boolean[] echo){
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;
			
			gbc.gridwidth = GridBagConstraints.RELATIVE;
			
			JTextField[] texts=new JTextField[prompt.length];
			for(int i=0; i<prompt.length; i++){
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 0;
			gbc.weightx = 1;
			panel.add(new JLabel(prompt[i]),gbc);
			
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = 1;
			if(echo[i]){
			texts[i]=new JTextField(20);
			}
			else{
			texts[i]=new JPasswordField(20);
			}
			panel.add(texts[i], gbc);
			gbc.gridy++;
			}
			
			if(JOptionPane.showConfirmDialog(null, panel, 
			             destination+": "+name,
			             JOptionPane.OK_CANCEL_OPTION,
			             JOptionPane.QUESTION_MESSAGE)
			==JOptionPane.OK_OPTION){
			String[] response=new String[prompt.length];
			for(int i=0; i<prompt.length; i++){
			response[i]=texts[i].getText();
			}
			return response;
			}
			else{
			return null;  // cancel
			}
		}
	}
	
	
	private void updatePlainCommand()
	{
		String command = "ssh -vN ";
		
		for(int i = 0; i < portforwardlist.size(); i++){
			
			String localport = ((JSONObject) portforwardlist.get(i)).get("localport").toString();
			String remoteport = ((JSONObject) portforwardlist.get(i)).get("remoteport").toString();
			
            command += String.format("-R %s:%s:%s ", remoteport, "localhost", localport);
        }
		command += String.format("%s@%s", credentials.get("username"), credentials.get("domain"));
		
		textAreaPlainCommand.setText(command);
	}
	
	private void updateListofPorts()
	{
		DefaultListModel<String> ltm = new DefaultListModel<String>();
		
		for(int i = 0; i < portforwardlist.size();i++){

			String localport = ((JSONObject) portforwardlist.get(i)).get("localport").toString();
			String remoteport = ((JSONObject) portforwardlist.get(i)).get("remoteport").toString();
			
			for (int o = 0; o < 5; o ++)
			{
				if (localport.length() < 5)
				{
					localport = " " + localport;
				}
				if (remoteport.length() < 5)
				{
					remoteport = " " + remoteport;
				}
			} 
			ltm.addElement(String.format("%s <-> %s", remoteport, localport));
	    }
		
		listListofPorts.setModel(ltm);
		
		updatePlainCommand();
	}
	
	private void store_portforwardlist()
	{
		JSONObject obj = new JSONObject();
		
		obj.put("portforwardlist", portforwardlist);
		
        try (FileWriter file = new FileWriter(dir+"portforwardlist.json")) {
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void load_credentials()
	{
        JSONParser parser = new JSONParser();
 
        try {
            Object obj = parser.parse(new FileReader(dir+"credentials.json"));
            JSONObject jsonObject = (JSONObject) obj;
 
            String domain = (String) jsonObject.get("domain");
            String username = (String) jsonObject.get("username");
            String password = (String) jsonObject.get("password");
            
            credentials.put("domain",domain);
            credentials.put("username",username);
            credentials.put("password",password);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void store_credentials(String domain, String username, String password)
	{
		JSONObject obj = new JSONObject();
		obj.put("domain",  domain);
        obj.put("username",  username);
        obj.put("password",  password);

        try (FileWriter file = new FileWriter(dir+"credentials.json")) {
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void load_portforwardlist()
	{
		JSONParser parser = new JSONParser();
		 
        try {
            Object obj = parser.parse(new FileReader(dir+"portforwardlist.json"));
            JSONObject jsonObject = (JSONObject) obj;
            
            portforwardlist = (JSONArray) jsonObject.get("portforwardlist");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 464, 356);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 59, 168, 95);
		
		frame.getContentPane().add(scrollPane);
		JButton btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		
        listListofPorts = new JList<String>();
        listListofPorts.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent arg0) {
        		if (!arg0.getValueIsAdjusting()) {
        			if (listListofPorts.getSelectedValue() != null)
        			{
	        			btnRemove.setEnabled(true);
	                }
	        		else
	        		{
	        			btnRemove.setEnabled(false);
	        		}
        		}
        	}
        });

        scrollPane.setViewportView(listListofPorts);
        
        listListofPorts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listListofPorts.setFont(new Font("Courier", Font.PLAIN, 11));
        listListofPorts.setLayoutOrientation(JList.VERTICAL);  
        
        textAreaPlainCommand = new JTextArea();
        textAreaPlainCommand.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textAreaPlainCommand.setLineWrap(true);
		textAreaPlainCommand.setBounds(253, 59, 168, 95);
		frame.getContentPane().add(textAreaPlainCommand);
		
		load_credentials();
		load_portforwardlist();
		updateListofPorts();
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBackground(Color.RED);
		statusPanel.setBounds(145, 267, 20, 20);
		frame.getContentPane().add(statusPanel);
		
		JButton btnToggleService = new JButton("Start Service");
		btnToggleService.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnToggleService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (!tunnel_enabled)
				{
					try {
						session = jsch.getSession(credentials.get("username"), credentials.get("domain"), 22);
						UserInfo ui=new MyUserInfo();
						ui.setPassword(credentials.get("password"));
						ui.setPromptYesNo("yes");
				        session.setUserInfo(ui);

						session.connect();

						for (int i = 0; i < portforwardlist.size(); i ++) {
							session.setPortForwardingR(Integer.parseInt((String) ((JSONObject) portforwardlist.get(i)).get("remoteport")), "localhost", Integer.parseInt((String) ((JSONObject) portforwardlist.get(i)).get("localport")));
						}
						
						btnToggleService.setText("Stop service");
						statusPanel.setBackground(Color.GREEN);
						tunnel_enabled = true;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					session.disconnect();
					btnToggleService.setText("Start service");
					statusPanel.setBackground(Color.RED);
					tunnel_enabled = false;
				}
			}
		});
		
		btnToggleService.setBounds(181, 255, 240, 45);
		frame.getContentPane().add(btnToggleService);

        JButton btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);

		textFieldRemotePort = new JTextField();
		textFieldRemotePort.addKeyListener(new KeyAdapter() {
		
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					Integer.parseInt(textFieldLocalPort.getText());
					Integer.parseInt(textFieldRemotePort.getText());
					btnAdd.setEnabled(true);
				}
				catch (Exception e1) {
					btnAdd.setEnabled(false);
				}
			}
		});

		textFieldRemotePort.setBounds(103, 181, 57, 20);
		frame.getContentPane().add(textFieldRemotePort);
		textFieldRemotePort.setColumns(10);
		
		textFieldLocalPort = new JTextField();
		textFieldLocalPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					Integer.parseInt(textFieldLocalPort.getText());
					Integer.parseInt(textFieldRemotePort.getText());
					btnAdd.setEnabled(true);
				}
				catch (Exception e1) {
					btnAdd.setEnabled(false);
				}
			}
		});

		textFieldLocalPort.setBounds(103, 212, 57, 20);
		frame.getContentPane().add(textFieldLocalPort);
		textFieldLocalPort.setColumns(10);
		
		JButton btnStoreCredentials = new JButton("Store Credentials");
		btnStoreCredentials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load_credentials();
				
				ShowCredentials dialog = new ShowCredentials(credentials.get("domain"),credentials.get("username"),credentials.get("password"));
				dialog.setVisible(true);
				
				dialog.addWindowListener(new WindowAdapter() {
			         public void windowDeactivated(WindowEvent windowEvent){
			            Map<String, String> entered_credentials = dialog.get_credentials();
			            if (!(entered_credentials.get("domain").equals(credentials.get("domain"))) || !(entered_credentials.get("username").equals(credentials.get("username"))) || !(entered_credentials.get("password").equals(credentials.get("password"))))
						{
							store_credentials(entered_credentials.get("domain"),entered_credentials.get("username"), entered_credentials.get("password"));
							credentials.put("domain", entered_credentials.get("domain"));
							credentials.put("username", entered_credentials.get("username"));
							credentials.put("password", entered_credentials.get("password"));
							updatePlainCommand();
						}
			         }
				});
			}
		});
		
		btnStoreCredentials.setBounds(293, 181, 128, 52);
		frame.getContentPane().add(btnStoreCredentials);
		
		JLabel lblNewLabel = new JLabel("Plain Command:");
		lblNewLabel.setBounds(253, 43, 89, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JCheckBox chckbxStartAsService = new JCheckBox("Start as service");
		chckbxStartAsService.setEnabled(false);
		chckbxStartAsService.setBounds(29, 266, 106, 23);
		frame.getContentPane().add(chckbxStartAsService);
		
		JLabel lblMultiportSshTunnel = new JLabel("Multi-Port SSH Tunnel");
		lblMultiportSshTunnel.setHorizontalAlignment(SwingConstants.CENTER);
		lblMultiportSshTunnel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblMultiportSshTunnel.setBounds(144, 11, 173, 14);
		frame.getContentPane().add(lblMultiportSshTunnel);
		
		JLabel lblNewLabel_1 = new JLabel("Remoteport <-> Localport");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setForeground(Color.GRAY);
		lblNewLabel_1.setBounds(29, 156, 168, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 String localport = textFieldLocalPort.getText();
				 String remoteport = textFieldRemotePort.getText();
				
				 DefaultListModel<String> ltm = new DefaultListModel<String>();
				
				 for(int i = 0; i< listListofPorts.getModel().getSize();i++){
			            ltm.addElement((listListofPorts.getModel().getElementAt(i)));
			     }
				 
				 Map<String, String> temp = new HashMap<String, String>();
					
				 temp.put("localport",localport);
				 temp.put("remoteport",remoteport);
				 
				 for (int o = 0; o < 5; o ++)
					{
						if (localport.length() < 5)
						{
							localport = " " + localport;
						}
						if (remoteport.length() < 5)
						{
							remoteport = " " + remoteport;
						}
					} 
				
				if (!(ltm.contains(String.format("%s <-> %s", remoteport, localport))))
				{
					ltm.addElement(String.format("%s <-> %s", remoteport, localport)); 
					 
					listListofPorts.setModel(ltm);
	
					portforwardlist.add(new JSONObject(temp));
					store_portforwardlist();
					updatePlainCommand();
				}
			}
		});
		
		btnAdd.setBounds(181, 180, 89, 23);
		frame.getContentPane().add(btnAdd);
		
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String[] temp = listListofPorts.getSelectedValue().split("<->");
				String remoteport = temp[0].replaceAll(" ", "");
				String localport = temp[1].replaceAll(" ", "");
				
				for(int i = 0; i < portforwardlist.size(); i++)
				{
					if (((JSONObject) portforwardlist.get(i)).get("localport").toString().equals(localport) && ((JSONObject) portforwardlist.get(i)).get("remoteport").toString().equals(remoteport))
					{
						portforwardlist.remove(i);
					}
				}
				store_portforwardlist();
				updateListofPorts();
			}
		});
		
		btnRemove.setBounds(181, 211, 89, 23);
		frame.getContentPane().add(btnRemove);
		
		JLabel lblLocalPort = new JLabel("Local Port:");
		lblLocalPort.setBounds(29, 215, 74, 14);
		frame.getContentPane().add(lblLocalPort);
		
		JLabel lblRemotePort = new JLabel("Remote Port:");
		lblRemotePort.setBounds(29, 187, 74, 14);
		frame.getContentPane().add(lblRemotePort);
		
		JLabel label = new JLabel("List of Forwarded Ports:");
		label.setBounds(29, 43, 143, 14);
		frame.getContentPane().add(label);
	}
}
