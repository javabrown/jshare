import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import com.jbrown.cast.BrownControl;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServerViewer extends JFrame {
    BrownControl _brownControl;
    JTextArea _eventLog;

    public ServerViewer() throws Exception {
        setSize(850, 550);
        setDefaultCloseOperation(3);
        _brownControl = new BrownControl();
        _eventLog = new JTextArea();
        getContentPane().setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        		_brownControl  , new JScrollPane(_eventLog));
       
        splitPane.setResizeWeight(0.7);
        
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
        
 
        setIcon();
        setVisible(true);
        
        int state = this.getExtendedState();  
        state = state | this.ICONIFIED;  
        this.setExtendedState(state); 
    }

  private void setIcon() {
    try {
      List<Image> icons = new ArrayList<Image>();
  
      InputStream inputStream = this.getClass().getResourceAsStream(
          "brown-logo.png");
      BufferedInputStream in = new BufferedInputStream(inputStream);
      Image image = ImageIO.read(in);
 
      icons.add(image);
      icons.add(image);
 
      this.setIconImages(icons);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
    
    public static void main(String[] args) throws Exception {
        ServerViewer viewer = new ServerViewer();
    }
    
}
