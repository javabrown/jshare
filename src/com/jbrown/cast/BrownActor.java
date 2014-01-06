package com.jbrown.cast;

import static java.awt.event.InputEvent.BUTTON1_MASK;
import static java.awt.event.InputEvent.BUTTON2_MASK;
 
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class BrownActor extends Robot implements BrownActorI {
    List<String> _timeline;
    String _ip;
   
    public BrownActor() throws AWTException {
        _timeline = new ArrayList<String>();
        _ip = getIpAddress();
    }

    private void performRemoteAct(String actString) throws Exception {
        String[] actionArr = actString.split("-");System.out.println(actString);
        Action action = Action.getInstance(actionArr[0]);
        int x = (int)Double.parseDouble(actionArr[1]);
        int y = (int)Double.parseDouble(actionArr[2]);
        int seed = Integer.parseInt(actionArr[3]);
        //String date = actionArr[4];
        double clientWidth = Double.parseDouble(actionArr[5]);
        double clientHeight = Double.parseDouble(actionArr[6]);
           
        double screenHeight =
            java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        double screenWidth =
            java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
       
        double c_x = (screenWidth /  clientWidth) * x;
        double c_y = (screenHeight /  clientHeight) * y;

       
        action.trigger(this, new BrownSpot(c_x, c_y, seed));
    }

    @Override
    public void remoteListener() {
        String url = "http://happy.javabrown.com/user/services/pipe/pop-event.php?ip="
                + _ip;

        try {
            String remoteStr = new BrownShooter().get(url);
            if (remoteStr != null && remoteStr.length() > 0 && !_timeline.contains(remoteStr)) {
                performRemoteAct(remoteStr);
                _timeline.add(remoteStr);
            }

            if (_timeline.size() > 1000) {
                _timeline.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   
    @Override
    public String getIpAddress() {
        try {
            InetAddress IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
       
        return "";
    }
   
    @Override
    public void click(BrownSpot spot) {
        this.mouseMove(spot.getX(), spot.getY());
        this.mousePress(BUTTON1_MASK);
        this.mouseRelease(BUTTON1_MASK);
    }

    @Override
    public void doubleClick(BrownSpot spot) {
        click(spot);
        click(spot);
    }

    @Override
    public void rightClick(BrownSpot spot) {
        this.mouseMove(spot.getX(), spot.getY());
        this.mousePress(BUTTON2_MASK);
        this.mouseRelease(BUTTON2_MASK);
    }

    @Override
    public void keyPress(BrownSpot spot) {
        // TODO Auto-generated method stub

    }

    @Override
    public BrownGrabber record(BrownSpot spot) {
        return new BrownGrabber(this.createScreenCapture(new Rectangle(spot)),
                0, 0, -1, -1, false);
    }
}
