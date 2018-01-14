package com.sharavara.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.ibm.mq.MQC;
import com.ibm.mq.MQMessage;
import com.sharavara.mq.MQProvider;
import com.sharavara.util.Utils;

public class GetMessages extends SwingWorker<Integer, Void> {

	private JButton btn;
	private JProgressBar progressBar;
	private JTable table;
	private MQProvider mq;
	private DefaultTableModel tModel;
	private String qname;
	private JTextArea textLog;
	public static SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private Integer mLimit;

	final Vector columnIdentifiers = new Vector() {
		{
			add("#");
			add("Put Date");
			add("MsgID");
			add("Message");
		}
	};

	public GetMessages(JButton btnBrowse, JProgressBar progressBar, JTable table, MQProvider mq, DefaultTableModel tModel, String qname, JTextArea textLog, Integer mLimit ) {
		this.btn = btnBrowse;
		this.progressBar = progressBar;
		this.table = table;
		this.mq = mq;
		this.tModel = tModel;
		this.qname = qname;
		this.textLog = textLog;
		this.mLimit = mLimit;
	}

	@Override
	protected Integer doInBackground() throws Exception {

		try {
			/*
			 * int mescount = mq.getCurrentDepth(txtQOut.getText()); System.out.println(mescount); progressBar.setIndeterminate(true);
			 */
			progressBar.setVisible(true);
			Vector dataVector = null;
			
			tModel.setDataVector(dataVector, columnIdentifiers);
			HashMap mesObj = mq.getMessage(qname);
			MQMessage message = new MQMessage();
			int i = 1;
			while (mesObj != null && mLimit >=i ) {
				message = (MQMessage) mesObj.get("MQMESSAGE");
				Vector rowData = new Vector();
				rowData.add(i);
				i++;
				rowData.add(datetimeFormat.format(message.putDateTime.getTime()));
				rowData.add(Utils.byteArrayToHexString((byte[]) mesObj.get("ID")));
				rowData.add(mesObj.get("MESSAGE"));
				tModel.addRow(rowData);
				if (mLimit > i-1) 
					mesObj = mq.getMessage(qname);
			}

			table.setModel(tModel);
			table.getColumnModel().getColumn(0).setPreferredWidth(25);
			table.getColumnModel().getColumn(1).setPreferredWidth(90);
			table.getColumnModel().getColumn(2).setPreferredWidth(330);
			
			textLog.append(datetimeFormat.format(new Date()) + "\tMessages have been got\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			textLog.append(datetimeFormat.format(new Date()) + "\t" + sw.toString());

		} finally {
			btn.setEnabled(true);
			progressBar.setIndeterminate(false);
			progressBar.setVisible(false);

		}

		return 1;
	}

	protected void done() {
		Integer status;
		try {
			// Retrieve the return value of doInBackground.
			status = get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
