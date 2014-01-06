package com.jbrown.cast;
 
interface BrownActorI {
    void click(BrownSpot spot);

    void doubleClick(BrownSpot spot);

    void rightClick(BrownSpot spot);

    void keyPress(BrownSpot spot);

    void remoteListener();

    BrownGrabber record(BrownSpot spot);
    
    String getIpAddress();
}
