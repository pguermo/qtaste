package com.qspin.qtaste.sutuidemo;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public final class DialogPanel extends JPanel {

	DialogPanel()
	{
		super();
		setName(COMPONENT_NAME);
		
		genUI();
	}
	
	private void genUI()
	{
		prepareComponent();
		setLayout(new GridLayout(NUMBER_OF_COMPONENT,2, 5, 5));
		add(mStart);
	}

	private void prepareComponent()
	{
		mStart = new JButton("Start");
		mStart.setName("START_BUTTON");
		mStart.addActionListener(new StartDialogProcess());
	}
	
	JButton mStart;
	
	private static final int NUMBER_OF_COMPONENT = 1;
	public static final String COMPONENT_NAME = "DIALOG_PANEL";
	
	private static class StartDialogProcess implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String result = JOptionPane.showInputDialog(null, 
					"How many popup do you want to display",
					"Number of popup?",
					JOptionPane.QUESTION_MESSAGE);
			if ( result != null )
			{
				int numberOfPopup = 0;
				try {
					numberOfPopup = Integer.parseInt(result);
				} catch (NumberFormatException ex) {
					return;
				}
				
				int sureAnswer = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to display " + numberOfPopup + " popup(s)?", 
						"Are you sure?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				
				if ( sureAnswer == JOptionPane.YES_OPTION )
				{
					for (int i=0; i<numberOfPopup; i++)
					{
						final String msg = "Popup number " + (i+1);
						Thread t = new Thread(new Runnable() {
							
							@Override
							public void run() {
								JOptionPane.showMessageDialog(null, msg, "POPUP", JOptionPane.INFORMATION_MESSAGE);
							}
						});
						t.start();
					}
				}
			}
		}
	}
}