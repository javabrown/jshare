package com.jbrown.cast;
 
import java.util.concurrent.Callable;

public class Poster implements Callable<String> {
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