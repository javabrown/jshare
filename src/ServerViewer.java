import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import static java.awt.event.InputEvent.*;

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

class BrownControl extends JPanel implements Runnable, ActionListener {
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

class BrownSetting extends JPanel{
   JSlider pushSpeed;
   JSlider popSpeed;
   
   public BrownSetting(){
	   pushSpeed = new JSlider(1, 10, 5);
	   popSpeed = new JSlider(1, 10, 5);
	   
	   JPanel jp = new JPanel();
	   jp.setLayout(new GridLayout(3, 2));
	   jp.add(new JLabel("Transmission Rate"));
	   jp.add(pushSpeed);
	   
	   jp.add(new JLabel("Listener Rate"));
	   jp.add(popSpeed);
	   //jp.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	   //jp.add(new JButton("Apply"));
	   
	   this.setLayout(new BorderLayout());
	   this.add(jp, BorderLayout.CENTER);
   }
}

// class BrownShot {
// BrownGrabber
// public BrownShot(){
// if (isPixcelMatched) {
// // System.out.println("NO Pixel change noticed+" + new Date());
// } else {
// System.out.println("Pixel change noticed!!!");
// _visibleImage = _newPixels.getImage();
// _currentPixels = _newPixels;
// post();
// }
// }
// }

class BrownGrabber extends PixelGrabber {
    private BufferedImage _image;

    public BrownGrabber(BufferedImage image, int x, int y, int w, int h,
            boolean forceRGB) {
        super(image, x, y, w, h, forceRGB);
        _image = image;
    }

    public BufferedImage getImage() {
        return _image;
    }

