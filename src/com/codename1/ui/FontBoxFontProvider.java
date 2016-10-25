/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.codename1.ui;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.io.Storage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;


/**
 * A font provider for the FontBox library that should be registered with
 ca.weblite.pisces.TTFFont so that it can make use of true-type fonts loaded through
 fontbox.  
 * 
 * Currently this only provides a facade for truetype fonts even though fontbox
 * likely has support for many more kinds.  Additional types would probably require
 * a little work to be able to generate Glyphs in teh correct format.
 * @author shannah
 */
public class FontBoxFontProvider implements TTFFont.FontProvider{

    Map<String, TrueTypeFont> ttfMap = new HashMap<String, TrueTypeFont>();
    private static FontBoxFontProvider defaultProvider = null;
    public static FontBoxFontProvider getDefaultProvider(){
        if ( defaultProvider == null ){
            
            defaultProvider = new FontBoxFontProvider();
            TTFFont.addProvider(defaultProvider);
        }
        return defaultProvider;
    }
    
    /**
     * Returns a font that has been previously loaded into the provider in the 
     * specified pixel size.
     * @param name The name of the font (should match name provided by loadTTF()
     * when the font was loaded.
     * @param size
     * @return The specified font or null if it hasn't been loaded.
     */
    public TTFFont getFont(String name, float size) {
        if ( ttfMap.containsKey(name)){
            return ttfMap.get(name).getFont(name, size);
        } else {
            try {
                InputStream fontStream = Display.getInstance().getResourceAsStream(null, "/"+name+".ttf");
                if (fontStream != null) {
                    loadTTF(name, fontStream);
                    if ( ttfMap.containsKey(name)){
                        return ttfMap.get(name).getFont(name, size);
                    }
                }
            } catch (Exception ex){}
            return null;
        }
    }
    
    /**
     * Loads a True Type font from an input stream.  This must be called before
 the getFont() method will be able to retrieve the font.
     * @param name The name of the font.  Used for looking up later via {@link #getFont(java.lang.String, float) }
     * @param is InputStream with the .ttf file contents.
     * @throws IOException 
     */
    public void loadTTF(String name, InputStream is) throws IOException{
        if (ttfMap.containsKey(name)) {
            return;
        }
        TTFParser parser = new TTFParser();
        TrueTypeFont font = parser.parseTTF(is);
        ttfMap.put(name, font);
        
    }
    
    /**
     * Loads a font from storage if available.  If not found, downloads font from
     * url and saves it to storage.
     * @param name The name of the font.  Used for looking up later via {@link #getFont(java.lang.String, float) }
     * @param storageKey The storage key where font should be stored.
     * @param url The url where the font should be downloaded.
     * @throws IOException 
     */
    public void createFontToStorage(String name, String storageKey, String url) throws IOException {
        if (ttfMap.containsKey(name)) {
            return;
        }
        Storage s = Storage.getInstance();
        if (s.exists(storageKey)) {
            loadTTF(name, s.createInputStream(storageKey));
            return;
        }
        ConnectionRequest req = new ConnectionRequest();
        req.setFailSilently(true);
        req.setHttpMethod("GET");
        req.setPost(false);
        req.setDestinationStorage(storageKey);
        req.setUrl(url);
        NetworkManager.getInstance().addToQueueAndWait(req);
        if (s.exists(storageKey)) {
            loadTTF(name, s.createInputStream(storageKey));
            return;
        }
        
    }
    
    /**
     * Loads a font from the file system if available.  If not found, downloads font from
     * url and saves it to the file system.
     * @param name The name of the font.  Used for looking up later via {@link #getFont(java.lang.String, float) }
     * @param path The file system path where the font should be saved.
     * @param url The url where the font may be downloaded.
     * @throws IOException 
     */
    public void createFontToFileSystem(String name, String path, String url) throws IOException {
        FileSystemStorage s = FileSystemStorage.getInstance();
        if (s.exists(path)) {
            loadTTF(name,s.openInputStream(path));
            return;
        }
        ConnectionRequest req = new ConnectionRequest();
        req.setFailSilently(true);
        req.setHttpMethod("GET");
        req.setPost(false);
        req.setDestinationFile(path);
        req.setUrl(url);
        NetworkManager.getInstance().addToQueueAndWait(req);
        if (s.exists(path)) {
            loadTTF(name, s.openInputStream(path));
            return;
        }
        
    }
    
    /**
     * Clears the font cache.
     */
    public void clearFontCache(){
        ttfMap.clear();
        
    }
    
}
