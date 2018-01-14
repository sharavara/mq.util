package com.sharavara.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.ibm.mq.MQException;
import com.sharavara.mq.Environment;
import com.sharavara.mq.MQProvider;

public class Main {

	private JFrame frmMqbrowser;
	private MQProvider mq = null;
	private boolean connected = false;
	private JTextField textStatus;
	private JPanel pPut;
	private JPanel pGet;
	private JLabel lblNewLabel;
	private JLabel lblReplyTo;
	private JTextField textQIn;
	private JTextField textQReply;
	private JTextField textMsgId;
	private JLabel lblQueue;
	private JTextField txtQOut;
	private JButton btnBrowse;
	private JButton btnGetAllMessages;
	private JScrollPane scrollPane_2;
	private JTable table;
	private JFormattedTextField txtRepeat;
	private JFormattedTextField txtLimit;
	private JTextArea textLog;
	private static Environment env;

	public static SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private JScrollPane scrollPane_3;
	private JTextPane textReadMessage;
	private JProgressBar progressBar;
	private JLabel lblRepeat;
	private JTextField txtEncoding;
	private JTextField txtCharacterset;
	private JTextField txtHost;
	private JTextField txtCh;
	private JTextField txtQm;
	private JTextField txtPort;
	private JTextField txtUsername;
	private JPasswordField txtPasswd;
	private JTextField txtEnvName;

	final static String ENV_FILE_NAME = "settings.dat";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					File envFile = new File(ENV_FILE_NAME);
					boolean exists = envFile.exists();

					if (exists) {
						ObjectInputStream in = new ObjectInputStream(new FileInputStream(ENV_FILE_NAME));
						env = (Environment) in.readObject();
						in.close();
					} else {
						env = new Environment();
					}

