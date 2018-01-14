package com.sharavara.mq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

public class MQProvider {
	private MQQueueManager qMgr;
	private int qOptions = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_OUTPUT | CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE;
	private HashMap<String, MQQueue> allMQ = new HashMap<String, MQQueue>();

	public MQProvider(Properties config) throws MQException {
		MQEnvironment.hostname = config.getProperty("MQhost");
		MQEnvironment.channel = config.getProperty("MQchannel");
		MQEnvironment.port = Integer.parseInt(config.getProperty("MQport"));
		if (!config.getProperty("MQuserID").equals(""))
			MQEnvironment.userID = config.getProperty("MQuserID");
		if (!config.getProperty("MQpassword").equals(""))
			MQEnvironment.password = config.getProperty("MQpassword");
		MQEnvironment.disableTracing();
		// MQEnvironment.traceSystemProperties();
		MQException.log = null;
		qMgr = new MQQueueManager(config.getProperty("MQmanager"));
	}

	public void put(String qName, String replyToQueue, byte[] ID, String message, int encoding, int CCSID)
			throws IOException, MQException {
		MQMessage inM = new MQMessage();
		inM.encoding = encoding; // 273
		inM.characterSet = CCSID; // 1208
		if (replyToQueue != null)
			inM.replyToQueueName = replyToQueue;
		if (ID != null)
			inM.messageId = ID;
		inM.write(message.getBytes("UTF-8"));
		MQQueue q = null;
		if (allMQ.containsKey(qName)) {
			q = allMQ.get(qName);
		} else {
			q = qMgr.accessQueue(qName, qOptions);
			allMQ.put(qName, q);
		}
		MQPutMessageOptions pmo = new MQPutMessageOptions();
		q.put(inM, pmo);
	}

	public String get(String qName, byte[] ID) throws Exception {
		String message = null;
		message = getbrowse(qName, ID, false);
		return message;

	}

	public String browse(String qName, byte[] ID) throws Exception {
		String message = null;
		message = getbrowse(qName, ID, true);
		return message;
	}

	private String getbrowse(String qName, byte[] ID, boolean browse) throws Exception {
		String message = null;
		MQMessage outM = new MQMessage();
		if (ID != null)
			outM.correlationId = ID;
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		if (browse)
			gmo.options = MQC.MQGMO_BROWSE_FIRST;

		MQQueue q = null;
		if (allMQ.containsKey(qName)) {
			q = allMQ.get(qName);
		} else {
			q = qMgr.accessQueue(qName, qOptions);
			allMQ.put(qName, q);
		}

		try {
			q.get(outM, gmo);
		} catch (MQException e) {
			if (e.reasonCode == 2033) { // queue is empty
				return null;
			} else {
				throw new Exception("A WebSphere MQ error occurred: Completion code: " + e.completionCode
						+ " Reason code: " + e.reasonCode, e);
			}
		}
		byte[] ans = new byte[outM.getDataLength()];
		outM.readFully(ans);
		message = new String(ans, "UTF-8");
		return message;
	}

	public HashMap<String, Object> browseMessage(String qName, int browseType) throws Exception {
		HashMap<String, Object> res = new HashMap<String, Object>();
		MQMessage outM = new MQMessage();
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		gmo.options = browseType; // MQC.MQGMO_BROWSE_NEXT; MQC.MQGMO_BROWSE_FIRST;
		try {

			MQQueue q = null;
			if (allMQ.containsKey(qName)) {
				q = allMQ.get(qName);
			} else {
				q = qMgr.accessQueue(qName, qOptions);
				allMQ.put(qName, q);
			}
			q.get(outM, gmo);

		} catch (MQException e) {
			if (e.reasonCode == 2033) { // queue is empty
				return null;
			} else {
				throw new Exception("A WebSphere MQ error occurred: Completion code: " + e.completionCode
						+ " Reason code: " + e.reasonCode, e);
			}
		}
		byte[] ans = new byte[outM.getDataLength()];
		outM.readFully(ans);
		res.put("ID", outM.messageId);
		res.put("MESSAGE", new String(ans, "UTF-8"));// UTF-8
		res.put("MQMESSAGE", outM);
		return res;
	}

	public HashMap<String, Object> getMessage(String qName) throws Exception {
		HashMap<String, Object> res = new HashMap<String, Object>();
		MQMessage outM = new MQMessage();
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		try {

			MQQueue q = null;
			if (allMQ.containsKey(qName)) {
				q = allMQ.get(qName);
			} else {
				q = qMgr.accessQueue(qName, qOptions);
				allMQ.put(qName, q);
			}
			q.get(outM, gmo);

		} catch (MQException e) {
			if (e.reasonCode == 2033) { // queue is empty
				return null;
			} else {
				throw new Exception("A WebSphere MQ error occurred: Completion code: " + e.completionCode
						+ " Reason code: " + e.reasonCode, e);
			}
		}
		byte[] ans = new byte[outM.getDataLength()];
		outM.readFully(ans);
		res.put("ID", outM.messageId);
		res.put("MESSAGE", new String(ans, "UTF-8"));
		res.put("MQMESSAGE", outM);
		return res;
	}

	public int getCurrentDepth(String qName) throws Exception {
		try {

			MQQueue q = null;
			if (allMQ.containsKey(qName)) {
				q = allMQ.get(qName);
			} else {
				q = qMgr.accessQueue(qName, qOptions);
				allMQ.put(qName, q);
			}
			return q.getCurrentDepth();

		} catch (MQException e) {
			if (e.reasonCode == 2033) { // queue is empty
				return 0;
			} else {
				throw new Exception("A WebSphere MQ error occurred: Completion code: " + e.completionCode
						+ " Reason code: " + e.reasonCode, e);
			}
		}
	}

	public void close() throws MQException {
		Map<String, MQQueue> map = allMQ;
		Iterator<Entry<String, MQQueue>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, MQQueue> entry = (Entry<String, MQQueue>) iterator.next();
			MQQueue mq = entry.getValue();
			if (mq != null)
				mq.close();
		}

		if (qMgr != null)
			qMgr.disconnect();
	}
}
