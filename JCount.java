// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JCount extends JPanel {

	private JButton starter;
	private JButton stoper;
	private JTextField textField;
	private Worker work;
	private JLabel label;

	public JCount(){
		work = null;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		starter = new JButton("Start");
		stoper = new JButton("Stop");
		label = new JLabel("");
		textField = new JTextField("", 10);
		add(textField);
		add(label);
		add(starter);
		add(stoper);
		initListeners();

	}

	private class Worker extends Thread {
		private int maxNumb;

		public Worker(int maxNumb){
			this.maxNumb = maxNumb;
		}

		public void run() {
			int curNumb = 0;
			label.setText("0");
			while(curNumb <= maxNumb){
				if(curNumb == maxNumb) {
					label.setText("" + maxNumb);
					break;
				}
				curNumb++;
				if(curNumb % 100 == 0){
					final String printAmount = "" + curNumb;
					label.setText("" + printAmount);
					try {
						sleep(100);
					} catch (InterruptedException e) {
						break;
					}
				}
			}

		}
	}

	private void initListeners(){
		starter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String txt = textField.getText();
				int amount = 1000000;
				if(txt.matches("\\d+")){
					amount = Integer.parseInt(txt);
				}
				if(work != null){
					work.interrupt();
				}
				work = new Worker(amount);
				work.start();
			}
		});

		stoper.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if(work != null){
					work.interrupt();
				}
			}
		});
	}


	private static void prepareGui() {
		JFrame frame = new JFrame();
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		for(int i = 0; i < 4; i++) {
			frame.add(new JCount());
		}
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}



	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				prepareGui();
			}
		});
	}
}