    public boolean isPixcelsMatch(BrownGrabber newPixels) {
        if (newPixels == null) {
            return false;
        }

        try {
            int[] data1 = null;
            if (this.grabPixels()) {
                int width = this.getWidth();
                int height = this.getHeight();
                data1 = new int[width * height];
                data1 = (int[]) this.getPixels();
                Arrays.sort(data1);
            }

            int[] data2 = null;
            if (newPixels.grabPixels()) {
                int width = newPixels.getWidth();
                int height = newPixels.getHeight();
                data2 = new int[width * height];
                data2 = (int[]) newPixels.getPixels();
                Arrays.sort(data2);
            }

            return java.util.Arrays.equals(data1, data2);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}

class BrownSpot extends Dimension {
    private double _x;
    private double _y;
    private int _seed;

    public BrownSpot(Rectangle rectangle) {
        super(new Dimension((int) rectangle.getWidth(),
                (int) rectangle.getHeight()));
        _x =  rectangle.getX();
        _y =  rectangle.getY();
        _seed = -1;
    }

    public BrownSpot(double x, double y, int seed) {
        _x = x;
        _y = y;
        _seed = seed;
    }

    public int getX() {
        return (int)_x;
    }

    public int getY() {
        return (int)_y;
    }

    public int getSeed() {
        return _seed;
    }
}

interface BrownActorI {
    void click(BrownSpot spot);

    void doubleClick(BrownSpot spot);

    void rightClick(BrownSpot spot);

    void keyPress(BrownSpot spot);

    void remoteListener();

    BrownGrabber record(BrownSpot spot);
    
    String getIpAddress();
}

class BrownActor extends Robot implements BrownActorI {
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

// interface ActI{
// void perform(BrownActorI a, int x, int y, int seed);
// }
//
// abstract class Act implements ActI {
// protected BrownActorI __actor;
//
// public Act(){
//
// }
//
// @Override
// public void perform(BrownActorI a, int x, int y, int seed){
// __actor = a;
// //int old_x = java.awt.MouseInfo.getPointerInfo().getLocation().x;
// //int old_y = java.awt.MouseInfo.getPointerInfo().getLocation().y;
// __actor.mouseMove(x, y);
// this.performRemaining(seed);
// //__actor.getHandle().mouseMove(old_x, old_y);
// }
//
// abstract void performRemaining(int seed);
// }
//
// class LcAct extends Act {
// @Override
// public void performRemaining(int seed) {
// __actor.getHandle().mousePress(InputEvent.BUTTON1_MASK);
// __actor.getHandle().mousePress(InputEvent.BUTTON1_MASK);
// }
// }
//
// class RcAct extends Act {
// @Override
// public void performRemaining(int seed) {
// __actor.getHandle().mousePress(InputEvent.BUTTON2_MASK);
// __actor.getHandle().mousePress(InputEvent.BUTTON2_MASK);
// }
// }
//
// class KpAct extends Act {
// @Override
// public void performRemaining(int seed) {
// __actor.getHandle().keyPress(seed);
// __actor.getHandle().keyRelease(seed);
// }
// }

enum Action {
    DOUBLE_CLICK("Dc", "doubleClick"), RIGHT_CLICK("Rc", "rightClick"), LEFT_CLICK(
            "Lc", "click"), KEY_PRESSED("Kp", "keyPress");

    private final String _actionType;
    private final String _methodName;

    Action(String actionType, String methodName) {
        _actionType = actionType;
        _methodName = methodName;
    }

    public String getActionName() {
        return _actionType;
    }

    public String getMethodName() {
        return _methodName;
    }

    //
    // public Class getActionClass() throws ClassNotFoundException {
    // Class clazz = Class.forName(_actionType+"Act");
    // return clazz;
    // }

    public static Action getInstance(String actionName) throws Exception {
        for (Action type : Action.values()) {
            if (type.getActionName().equalsIgnoreCase(actionName)) {
                return type;
            }
        }

        throw new Exception("Unknown actionName type:" + actionName);
    }

    public boolean typeOf(Action action) {
        try {
            return getInstance(_actionType).equals(action);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // public ActI getActor() {
    // try {
    // Class clazz = this.getActionClass();
    // Constructor<?> constructor = clazz.getDeclaredConstructor();
    // return (ActI) constructor.newInstance();
    // } catch (Exception ex) {
    // System.err.println("No actor to work with remote-listener" + ex);
    // ex.printStackTrace();
    // }
    // return null;
    // }

    public void trigger(BrownActorI actor, BrownSpot spot) {
        java.lang.reflect.Method method;
        try {
            method = actor.getClass().getMethod(_methodName, BrownSpot.class);
            method.invoke(actor, spot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class BrownStage extends JPanel {
    Dimension _originalDimenetion;
    Dimension _visibleDimenetion;
    BufferedImage _visibleImage;
    BrownGrabber _currentPixels;// Lazy load
    BrownGrabber _newPixels;
    ExecutorService _threadExecutor;
    boolean _isEventOccured;
    BrownActorI _actor;
    boolean _isBroadcastOn;

    public BrownStage(BrownActorI actor) {
        try {
            _originalDimenetion = new DimensionUIResource(this.getWidth(),
                    this.getHeight());
            _visibleDimenetion = new DimensionUIResource(this.getWidth(),
                    this.getHeight());
            _threadExecutor = Executors.newFixedThreadPool(1);
            _actor = actor;
            _isBroadcastOn = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage image = record();
        if (image != null) {
            _originalDimenetion = new DimensionUIResource(image.getWidth(),
                    image.getHeight());
            _visibleDimenetion = new DimensionUIResource(this.getWidth(),
                    this.getHeight());

            Image img = image.getScaledInstance(
                    (int) _visibleDimenetion.getWidth(),
                    (int) _visibleDimenetion.getHeight(), Image.SCALE_SMOOTH);

            g.drawImage(img, 0, 0, this);
        }
    }

    public BufferedImage record() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        _newPixels = _actor.record(new BrownSpot(new Rectangle(screenSize)));

        boolean isPixcelMatched = _newPixels.isPixcelsMatch(_currentPixels);

        if (isPixcelMatched) {
            // System.out.println("NO Pixel change noticed+" + new Date());
        } else {
            System.out.println("Pixel change noticed!!!");
            _visibleImage = _newPixels.getImage();
            _currentPixels = _newPixels;
            if (_isBroadcastOn) {
                post();
            }
        }

        return _newPixels.getImage();
    }

    public void setBroadcastOn(boolean flag) {
        _isBroadcastOn = flag;
    }

    public synchronized boolean isBroadcastOn() {
        return _isBroadcastOn;
    }

    private void post() {
        Callable<String> callable = new Poster(
                "http://happy.javabrown.com/user/services/pipe/push.php",
                base64Image(), _actor.getIpAddress());
        try {
            Future<String> future = _threadExecutor.submit(callable);
            System.out.println(new Date() + ": " + future.get());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
        	new BrownShooter().get(
                    "http://happy.javabrown.com/user/services/pipe/push.php?unlink="+ _actor.getIpAddress());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Disconnected");
    }
    
    public String base64Image() {
        return encodeToString(_visibleImage, null);
    }

    public String encodeToString(BufferedImage image, String imageType) {
        try {

            if (imageType == null || imageType.trim().length() == 0) {
                imageType = "jpg";
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, imageType, baos);
            baos.flush();
            Base64 base = new Base64(false);
            String encodedImage = base.encodeToString(baos.toByteArray());
            baos.close();
            encodedImage = java.net.URLEncoder.encode(encodedImage,
                    "ISO-8859-1");
            return encodedImage;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // boolean isPixcelsMatch(PixelGrabber newPixels, PixelGrabber
    // currentPixels){
    // PixelGrabber grab1 = newPixels;
    // PixelGrabber grab2 = currentPixels;
    //
    // if (grab1 == null || grab2 == null) {
    // return false;
    // }
    //
    // try {
    // int[] data1 = null;
    //
    // if (grab1.grabPixels()) {
    // int width = grab1.getWidth();
    // int height = grab1.getHeight();
    // data1 = new int[width * height];
    // data1 = (int[]) grab1.getPixels();
    // Arrays.sort(data1);
    // }
    //
    // int[] data2 = null;
    //
    // if (grab2.grabPixels()) {
    // int width = grab2.getWidth();
    // int height = grab2.getHeight();
    // data2 = new int[width * height];
    // data2 = (int[]) grab2.getPixels();
    // Arrays.sort(data2);
    // }
    //
    // boolean isPixcelMatched = java.util.Arrays.equals(data1, data2);
    //
    // //if (!isPixcelMatched) {
    // // _currentPixels = _newPixels;
    // //}
    //
    // return isPixcelMatched;
    //
    // } catch (InterruptedException ex) {
    // ex.printStackTrace();
    // }
    //
    // return false;
    // }

    private void registerEvents() {
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                _isEventOccured = true;
                System.out.println("mouse");
            }
        });

        this.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                _isEventOccured = true;
                System.out.println("key");
            }
        });
    }
}

class Poster implements Callable<String> {
    String _url;
    String _imageBase64;
    String _ip;

    public Poster(String url, String imageBase64, String ip) {
        _url = url;
        _imageBase64 = imageBase64;
        _ip = ip;
    }

    @Override
    public String call() throws Exception {
        return new BrownShooter().postImage(_url, _imageBase64, _ip);
    }
}

// class Listener implements Callable<String>{
// Robot _actor;
// String _url;
//
// public Listener(Robot actor, String url){
// _actor = actor;
// _url = url;
// }
//
// @Override
// public String call() throws Exception {
// while(true){
// Thread.sleep(2000);
// String a = null;//new BrownShooter().get(_url);
// perform(a);
// }
// }
//
// void perform(String action){
// if(action!= null){
// String[] str = action.split("-");
//
// if(str[0].equals("click")){
// _actor.mouseMove(Integer.parseInt(str[1]),
// Integer.parseInt(str[2]));
// _actor.mousePress(InputEvent.BUTTON1_MASK );
// _actor.mouseRelease(InputEvent.BUTTON1_MASK );
// }
// }
// }
// }

class BrownShooter {
    private String _cookies;
    private HttpClient _client;
    private final String USER_AGENT = "Mozilla/5.0";

    public BrownShooter() {
        _client = new DefaultHttpClient();
        _cookies = null;
    }

    public String get(String url) throws Exception {
        HttpGet request = new HttpGet(url);

        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");

        HttpResponse response = _client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response
                .getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // set cookies
        setCookies(response.getFirstHeader("Set-Cookie") == null ? ""
                : response.getFirstHeader("Set-Cookie").toString());

        return result.toString();
    }

    public String postImage(String url, String imageBase64, String ip) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("imageData", imageBase64));
        nameValuePairs.add(new BasicNameValuePair("ip", ip));
        
        try {
            return this.post(url, nameValuePairs);
        } catch (Exception ex) {
            System.err.println("Post failed!!");
            ex.printStackTrace();
        }

        return null;
    }

    public String post(String url, List<NameValuePair> postParams)
            throws Exception {
        HttpPost post = new HttpPost(url);

        // add header
        // post.setHeader("Host", "accounts.google.com");
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Cookie", getCookies());
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Referer",
                "https://accounts.google.com/ServiceLoginAuth");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        if (postParams != null) {
            post.setEntity(new UrlEncodedFormEntity(postParams));
        }

        HttpResponse response = _client.execute(post);

        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("\nSending 'POST' request to URL : " + url);
        // System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response
                .getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    public List<NameValuePair> getFormParams0(String html, String username,
            String password) throws UnsupportedEncodingException {
        System.out.println("Extracting form's data...");
        return null;
    }

    public String getCookies() {
        return _cookies;
    }

    public void setCookies(String cookies) {
        _cookies = cookies;
    }
}

//class CoreShooter {
//	private static final String USER_AGENT = "Mozilla/5.0";
//	 
//	
//	public static String get(String url) throws Exception {
//		URL obj = new URL(url);
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
// 
//		// optional default is GET
//		con.setRequestMethod("GET");
// 
//		//add request header
//		con.setRequestProperty("User-Agent", USER_AGENT);
// 
//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);
// 
//		BufferedReader in = new BufferedReader(
//		        new InputStreamReader(con.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
// 
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		
//		in.close();
// 
//		//print result
//		return response.toString();
//	}
//
//	public static String post(String targetURL, String urlParameters) {
//		URL url;
//		HttpURLConnection connection = null;
//		try {
//			// Create connection
//			url = new URL(targetURL);
//			connection = (HttpURLConnection) url.openConnection();
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Content-Type",
//					"application/x-www-form-urlencoded");
//
//			connection.setRequestProperty("Content-Length",
//					"" + Integer.toString(urlParameters.getBytes().length));
//			connection.setRequestProperty("Content-Language", "en-US");
//
//			connection.setUseCaches(false);
//			connection.setDoInput(true);
//			connection.setDoOutput(true);
//
//			// Send request
//			DataOutputStream wr = new DataOutputStream(
//					connection.getOutputStream());
//			wr.writeBytes(urlParameters);
//			wr.flush();
//			wr.close();
//
//			// Get Response
//			InputStream is = connection.getInputStream();
//			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//			String line;
//			StringBuffer response = new StringBuffer();
//			while ((line = rd.readLine()) != null) {
//				response.append(line);
//				response.append('\r');
//			}
//			rd.close();
//			//--
//			System.out.println("\'POST' request sent to URL : " + targetURL);
//			System.out.println("Response =" +  response.toString());
//			//--
//			return response.toString();
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			return null;
//
//		} finally {
//
//			if (connection != null) {
//				connection.disconnect();
//			}
//		}
//	}
//}
