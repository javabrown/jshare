package com.jbrown.cast;
 
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

import com.jbrown.cast.BrownSetting;

public class BrownControl extends JPanel implements Runnable, ActionListener {
    BrownStage _brownStage;
    JLabel _label;
    JTextField _phoneNumber;
    JButton _brodcast;
    JButton _disconnect;
    BrownActorI _actor;
    Thread _listener;

    final String BROADCAST = "Brodcast";
    final String DISCONNECT = "Disconnect";

    public BrownControl() throws Exception {
        this.setLayout(new BorderLayout());
        _actor = new BrownActor();
        initControllerComponent();

        _listener = new Thread(this);
    }

    void initControllerComponent() {
        _brownStage = new BrownStage(_actor);
        _label = new JLabel("IP Address");
        _phoneNumber = new JTextField(_actor.getIpAddress(), 10);
        _brodcast = new JButton(BROADCAST);
        _disconnect = new JButton(DISCONNECT);

        _brodcast.setActionCommand(BROADCAST);
        _disconnect.setActionCommand(DISCONNECT);

        _brodcast.addActionListener(this);
        _disconnect.addActionListener(this);
        _disconnect.setVisible(false);

        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout());

        jp.add(_label);
        jp.add(_phoneNumber);
        jp.add(_brodcast);
        jp.add(_disconnect);

        this.add(jp, BorderLayout.NORTH);
        //this.add(_brownStage, BorderLayout.CENTER);
         
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Stream", _brownStage);
        tabbedPane.addTab("Setting", new BrownSetting());
        
		JEditorPane jep = new JEditorPane();
		jep.setEditable(false);   
		jep.setEditorKit(new HTMLEditorKit());
		
        try {
          //jep.setText("<html><img src='file:///Users/rajakhan/Desktop/app-image.png' style='width:500;height:400'/></html>");
        	File doc = new File("files/about.html");
        	
        	jep.setPage(doc.toURI().toURL());
        }
        catch (Exception e) {
          jep.setContentType("text/html");
          jep.setText("<html>Could not load webpage</html>");
        }  
        
		
        tabbedPane.addTab("About", new JScrollPane(jep));//new JLabel(new ImageIcon(img)));
        this.add(tabbedPane, BorderLayout.CENTER);
    }
   
    public BrownActorI getBrownActor() {
        return _actor;
    }

    @Override
    public void run() {
        System.out.println(_brownStage.isBroadcastOn());
        while (_brownStage.isBroadcastOn()) {

            System.out.println(_brownStage.isBroadcastOn());
            _actor.remoteListener();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopBrodcast() {
        _brownStage.setBroadcastOn(false);
        _disconnect.setVisible(false);
        _brodcast.setVisible(true);
        _listener.stop();
        _brownStage.disconnect();
        
    }

    private void startBrodcast() {
        _brownStage.setBroadcastOn(true);
        _disconnect.setVisible(true);
        _brodcast.setVisible(false);
        _listener.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(BROADCAST)) {
            this.startBrodcast();
        }

        if (e.getActionCommand().equals(DISCONNECT)) {
            this.stopBrodcast();
        }
    }
}