package app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.SwingConstants;

public class ShowCredentials extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField_un;
	private String Credentials;
	private JTextField textField__pw;
	private JTextField textField_dm;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShowCredentials frame = new ShowCredentials();
					frame.setVisible(true);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Map<String, String>  get_credentials()
	{
		Map<String, String> credentials = new HashMap<String, String>();
		
		credentials.put("domain",textField_dm.getText());
		credentials.put("username",textField_un.getText());
        credentials.put("password",textField__pw.getText());
		
		return credentials;
	}
	
	public ShowCredentials(String domain, String username, String password)
	{
		initialize();
		textField_dm.setText(domain);
		textField_un.setText(username);
		textField__pw.setText(password);
	}
	
	private void initialize()
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 268, 307);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField_un = new JTextField();
		textField_un.setBounds(116, 121, 115, 20);
		contentPane.add(textField_un);
		textField_un.setColumns(10);
		
		JButton btn_save = new JButton("Save");
		btn_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Credentials = textField_un.getText();
				dispose();
			}
		});
		btn_save.setBounds(130, 224, 89, 23);
		contentPane.add(btn_save);
		
		JLabel lblNewLabel = new JLabel("Enter Credentiales");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setBounds(48, 27, 153, 14);
		contentPane.add(lblNewLabel);
		
		textField__pw = new JTextField();
		textField__pw.setBounds(116, 163, 115, 20);
		contentPane.add(textField__pw);
		textField__pw.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Enter Username:");
		lblNewLabel_1.setBounds(12, 124, 81, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Enter Password:");
		lblNewLabel_2.setBounds(12, 166, 85, 14);
		contentPane.add(lblNewLabel_2);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("View Password");
		chckbxNewCheckBox.setEnabled(false);
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setBounds(12, 224, 97, 23);
		contentPane.add(chckbxNewCheckBox);
		
		textField_dm = new JTextField();
		textField_dm.setColumns(10);
		textField_dm.setBounds(116, 77, 115, 20);
		contentPane.add(textField_dm);
		
		JLabel lblEnterDomain = new JLabel("Enter Domain:");
		lblEnterDomain.setBounds(12, 80, 81, 14);
		contentPane.add(lblEnterDomain);
	}
	
	/**
	 * Create the frame.
	 */
	public ShowCredentials() {
		initialize();
	}
}
