import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import com.jbrown.cast.BrownControl;
import java.awt.BorderLayout;

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
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        ServerViewer viewer = new ServerViewer();
    }
}