					Main window = new Main();
					window.frmMqbrowser.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		frmMqbrowser = new JFrame();
		frmMqbrowser.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (mq != null) {
					try {
						mq.close();
					} catch (MQException ex) {
						ex.printStackTrace();
					}

				}
			}
		});
		frmMqbrowser.setResizable(false);
		frmMqbrowser.setTitle("MQ Util  0.5");
		frmMqbrowser.setBounds(100, 100, 1301, 776);
		frmMqbrowser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pHeader = new JPanel();
		pHeader.setFont(new Font("Tahoma", Font.BOLD, 11));

		final JButton btnConnect = new JButton("Connect");
		btnConnect.setIcon(new ImageIcon(Main.class.getResource("/resources/connect.gif")));
		btnConnect.setBounds(10, 76, 86, 20);
		pHeader.add(btnConnect);

		pHeader.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Connection parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		pPut = new JPanel();
		pPut.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Put message", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		pGet = new JPanel();
		pGet.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Get message", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JLabel lblHttpsharavaracom = new JLabel("\u00A9 http://sharavara.com");
		lblHttpsharavaracom.setFont(new Font("Verdana", Font.PLAIN, 10));

		JButton btnClearLog = new JButton("Clear log");
		btnClearLog.setIcon(new ImageIcon(Main.class.getResource("/resources/cross.gif")));
		btnClearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textLog.setText("");
			}
		});
		GroupLayout groupLayout = new GroupLayout(frmMqbrowser.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(pHeader, GroupLayout.DEFAULT_SIZE, 1277, Short.MAX_VALUE)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1277, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup().addComponent(btnClearLog)
										.addPreferredGap(ComponentPlacement.RELATED, 1054, Short.MAX_VALUE)
										.addComponent(lblHttpsharavaracom))
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(pPut, GroupLayout.PREFERRED_SIZE, 560, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(pGet, GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)))
						.addGap(18)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(pHeader, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(pGet, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
								.addComponent(pPut, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE))
						.addGap(6).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE).addGap(4)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(btnClearLog)
								.addComponent(lblHttpsharavaracom))
						.addContainerGap()));

		textLog = new JTextArea();
		textLog.setBackground(UIManager.getColor("ToolTip.background"));
		scrollPane.setViewportView(textLog);
		pGet.setLayout(null);

		lblQueue = new JLabel("Queue*:");
		lblQueue.setBounds(12, 22, 55, 16);
		pGet.add(lblQueue);

		txtQOut = new JTextField();
		txtQOut.setBounds(68, 20, 331, 20);
		pGet.add(txtQOut);
		txtQOut.setColumns(10);

		table = new JTable();
		final DefaultTableModel tModel = new DefaultTableModel() {
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};

		/*
		 * table.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override public void mouseClicked(MouseEvent arg0) {
		 * //System.out.println(table.getSelectedRow()); textReadMessage.setText(
		 * (String) tModel.getValueAt(table.getSelectedRow(), 2)); } });
		 */
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (table.getSelectedRow() >= 0)
					textReadMessage.setText((String) tModel.getValueAt(table.getSelectedRow(), 3));
			}
		});

		btnBrowse = new JButton("Browse");
		btnBrowse.setIcon(new ImageIcon(Main.class.getResource("/resources/eye.gif")));

		final Vector<String> columnIdentifiers = new Vector<String>() {
			{
				add("#");
				add("Put Date");
				add("MsgID");
				add("Message");
			}
		};

		tModel.setColumnIdentifiers(columnIdentifiers);

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				BrowseMessages bm = new BrowseMessages(btnBrowse, progressBar, table, mq, tModel, txtQOut.getText(),
						textLog, new Integer(txtLimit.getText()));
				bm.execute();
				progressBar.setIndeterminate(true);
				btnBrowse.setEnabled(false);

			}
		});

		btnBrowse.setBounds(511, 18, 101, 26);
		pGet.add(btnBrowse);

		btnGetAllMessages = new JButton("Get");
		btnGetAllMessages.setIcon(new ImageIcon(Main.class.getResource("/resources/basket_remove.gif")));
		btnGetAllMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				GetMessages bm = new GetMessages(btnBrowse, progressBar, table, mq, tModel, txtQOut.getText(), textLog,
						new Integer(txtLimit.getText()));
				bm.execute();
				progressBar.setIndeterminate(true);
				btnBrowse.setEnabled(false);

			}
		});
		btnGetAllMessages.setBounds(613, 17, 78, 26);
		pGet.add(btnGetAllMessages);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setBounds(12, 49, 679, 193);
		pGet.add(scrollPane_2);

		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "#", "Put Date", "MsgID", "Message" }));

		scrollPane_2.setViewportView(table);

		scrollPane_3 = new JScrollPane();
		scrollPane_3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_3.setBounds(12, 253, 679, 241);
		pGet.add(scrollPane_3);

		textReadMessage = new JTextPane();
		scrollPane_3.setViewportView(textReadMessage);

		JLabel lblLimit = new JLabel("Limit:");
		lblLimit.setBounds(405, 23, 46, 14);
		pGet.add(lblLimit);

		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;

		// JFormattedTextField txtLimit = new JFormattedTextField((Format) null);
		txtLimit = new JFormattedTextField(decimalFormat);
		txtLimit.setColumns(10);
		txtLimit.setBounds(451, 20, 55, 20);
		txtLimit.setText("1000");
		pGet.add(txtLimit);
		pPut.setLayout(null);

		lblNewLabel = new JLabel("Queue*:");
		lblNewLabel.setBounds(12, 20, 55, 16);
		pPut.add(lblNewLabel);

		lblReplyTo = new JLabel("Reply To:");
		lblReplyTo.setBounds(12, 48, 66, 16);
		pPut.add(lblReplyTo);

		textQIn = new JTextField();
		textQIn.setBounds(100, 20, 443, 20);
		pPut.add(textQIn);
		textQIn.setColumns(10);

		textQReply = new JTextField();
		textQReply.setBounds(100, 46, 443, 20);
		pPut.add(textQReply);
		textQReply.setColumns(10);

		JLabel lblMessage = new JLabel("Message ID:");
		lblMessage.setBounds(12, 74, 84, 16);
		pPut.add(lblMessage);
		final JTextPane textMessage = new JTextPane();

		JButton btnPut = new JButton("Put");
		btnPut.setIcon(new ImageIcon(Main.class.getResource("/resources/basket_put.gif")));
		btnPut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int rep = 1;
					if (txtRepeat.getText() != null && !txtRepeat.getText().equals("")) {
						rep = new Integer(txtRepeat.getText()).intValue();
					}
					for (int i = 1; i <= rep; i++) {
						String replayQ = null;
						if (!textQReply.getText().equals("")) {
							replayQ = textQReply.getText();
						}
						byte[] msgid = textMsgId.getText().getBytes();

						int encoding;
						int CCSID;
						if (txtEncoding.getText() != null && !txtEncoding.getText().equals("")) {
							encoding = new Integer(txtEncoding.getText()).intValue();
						} else {
							encoding = 273;
						}

						if (txtCharacterset.getText() != null && !txtCharacterset.getText().equals("")) {
							CCSID = new Integer(txtCharacterset.getText()).intValue();
						} else {
							CCSID = 1208;
						}

						mq.put(textQIn.getText(), replayQ, msgid, textMessage.getText(), encoding, CCSID);
						textLog.append(datetimeFormat.format(new Date()) + "\tMessage has been sent to the queue [" + i
								+ "]\n");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					textLog.append(datetimeFormat.format(new Date()) + "\t" + sw.toString());

				}

			}
		});
		btnPut.setBounds(459, 100, 84, 26);
		pPut.add(btnPut);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(12, 131, 531, 363);
		pPut.add(scrollPane_1);

		scrollPane_1.setViewportView(textMessage);
		textMessage.setFont(new Font("Courier New", Font.PLAIN, 11));

		textMsgId = new JTextField();
		textMsgId.setBounds(100, 74, 443, 20);
		pPut.add(textMsgId);
		textMsgId.setColumns(10);

		lblRepeat = new JLabel("Repeat:");
		lblRepeat.setBounds(330, 105, 46, 14);
		pPut.add(lblRepeat);

		decimalFormat.setGroupingUsed(false);
		txtRepeat = new JFormattedTextField(decimalFormat);
		txtRepeat.setBounds(388, 100, 55, 26);
		txtRepeat.setColumns(10); // whatever size you wish to set

		pPut.add(txtRepeat);

		JLabel lblEncoding = new JLabel("Encoding:");
		lblEncoding.setBounds(12, 106, 84, 16);
		pPut.add(lblEncoding);

		txtEncoding = new JFormattedTextField(decimalFormat);
		txtEncoding.setText("273");
		txtEncoding.setBounds(100, 100, 55, 26);
		pPut.add(txtEncoding);
		txtEncoding.setColumns(10);

		JLabel lblCharacterid = new JLabel("CharacterSet:");
		lblCharacterid.setBounds(167, 104, 84, 16);
		pPut.add(lblCharacterid);

		txtCharacterset = new JFormattedTextField(decimalFormat);
		txtCharacterset.setText("1208");
		txtCharacterset.setBounds(263, 100, 55, 26);
		pPut.add(txtCharacterset);
		txtCharacterset.setColumns(10);
		pHeader.setLayout(null);

		JLabel lblEnv = new JLabel("Environment:");
		lblEnv.setBounds(10, 20, 86, 16);
		pHeader.add(lblEnv);
		pHeader.add(btnConnect);

		textStatus = new JTextField();
		textStatus.setEditable(false);
		textStatus.setBackground(UIManager.getColor("ToolTip.background"));
		textStatus.setFont(new Font("Dialog", Font.BOLD, 12));
		textStatus.setBounds(98, 76, 280, 20);
		textStatus.setForeground(Color.RED);
		pHeader.add(textStatus);
		textStatus.setColumns(10);

		// Select the connection
		final JComboBox<String> comboEnv = new JComboBox<String>();
		comboEnv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties config = env.getEnv(String.valueOf(comboEnv.getSelectedItem()));
				txtHost.setText(config.getProperty("MQhost"));
				txtCh.setText(config.getProperty("MQchannel"));
				txtPort.setText(config.getProperty("MQport"));
				txtQm.setText(config.getProperty("MQmanager"));
				txtUsername.setText(config.getProperty("MQuserID"));
				txtPasswd.setText(config.getProperty("MQpassword"));
				txtEnvName.setText(String.valueOf(comboEnv.getSelectedItem()));

			}
		});

		comboEnv.setModel(new DefaultComboBoxModel<String>(env.getKeys()));
		comboEnv.setBounds(98, 19, 280, 20);
		pHeader.add(comboEnv);

		progressBar = new JProgressBar();
		progressBar.setToolTipText("");
		progressBar.setBounds(390, 76, 862, 20);
		pHeader.add(progressBar);

		JButton btnAdd = new JButton("");
		btnAdd.setIcon(new ImageIcon(Main.class.getResource("/resources/add.gif")));
		btnAdd.addActionListener(new ActionListener() {
			// Save connection
			public void actionPerformed(ActionEvent e) {
				Properties config = new Properties();
				config.setProperty("MQhost", txtHost.getText());
				config.setProperty("MQchannel", txtCh.getText());
				config.setProperty("MQport", txtPort.getText());
				config.setProperty("MQmanager", txtQm.getText());
				config.setProperty("MQuserID", txtUsername.getText());
				config.setProperty("MQpassword", txtPasswd.getText());

				if (!txtEnvName.getText().equals("")) {
					env.setEnv(txtEnvName.getText(), config);
					try {
						ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ENV_FILE_NAME));
						out.writeObject(env);
						out.close();
						DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboEnv.getModel();
						if (model.getIndexOf(txtEnvName.getText()) == -1) {
							comboEnv.addItem(txtEnvName.getText());
						}

						textLog.append("[INFO] Connection has been saved\n");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					textLog.append("[WARNING] Connection's name did not provided. Connection hasn't been saved\n");
				}
			}
		});
		btnAdd.setBounds(322, 47, 29, 29);
		pHeader.add(btnAdd);

		JButton btnDelete = new JButton("");
		btnDelete.addActionListener(new ActionListener() {
			// Delete the connection
			public void actionPerformed(ActionEvent e) {
				if (!txtEnvName.getText().equals("")) {
					if (env.delEnv(txtEnvName.getText())) {
						try {
							ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ENV_FILE_NAME));
							out.writeObject(env);
							out.close();
							
							DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboEnv.getModel();
							if (model.getIndexOf(txtEnvName.getText()) != -1) {
								comboEnv.removeItem(txtEnvName.getText());
							}

							textLog.append("[INFO] Connection has been deleted\n");
						} catch (IOException e1) {
							e1.printStackTrace();
						}

					} else {
						textLog.append("[WARNING] Connection's name is not correct. Connection hasn't been deleted\n");
					}
				} else {
					textLog.append("[WARNING] Connection's name did not provided. Connection hasn't been deleted\n");
				}
			}
		});
		btnDelete.setIcon(new ImageIcon(Main.class.getResource("/resources/delete.gif")));
		btnDelete.setBounds(349, 47, 29, 29);
		pHeader.add(btnDelete);

		JLabel label = new JLabel("Host*:");
		label.setBounds(422, 20, 43, 16);
		pHeader.add(label);

		JLabel label_1 = new JLabel("Channel*:");
		label_1.setBounds(984, 20, 71, 16);
		pHeader.add(label_1);

		txtHost = new JTextField();
		txtHost.setColumns(20);
		txtHost.setBounds(466, 18, 201, 20);
		pHeader.add(txtHost);

		txtCh = new JTextField();
		txtCh.setColumns(20);
		txtCh.setBounds(1048, 18, 211, 20);
		pHeader.add(txtCh);

		JLabel label_2 = new JLabel("Queue Manager*:");
		label_2.setBounds(692, 20, 117, 16);
		pHeader.add(label_2);

		JLabel label_3 = new JLabel("Port*:");
		label_3.setBounds(1010, 50, 43, 16);
		pHeader.add(label_3);

		txtQm = new JTextField();
		txtQm.setColumns(15);
		txtQm.setBounds(805, 18, 152, 20);
		pHeader.add(txtQm);

		txtPort = new JFormattedTextField(decimalFormat);
		txtPort.setText("1414");
		txtPort.setColumns(15);
		txtPort.setBounds(1048, 50, 211, 20);
		pHeader.add(txtPort);

		JLabel label_4 = new JLabel("Username*:");
		label_4.setBounds(390, 50, 72, 20);
		pHeader.add(label_4);

		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		txtUsername.setBounds(466, 50, 201, 20);
		pHeader.add(txtUsername);

		JLabel label_5 = new JLabel("Password*:");
		label_5.setBounds(730, 50, 86, 20);
		pHeader.add(label_5);

		txtPasswd = new JPasswordField();
		txtPasswd.setBounds(805, 50, 152, 20);
		pHeader.add(txtPasswd);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 50, 86, 16);
		pHeader.add(lblName);

		txtEnvName = new JTextField();
		txtEnvName.setBounds(98, 47, 222, 26);
		pHeader.add(txtEnvName);
		txtEnvName.setColumns(10);

		progressBar.setVisible(false);

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					if (!connected) {
						// TODO read connection's parameters from fields
						Properties config = new Properties();
						config = env.getEnv(String.valueOf(comboEnv.getSelectedItem()));
						mq = new MQProvider(config);
						connected = true;
						textStatus.setText("Connected");
						btnConnect.setText("Disconnect");
						comboEnv.setEnabled(false);
						textLog.append(datetimeFormat.format(new Date()) + "\tConnected\n");
					} else {
						if (mq != null) {
							mq.close();
							mq = null;
							connected = false;
							textStatus.setText("");
							btnConnect.setText("Connect");
							comboEnv.setEnabled(true);
							textLog.append(datetimeFormat.format(new Date()) + "\tDisconnected\n");
						}
					}
				} catch (MQException ex) {
					ex.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					textLog.append(datetimeFormat.format(new Date()) + "\t" + sw.toString());
				}
			}

		});
		frmMqbrowser.getContentPane().setLayout(groupLayout);
	}
}
