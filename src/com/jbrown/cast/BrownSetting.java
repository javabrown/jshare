package com.jbrown.cast;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class BrownSetting extends JPanel {
	JSlider pushSpeed;
	JSlider popSpeed;

	public BrownSetting() {
		pushSpeed = new JSlider(1, 10, 5);
		popSpeed = new JSlider(1, 10, 5);

		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(3, 2));
		jp.add(new JLabel("Transmission Rate"));
		jp.add(pushSpeed);

		jp.add(new JLabel("Listener Rate"));
		jp.add(popSpeed);
		// jp.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		// jp.add(new JButton("Apply"));

		this.setLayout(new BorderLayout());
		this.add(jp, BorderLayout.CENTER);
	}
}