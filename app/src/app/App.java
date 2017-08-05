package app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.List;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
//import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import app.Tuple;


public class App {

	private JFrame frame;
	private JTextField textFieldRemotePort;
	private JTextField textFieldLocalPort;
	private Map<String, String> credentials = new HashMap<String, String>();
	//ArrayList portlist = new ArrayList();
	
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

	
	private void updatePlainCommand(DefaultListModel<String> ltm)
	{
		//String d = "";
		String[] temp = null;
		for(int i = 0; i< ltm.getSize(); i++){
            temp = ((String) ltm.getElementAt(i)).split("-");
            System.out.println(temp[0]);
        }
		
		
		
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 464, 356);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
	
        JSONParser parser = new JSONParser();
 
        try {
 
        	String dir = System.getProperty("user.dir")+"/src/app/";

            Object obj = parser.parse(new FileReader(dir+"credentials.json"));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            String username = (String) jsonObject.get("username");
            String password = (String) jsonObject.get("password");
            
            credentials.put("username",username);
            credentials.put("password",password);
            /*
            Object obj1 = parser.parse(new FileReader(dir+"portforwardlist.json"));
            JSONObject jsonObject1 = (JSONObject) obj;
            
            JSONArray companyList = (JSONArray) jsonObject1.get("portforwardlist");
 */
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 59, 168, 95);
		
		JButton btnToggleService = new JButton("Start Service");
		btnToggleService.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		btnToggleService.setBounds(181, 255, 240, 45);
		frame.getContentPane().add(btnToggleService);

        frame.getContentPane().add(scrollPane);
        
        JList<String> listListofPorts = new JList<String>();
        scrollPane.setViewportView(listListofPorts);
        
        listListofPorts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listListofPorts.setFont(new Font("Serif", Font.ITALIC, 14));
        listListofPorts.setLayoutOrientation(JList.VERTICAL);  
        
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

		textFieldRemotePort.setBounds(103, 212, 57, 20);
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

		textFieldLocalPort.setBounds(103, 181, 57, 20);
		frame.getContentPane().add(textFieldLocalPort);
		textFieldLocalPort.setColumns(10);
		
		JTextPane txtPlainCommand = new JTextPane();
		txtPlainCommand.setBounds(253, 59, 168, 95);
		frame.getContentPane().add(txtPlainCommand);
		
		JButton btnStoreCredentials = new JButton("Store Credentials");
		btnStoreCredentials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowCredentials dialog = new ShowCredentials();
				dialog.setVisible(true);
				
				dialog.addWindowListener(new WindowAdapter() {
			         public void windowDeactivated(WindowEvent windowEvent){
			            System.out.println(dialog.get_credentials());
			            System.out.println(credentials.get("username"));
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
		
		JLabel lblNewLabel_1 = new JLabel("List of Forwarded Ports:");
		lblNewLabel_1.setBounds(29, 43, 143, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		
		
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				DefaultListModel<String> ltm = new DefaultListModel<String>();
				
				 for(int i = 0; i< listListofPorts.getModel().getSize();i++){
			            ltm.addElement((listListofPorts.getModel().getElementAt(i)));
			        }
				
				ltm.addElement(textFieldLocalPort.getText()+"-"+textFieldRemotePort.getText());
				
				listListofPorts.setModel(ltm);
				
				updatePlainCommand(ltm);
				
			}
		});
		btnAdd.setBounds(181, 180, 89, 23);
		frame.getContentPane().add(btnAdd);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setBounds(181, 211, 89, 23);
		frame.getContentPane().add(btnRemove);
		
		JLabel lblLocalPort = new JLabel("Local Port:");
		lblLocalPort.setBounds(29, 184, 74, 14);
		frame.getContentPane().add(lblLocalPort);
		
		JLabel lblRemotePort = new JLabel("Remote Port:");
		lblRemotePort.setBounds(29, 218, 74, 14);
		frame.getContentPane().add(lblRemotePort);
		
		
		
		
		
	}
}
