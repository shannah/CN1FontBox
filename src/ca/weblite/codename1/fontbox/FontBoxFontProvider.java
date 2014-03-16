/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.weblite.codename1.fontbox;

import com.codename1.io.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import ca.weblite.pisces.Font;

/**
 * A font provider for the FontBox library that should be registered with
 * ca.weblite.pisces.Font so that it can make use of true-type fonts loaded through
 * fontbox.  
 * 
 * Currently this only provides a facade for truetype fonts even though fontbox
 * likely has support for many more kinds.  Additional types would probably require
 * a little work to be able to generate Glyphs in teh correct format.
 * @author shannah
 */
public class FontBoxFontProvider implements Font.FontProvider{

    Map<String, TrueTypeFont> ttfMap = new HashMap<String, TrueTypeFont>();
    private static FontBoxFontProvider defaultProvider = null;
    public static FontBoxFontProvider getDefaultProvider(){
        if ( defaultProvider == null ){
            
            defaultProvider = new FontBoxFontProvider();
            ca.weblite.pisces.Font.addProvider(defaultProvider);
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
    public Font getFont(String name, float size) {
        if ( ttfMap.containsKey(name)){
            return ttfMap.get(name).getFont(name, size);
        } else {
            return null;
        }
    }
    
    /**
     * Loads a True Type font from an input stream.  This must be called before
     * the getFont() method will be able to retrieve the font.
     * @param name
     * @param is
     * @throws IOException 
     */
    public void loadTTF(String name, InputStream is) throws IOException{
        TTFParser parser = new TTFParser();
        TrueTypeFont font = parser.parseTTF(is);
        ttfMap.put(name, font);
        
    }
    
    public void clearFontCache(){
        ttfMap.clear();
        
    }
    
}
