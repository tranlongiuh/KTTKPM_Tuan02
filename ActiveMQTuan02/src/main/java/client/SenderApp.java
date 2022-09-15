package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

public class SenderApp {

	public static JTextField motto1;
	public static Message msg;
	public static Session session;
	public static MessageProducer producer;
	public static Connection con;

	public static void main(String[] args) throws Exception {
		// config environment for JMS
		BasicConfigurator.configure();
		// config environment for JNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		// create context
		Context ctx = new InitialContext(settings);
		// lookup JMS connection factory
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		// lookup destination. (If not exist-->ActiveMQ create once)
		Destination destination = (Destination) ctx.lookup("dynamicQueues/tranlong");
		// get connection using credential
		con = factory.createConnection("admin", "admin");
		// connect to MOM
		con.start();
		// create session
		session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
		// create producer
		producer = session.createProducer(destination);

		JFrame f = new JFrame("The Twilight Zone");

		// set size and location of frame
		f.setSize(600, 400);
		f.setLocation(100, 150);

		// make sure it quits when x is clicked
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel labelM = new JLabel("Enter message: ");
		labelM.setBounds(50, 50, 200, 30);
		motto1 = new JTextField();

		// set size of the text box
		motto1.setBounds(50, 100, 200, 30);

		// add button
		JButton btnSubmit = new JButton("Send");
		btnSubmit.setBounds(95, 150, 100, 30);

		// add elements to the frame
		f.add(labelM);
		f.add(motto1);
		f.add(btnSubmit);
		f.setLayout(null);
		f.setVisible(true);

		btnSubmit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String text = motto1.getText().toString();

				// create text message
				try {
					msg = session.createTextMessage(text);
					producer.send(msg);
					producer.send(msg);

					System.out.println("Finished....");
				} catch (JMSException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

	}
}