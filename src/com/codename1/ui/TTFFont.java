/*
 * Pisces User
 * Copyright (C) 2009 John Pritchard
 * Codename One Modifications Copyright (C) 2013 Steve Hannah
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.  The copyright
 * holders designate particular file as subject to the "Classpath"
 * exception as provided in the LICENSE file that accompanied this
 * code.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package com.codename1.ui;


import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.ui.Display;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.GeneralPath;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
//import java.net.URL;

/**
 * Encapsulates a truetype font loaded through font box.  TTFFont can be used interchangeably with
 * regular "native" fonts in Codename One components.  Rather than relying native font rendering, 
 * this uses the CN1 shapes API ({@link com.codename1.ui.geom.GeneralPath}) to render the individual
 * glyphs.
 * 
 * UnsupportedOperationException on drawing.
 * 
 * @see ca.weblite.pisces.f.Psf2
 */
public class TTFFont
    extends CustomFont
    implements Iterable<TTFFont.Glyph>
{
    
    private static List<FontProvider> providers = new ArrayList<FontProvider>();
    private static Image dummyImg;
    private float pixelSize;
    
    private float hscale = 1f;
    private float vscale = 1f;
    private Stroke stroke = null;
    private Integer strokeColor = null;
    private boolean filled = true;
    private Integer fillColor = null;
    private boolean antialias = true;
    
    /**
     * A dummy image that is passed to the CustomFont constructor.
     * @return 
     */
    private static Image getDummyImage() {
        if (dummyImg == null) {
            dummyImg = Image.createImage(1, 1);
            
        }
        return dummyImg;
    }
    
    
    
    /**
     * Adds a font provider that can be used to load fonts.
     * @param provider 
     */
    static void addProvider(FontProvider provider){
        providers.add(provider);
    }
    
    /**
     * Removes a font provider.
     * @param provider 
     */
    static void removeProvider(FontProvider provider){
        providers.remove(provider);
    }
    
    /**
     * Gets a font with the specified name and size.  If the font hasn't been loaded yet,
     * this will try to load the font from the resources at "/{name}.ttf".  
     * @param name The name of the font to load.
     * @param size The size (in pixels).
     * @return The font or null if it could not be found.
     */
    public static TTFFont getFont(String name, float size){
        TTFFont out = null;
        if (providers.isEmpty()) {
            FontBoxFontProvider.getDefaultProvider();
        }
        for ( FontProvider provider: providers){
            TTFFont src = provider.getFont(name, size);
            if ( src != null ){
                out = new TTFFont(name, src.collection);
                out.ascent = src.ascent;
                out.descent = src.descent;
                out.provider = provider;
                out.pixelSize = size;
                return out;
            }
        }
        try {
            out = new TTFFont(name);
            out.pixelSize = size;
        } catch ( IOException ioe){
            Log.e(ioe);
        }
        return out;
    }
    
    /**
     * Loads a font from resources at given resource path.  
     * @param name The name of the font.
     * @param resourcePath The resource path to load the font from.
     * @return The font or a dummy, empty font if not found.
     * @throws IOException 
     */
    public static TTFFont createFont(String name, String resourcePath) throws IOException {
        FontBoxFontProvider.getDefaultProvider().loadTTF(name, Display.getInstance().getResourceAsStream(null, resourcePath));
        return getFont(name, 12);
    }
    
    /**
     * Loads a font from an InputStream
     * @param name The name of the font.
     * @param inputStream InputStream with TTF file contents.
     * @return The font.
     * @throws IOException 
     */
    public static TTFFont createFont(String name, InputStream inputStream) throws IOException {
        FontBoxFontProvider.getDefaultProvider().loadTTF(name, inputStream);
        return getFont(name, 12);
    }
    
    /**
     * Loads a font from storage if available.  If not found, downloads font from
     * url and saves it to storage.
     * @param name The name of the font.  Used for looking up later via {@link #getFont(java.lang.String, float) }
     * @param storageKey The storage key where font should be stored.
     * @param url The url where the font should be downloaded.
     * @throws IOException 
     */
    public static TTFFont createFontToStorage(String name, String storageKey, String url) throws IOException {
        FontBoxFontProvider.getDefaultProvider().createFontToStorage(name, storageKey, url);
        return getFont(name, 12);
        
    }
    
    /**
     * Loads a font from the file system if available.  If not found, downloads font from
     * url and saves it to the file system.
     * @param name The name of the font.  Used for looking up later via {@link #getFont(java.lang.String, float) }
     * @param path The file system path where the font should be saved.
     * @param url The url where the font may be downloaded.
     * @throws IOException 
     */
    public static TTFFont createFontToFileSystem(String name, String path, String url) throws IOException {
        
        FontBoxFontProvider.getDefaultProvider().createFontToFileSystem(name, path, url);
        return getFont(name, 12);
    }

    /**
     * Checks whether the font should be rendered antialiased.
     * @return the antialias
     */
    private boolean isAntialias() {
        return antialias;
    }

    /**
     * Sets whether the font should be rendered antialiased.
     * @param antialias the antialias to set
     */
    private void setAntialias(boolean antialias) {
        this.antialias = antialias;
    }
    
    /**
     * Vector or Bitmap
     */
    public enum Kind {

        Draw, Blit
    }
    
    
    /**
     * Interface that can be implemented by any class that wishes to provide
     * fonts to Pisces.  
     */
    static interface FontProvider {
        
        /**
         * Returns the specified font in the given size.
         * @param name The name of the font.
         * @param size The size of the font (in pixels);
         * @return 
         */
        public TTFFont getFont(String name, float size);
    }

    /**
     * 
     */
    public interface Glyph {

        /**
         * The implementor defines a public constructor for no
         * arguments.  An instance is created with this constructor,
         * and then initialized via the read method.
         */
        public interface Collection
            extends java.lang.Iterable<Glyph>
        {
            public TTFFont.Kind getKind();

            public Glyph getGlyph(char id);

            public int getMaxWidth();

            public int getMaxHeight();

            
            public void read(InputStream in) throws IOException;
        }

        public char getId();

        public int getWidth();

        public int getHeight();
        /**
         * Blit the glyph in its ideal box as defined for all glyphs
         * in the collection.  The argument coordinates locate the
         * glyph box at its top left location.  
         * 
         * Glyph coordinates are not relative to a font baseline.
         * 
         * Bitmap fonts are typically blitted.  
         * 
         * Vector fonts will throw an UnsupportedOperationException on
         * blitting.
         * 
         * In future the rendering engine may expose methods to draw a
         * bitmap font with transformations and antialiasing.
         */
        public Glyph blit(Graphics g, int x, int y, float op);
        /**
         * Draw the glyph.
         * 
         * Stroke or vector fonts must be drawn.  
         * 
         * Bitmap fonts may throw an UnsupportedOperationException on
         * drawing.
         * @param g
         * @param x
         * @param y
         * @param op
         * @return 
         */
        public Glyph draw(Graphics g, int x, int y, float op);
        public Glyph stroke(Graphics g, int x, int y, float op, Stroke stroke);
        public Glyph fill(Graphics g, int x, int y, float op);
        public Glyph draw(GeneralPath sink, int x, int y, float op);
        
       
    }
    
    
    
    

    /**
     * Map from font (file) name extension to implementation class.
     */
    public enum Type {

        PSFU(Object.class);


        public final Class<TTFFont.Glyph.Collection> jclass;


        Type(Class jclass){
            this.jclass = jclass;
        }

        public TTFFont.Glyph.Collection newInstance(){
            //throw new RuntimeException("newInstance not supported yet");
           try {
                return (TTFFont.Glyph.Collection)this.jclass.newInstance();
            }
           catch (InstantiationException exc){
                throw new RuntimeException(exc.getMessage());
            }
            catch (IllegalAccessException exc){
              throw new RuntimeException(exc.getMessage());
            }
        }


        public final static TTFFont.Type Of(String name){
            int idx = name.lastIndexOf('.');
            if (-1 == idx)
                throw new IllegalArgumentException("Font file name missing extension");
            else
                return Type.valueOf(name.substring(idx+1).toUpperCase());
        }
        public final static TTFFont.Glyph.Collection Create(String name)
            throws IOException
        {
            TTFFont.Type type = Type.Of(name);
            TTFFont.Glyph.Collection collection = type.newInstance();
            /*
            NativeFontLoader loader = (NativeFontLoader)NativeLookup.create(NativeFontLoader.class);
            if ( loader.isSupported() ){
                byte[] data = loader.getFontData(name);
                InputStream is = new ByteArrayInputStream(data);
                try {
                    collection.read(is);
                } finally {
                    is.close();
                }
                return collection;
                
            }
            */
            /*
             */
            String url = "/"+name;
            //if ('/' == name.charAt(0))
            //    url = TTFFont.class.getResource(name);
            //else
            //    url = TTFFont.class.getResource("/"+name);
            ///*
            // */
            if (null == url)
                throw new IllegalArgumentException("Font file not found "+name);
            else {
                InputStream in = Display.getInstance().getResourceAsStream(TTFFont.class, url);
                try {
                    collection.read(in);
                }
                finally {
                    in.close();
                }
                return collection;
            }
        }
    }


    private String name;
    private FontProvider provider;

    private TTFFont.Glyph.Collection collection;
    private int ascent=0;
    private int descent=0;
    
    public int getAscent(){
        int strokeWidth = stroke == null ? 0 : (int)(2 * stroke.getLineWidth());
        return ((int)(ascent * vscale)) + strokeWidth;
    }

    @Override
    public int getHeight() {
        int strokeWidth = stroke == null ? 0 : (int)(stroke.getLineWidth());
        return ((int)(getMaxHeight() * vscale)) + strokeWidth;
    }

    @Override
    public Object getNativeFont() {
        return null;
    }

    @Override
    public boolean isTTFNativeFont() {
        return true;
    }
    
    
    
    
    
    
    public int getDescent(){
        return descent;
    }
    
    public void setAscent(int ascent){
        this.ascent = ascent;
    }
    
    public void setDescent(int descent){
        this.descent = descent;
    }

    
    public TTFFont(String name, TTFFont.Glyph.Collection collection){
        super(getDummyImage(), new int[0], new int[0], "");
        this.name = name;
        this.collection = collection;
    }

    @Override
    public int charWidth(char ch) {
        int strokeWidth = stroke == null ? 0 : (int)(2 * stroke.getLineWidth());
        Glyph glyph = getGlyph(ch);
        if (glyph == null) {
            return ((int)(getMaxWidth() * hscale)) + strokeWidth;
        }
        return ((int)(glyph.getWidth() * hscale)) + strokeWidth;
    }

    @Override
    public int charsWidth(char[] ch, int offset, int length) {
        int strokeWidth = stroke == null ? 0 : (int)(2 * stroke.getLineWidth());
        int w = 0;
        for (int i=0; i<length; i++) {
            Glyph glyph =  getGlyph(ch[i+offset]);
            if (glyph == null) {
                w += getMaxWidth();
            } else {
                w += glyph.getWidth();
            }
        }
        return ((int)(w*hscale)) + strokeWidth;
    }

    @Override
    public Font derive(float sizePixels, int weight) {
        TTFFont out = deriveFont(sizePixels);
        
        return out;
    }

    @Override
    void drawChar(Graphics g, char character, int x, int y) {
        draw(g, ""+character, x, y, 1f);
    }

    @Override
    void drawChars(Graphics g, char[] data, int offset, int length, int x, int y) {
        draw(g, new String(data, offset, length), x, y, 1f );
    }

    @Override
    void drawString(Graphics g, String str, int x, int y) {
        draw(g, str, x, y, 1f);
    }

    @Override
    public void addContrast(byte value) {
        
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TTFFont) {
            TTFFont f = (TTFFont)o;
            return f.name.equals(name) && f.pixelSize == pixelSize && f.antialias==antialias && f.hscale==hscale && f.vscale==vscale && f.stroke == stroke && f.strokeColor == strokeColor && f.fillColor == fillColor && f.filled == this.filled;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Float.floatToIntBits(this.pixelSize);
        hash = 41 * hash + Float.floatToIntBits(this.hscale);
        hash = 41 * hash + Float.floatToIntBits(this.vscale);
        hash = 41 * hash + (this.stroke != null ? this.stroke.hashCode() : 0);
        hash = 41 * hash + (this.strokeColor != null ? this.strokeColor.hashCode() : 0);
        hash = 41 * hash + (this.filled ? 1 : 0);
        hash = 41 * hash + (this.fillColor != null ? this.fillColor.hashCode() : 0);
        hash = 41 * hash + (this.antialias ? 1 : 0);
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    
    
    
    
    
    /**
     * @param name Font file name, for example "sun12x22.psfu".
     */
    public TTFFont(String name)
        throws IOException
    {
        super(getDummyImage(), new int[0], new int[0], "");
        if (null != name && 0 < name.length()){
            this.name = name;
            this.collection = TTFFont.Type.Create(name);
        }
        else
            throw new IllegalArgumentException();
    }


    public TTFFont deriveFont(float size){
        if ( provider != null ){
            TTFFont src = provider.getFont(name, size);
            TTFFont out =  new TTFFont(name, src.collection);
            
            out.setAscent(src.ascent);
            out.setDescent(src.descent);
            
            
                    
            if ( out != null ){
                out.provider = provider;
                out.pixelSize = size;
                out.hscale = hscale;
                out.vscale = vscale;
                
                out.stroke = stroke;
                out.fillColor = fillColor;
                out.strokeColor = strokeColor;
                out.antialias = antialias;
                out.filled = filled;
            
            
            }
            return out;
        } 
        return null;
    }
    
    public TTFFont deriveScaled(float hScale, float vScale) {
        return deriveFont(pixelSize, hScale, vScale, stroke, fillColor, strokeColor, antialias, filled);
    }
    
    public TTFFont deriveHorizontalScaled(float hscale) {
        return deriveFont(pixelSize, hscale, vscale, stroke, fillColor, strokeColor, antialias, filled);
    }
    
    public TTFFont deriveVerticalScaled(float vscale) {
        return deriveFont(pixelSize, hscale, vscale, stroke, fillColor, strokeColor, antialias, filled);
    }
    
    public TTFFont deriveStroked(Stroke stroke, Integer strokeColor) {
        return deriveFont(pixelSize, hscale, vscale, stroke, fillColor, strokeColor, antialias, filled);
    }
    
    public TTFFont deriveFilled(boolean filled, Integer fillColor) {
        return deriveFont(pixelSize, hscale, vscale, stroke, fillColor, strokeColor, antialias, filled);
    }
    
    public TTFFont deriveAntialias(boolean antialias) {
        return deriveFont(pixelSize, hscale, vscale, stroke, fillColor, strokeColor, antialias, filled);
    }
    
    public TTFFont deriveFont(float size, float hScale, float vScale, Stroke stroke, Integer fillColor, Integer strokeColor, boolean antialias, boolean filled){
        if ( provider != null ){
            TTFFont src = provider.getFont(name, size);
            TTFFont out =  new TTFFont(name, src.collection);
            
            out.setAscent(src.ascent);
            out.setDescent(src.descent);
            //TTFFont out = provider.getFont(name, size);
              
            if ( out != null ){
                out.pixelSize = size;
                out.hscale = hScale;
                out.vscale = vScale;
                out.stroke = stroke;
                out.fillColor = fillColor;
                out.strokeColor = strokeColor;
                out.antialias = antialias;    
                out.provider = provider;
            }
            return out;
        } 
        return null;
    }
    
    
    public Kind getKind(){
        return this.collection.getKind();
    }
    public Glyph getGlyph(char id){
        return this.collection.getGlyph(id);
    }
    /**
     * Bitmap font
     */
    private TTFFont blit(Graphics g, String string, int x, int y, float op){
        boolean oldAntialiased = g.isAntiAliased();
        //if (g.isAntiAliasedText()) {
            g.setAntiAliased(true);
        //}
        if (null != string){
            char[] cary = string.toCharArray();
            int clen = cary.length;
            if (0 < clen){
                char ch;
                int px = x;
                int py = y;
                Glyph glyph;
                for (int cc = 0; cc < clen; cc++){
                    ch = cary[cc];
                    switch (ch){
                    case 0x20:
                        glyph = this.collection.getGlyph(ch);
                        if (null != glyph)
                            px += glyph.getWidth();
                        else
                            px += this.collection.getMaxWidth();
                        break;
                    case 0x0A:
                        px = x;
                        py += this.collection.getMaxHeight();
                        break;
                    case 0x0D:
                        px = x;
                        break;
                    default:
                        glyph = this.collection.getGlyph(ch);

                        if (null != glyph){
                            glyph.blit(g,px,py,op);

                            px += glyph.getWidth();
                        }
                        else
                            px += this.collection.getMaxWidth();
                        break;
                    }
                }
            }
        }
        g.setAntiAliased(oldAntialiased);
        return this;
    }
    
    public TTFFont draw(GeneralPath sink, String string, int x, int y, float op){
        
        return draw(sink, string, x, y, op, hscale, vscale);
    }
    public TTFFont draw(GeneralPath sink, String string, int x, int y, float op, float hscale, float vscale){
        if (hscale != 1f || vscale != 1f) {
            GeneralPath scaledPath = new GeneralPath();
            draw(scaledPath, string, x, y, op, 1f, 1f);
            Transform t = Transform.makeTranslation(x, y);
            t.scale(hscale, vscale);
            t.translate(-x, -y);
            scaledPath.transform(t);
            sink.append(scaledPath, false);
            return this;
        }
        if (null != string){
            char[] cary = string.toCharArray();
            int clen = cary.length;
            if (0 < clen){
                char ch;
                int px = x;
                int py = y;
                
                Glyph glyph;
                for (int cc = 0; cc < clen; cc++){
                    ch = cary[cc];
                    switch (ch){
                    case 0x20:
                        glyph = this.collection.getGlyph(ch);
                        if (null != glyph)
                            px += glyph.getWidth();
                        else
                            px += this.collection.getMaxWidth();
                        break;
                    case 0x0A:
                        px = x;
                        py += this.collection.getMaxHeight();
                        break;
                    case 0x0D:
                        px = x;
                        break;
                    default:
                        glyph = this.collection.getGlyph(ch);

                        if (null != glyph){
                            
                            glyph.draw(sink,px,py,op);
                            px += glyph.getWidth();
                        }
                        else
                            px += this.collection.getMaxWidth();
                        break;
                    }
                }
            }
        }
        return this;
    }
    
    
    
    
    /**
     * Vector font
     */
    private TTFFont draw(Graphics g, String string, int x, int y, float op){
        boolean oldAntialiased = g.isAntiAliased();
        //if (g.isAntiAliasedText()) {
        g.setAntiAliased(antialias);
        
        //}
        
        int strokeWidth = stroke == null ? 0 : (int)(stroke.getLineWidth());
        x += strokeWidth;
        y += strokeWidth;
        GeneralPath strPath = new GeneralPath();
        draw(strPath, string, x, y, op);
        int oldColor = g.getColor();
        if (filled && fillColor != null) {
            g.setColor(fillColor);
        }
        if (filled) {
            g.fillShape(strPath);
        }
        
        
        if (stroke != null && strokeColor != null) {
            g.setColor(strokeColor);
        }
        if (stroke != null) {
            g.drawShape(strPath, stroke);
        }
        
        
        if (g.getColor() != oldColor) {
            g.setColor(oldColor);
        }
        
        
        g.setAntiAliased(oldAntialiased);
        return this;
        
    }
    private int getMaxWidth(){
        return this.collection.getMaxWidth();
    }
    private int getMaxHeight(){
        return this.collection.getMaxHeight();
    }
    public java.util.Iterator<TTFFont.Glyph> iterator(){
        return this.collection.iterator();
    }

    @Override
    public float getPixelSize() {
        return pixelSize;
    }

    @Override
    public int substringWidth(String str, int offset, int len) {
        return this.charsWidth(str.toCharArray(), offset, len);
    }

    @Override
    public int stringWidth(String str) {
        return this.charsWidth(str.toCharArray(), 0, str.length());
    }

   
    
    
    
    
}
