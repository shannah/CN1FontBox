/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.fontbox.ttf;




import com.codename1.ui.TTFFont;
import com.codename1.ui.TTFFont.Glyph;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Stroke;
import com.codename1.ui.Transform;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


import com.codename1.ui.geom.GeneralPath;

/**
 * A class to hold true type font information.
 * 
 * @author Ben Litchfield (ben@benlitchfield.com)
 * @version $Revision: 1.2 $
 */
public class TrueTypeFont 
{
    Map<String,com.codename1.ui.TTFFont> fontCache = new HashMap<String,com.codename1.ui.TTFFont>();
    GeneralPath[] glyphPaths;
    
    
    private float version; 
    
    private Map<String,TTFTable> tables = new HashMap<String,TTFTable>();
    
    private TTFDataStream data;
    
    /**
     * Constructor.  Clients should use the TTFParser to create a new TrueTypeFont object.
     * 
     * @param fontData The font data.
     */
    TrueTypeFont( TTFDataStream fontData )
    {
        data = fontData;
    }
    
    private void initPaths(){
        if ( glyphPaths == null ){
            int numGlyphs = this.getGlyph().getGlyphs().length;
            glyphPaths = new GeneralPath[numGlyphs];
        }
        
    }
    
    
    private GeneralPath getGlyphPath(int glyphId){
        initPaths();
        if ( glyphPaths[glyphId] == null ){
            GlyphData data = this.getGlyph().getGlyphs()[glyphId];
            Glyph2D g2d = new Glyph2D(data.getDescription(),(short)100, 200);
            glyphPaths[glyphId] = g2d.getPath();
        }
        return glyphPaths[glyphId];
        
    }
    
    
    
    /**
     * Close the underlying resources.
     * 
     * @throws IOException If there is an error closing the resources.
     */
    public void close() throws IOException
    {
        data.close();
    }

    /**
     * @return Returns the version.
     */
    public float getVersion() 
    {
        return version;
    }
    /**
     * @param versionValue The version to set.
     */
    public void setVersion(float versionValue) 
    {
        version = versionValue;
    }
    
    /**
     * Add a table definition.
     * 
     * @param table The table to add.
     */
    public void addTable( TTFTable table )
    {
        tables.put( table.getTag(), table );
    }
    
    /**
     * Get all of the tables.
     * 
     * @return All of the tables.
     */
    public Collection<TTFTable> getTables()
    {
        return tables.values();
    }
    
    /**
     * This will get the naming table for the true type font.
     * 
     * @return The naming table.
     */
    public NamingTable getNaming()
    {
        return (NamingTable)tables.get( NamingTable.TAG );
    }
    
    /**
     * Get the postscript table for this TTF.
     * 
     * @return The postscript table.
     */
    public PostScriptTable getPostScript()
    {
        return (PostScriptTable)tables.get( PostScriptTable.TAG );
    }
    
    /**
     * Get the OS/2 table for this TTF.
     * 
     * @return The OS/2 table.
     */
    public OS2WindowsMetricsTable getOS2Windows()
    {
        return (OS2WindowsMetricsTable)tables.get( OS2WindowsMetricsTable.TAG );
    }
    
    /**
     * Get the maxp table for this TTF.
     * 
     * @return The maxp table.
     */
    public MaximumProfileTable getMaximumProfile()
    {
        return (MaximumProfileTable)tables.get( MaximumProfileTable.TAG );
    }
    
    /**
     * Get the head table for this TTF.
     * 
     * @return The head table.
     */
    public HeaderTable getHeader()
    {
        return (HeaderTable)tables.get( HeaderTable.TAG );
    }
    
    /**
     * Get the hhea table for this TTF.
     * 
     * @return The hhea table.
     */
    public HorizontalHeaderTable getHorizontalHeader()
    {
        return (HorizontalHeaderTable)tables.get( HorizontalHeaderTable.TAG );
    }
    
    /**
     * Get the hmtx table for this TTF.
     * 
     * @return The hmtx table.
     */
    public HorizontalMetricsTable getHorizontalMetrics()
    {
        return (HorizontalMetricsTable)tables.get( HorizontalMetricsTable.TAG );
    }
    
    /**
     * Get the loca table for this TTF.
     * 
     * @return The loca table.
     */
    public IndexToLocationTable getIndexToLocation()
    {
        return (IndexToLocationTable)tables.get( IndexToLocationTable.TAG );
    }
    
    /**
     * Get the glyf table for this TTF.
     * 
     * @return The glyf table.
     */
    public GlyphTable getGlyph()
    {
        return (GlyphTable)tables.get( GlyphTable.TAG );
    }
    
    /**
     * Get the cmap table for this TTF.
     * 
     * @return The cmap table.
     */
    public CMAPTable getCMAP()
    {
        return (CMAPTable)tables.get( CMAPTable.TAG );
    }
    
    public com.codename1.ui.TTFFont getFont(String asName, float size){
        String key = asName+size;
        if ( fontCache.containsKey(key)){
            return fontCache.get(key);
        } else {
            com.codename1.ui.TTFFont f = new com.codename1.ui.TTFFont(asName, new PiscesFontCollection(size));
            
            HeaderTable h = this.getHeader();
            float upem = h.getUnitsPerEm();
            float ascender = this.getHorizontalHeader().getAscender();
            float scale = size / upem;
            ascender = ascender * scale;
            
            float descender = this.getHorizontalHeader().getDescender();
            descender = descender * scale;
            f.setAscent((int)ascender);
            f.setDescent((int)descender);
            fontCache.put(key, f);
            return f;
        }
    }
    
    /**
     * This permit to get the data of the True Type TTFFont
 program representing the stream used to build this 
 object (normally from the TTFParser object).
     * 
     * @return COSStream True type font program stream
     * 
     * @throws IOException If there is an error getting the font data.
     */
    public InputStream getOriginalData() throws IOException 
    {
       return data.getOriginalData(); 
    }
    
    class PiscesFontCollection implements Glyph.Collection {
        
        final float size;
        
        Image[] bitmaps;
        
        
        
        public PiscesFontCollection(float size){
            this.size = size;
        }

        
        Image[] bitmaps(){
            if ( bitmaps == null ){
                bitmaps = new Image[TrueTypeFont.this.getGlyph().getGlyphs().length];
            }
            return bitmaps;
        }
        
        
        
        public TTFFont.Kind getKind() {
            return TTFFont.Kind.Draw;
        }

        private int getGlyphId(char c ){
            int glyphId = -1;
            CMAPEncodingEntry[] maps = getCMAP().getCmaps();
            for ( int i=0; i<maps.length; i++){
                glyphId = maps[i].getGlyphId((int)c);
                if ( glyphId >= 0 ){
                   break;
                }
            }
            
            if (glyphId == -1 ){
                glyphId = 0;
            }
            return glyphId;
        }
        
        public Glyph getGlyph(char c) {
            int glyphId = getGlyphId(c);
            return new GlyphImpl(TrueTypeFont.this.getGlyph().getGlyphs()[glyphId], c, size, glyphId);
        }

        public int getMaxWidth() {
            HeaderTable h  = TrueTypeFont.this.getHeader();
            float upem = h.getUnitsPerEm();
            int w= (int)(size * (h.getXMax()-h.getXMin())/upem);
            return w;
        }

        public int getMaxHeight() {
            HeaderTable h = TrueTypeFont.this.getHeader();
            float upem = h.getUnitsPerEm();
            int hi = (int)(size * (h.getYMax()-h.getYMin())/upem);
            return hi;
        }

        public void read(InputStream in) throws IOException {
            throw new RuntimeException("Not implemented");
        }

        public Iterator<Glyph> iterator() {
            throw new RuntimeException("Not implemented yet");
        }
        
        
        
        class GlyphImpl implements Glyph {

            final float size;
            final GlyphData data;
            final char id;
            final static float resolution = 300f;
            final int glyphId;
            GlyphImpl(GlyphData data, char id, float size, int glyphId){
                this.data = data;
                this.id = id;
                this.size = size;
                this.glyphId = glyphId;

            }

            public char getId() {
                return id;
            }

            private Image getBitmap(){
                if ( bitmaps()[glyphId] == null ){
                    Image img = Image.createImage(getWidth(), getHeight());
                    Graphics g = img.getGraphics();
                    g.setColor(0x0);
                    draw(g, 0, 0, 0f);
                    bitmaps()[glyphId] = img;
                }
                return bitmaps()[glyphId];
            }

            public int getWidth() {
                HeaderTable h = TrueTypeFont.this.getHeader();
                float advance = TrueTypeFont.this.getHorizontalMetrics().getAdvanceWidth()[glyphId];

                float upem = h.getUnitsPerEm();

                int w = (int)(size * advance/upem);
                return w;

            }

            public int getHeight() {
                return (int)getMaxHeight();
            }

            public Glyph blit(Graphics g, int x, int y, float opacity) {
                int alpha = g.getAlpha();
                int newAlpha = (int)(opacity * 255);
                g.setAlpha(newAlpha);
                g.drawImage(getBitmap(), x, y);
                g.setAlpha(alpha);
                return this;
            }

            public Glyph draw(Graphics g, int x, int y, float opacity) {
                
                GeneralPath p2 = prepareForDraw(g, x, y, opacity);
                g.fillShape(p2);
                return this;

            }
            
            private GeneralPath prepareForDraw(Graphics g, int x, int y, float opacity) {
                //Glyph2D g2d = new Glyph2D(this.data.getDescription(),(short)100, 200);
                //Path p = null;
                HeaderTable h = TrueTypeFont.this.getHeader();
                float upem = h.getUnitsPerEm();
                float ascender = TrueTypeFont.this.getHorizontalHeader().getAscender();
                
                float scale = size / upem;
                ascender = ascender * scale;
                Transform transform = Transform.makeTranslation(x, y+ascender);
                transform.scale(scale, -scale);

                GeneralPath p = getGlyphPath(glyphId);
                
                GeneralPath p2 = new GeneralPath(p);
                //p.produce(p2);
                p2.transform(transform);
                return p2;
            }
            
            public Glyph draw(GeneralPath g, int x, int y, float opacity) {
                //Glyph2D g2d = new Glyph2D(this.data.getDescription(),(short)100, 200);
                //Path p = null;
                HeaderTable h = TrueTypeFont.this.getHeader();
                float upem = h.getUnitsPerEm();
                float ascender = TrueTypeFont.this.getHorizontalHeader().getAscender();
                float scale = size / upem;
                ascender = ascender * scale;
                Transform transform = Transform.makeTranslation(x, y + ascender);
                transform.scale(scale, -scale);
                GeneralPath p = getGlyphPath(glyphId);
                if (p == null) {
                    return this;
                }
                g.append(p.getPathIterator(transform), false);
                return this;

            }

            public Glyph stroke(Graphics g, int x, int y, float op, Stroke stroke) {
                GeneralPath p2 = prepareForDraw(g, x, y, op);
                g.drawShape(p2, stroke);
                return this;
            }

            public Glyph fill(Graphics g, int x, int y, float op) {
                return draw(g, x, y, op);
            }
        }
        
    }
    
    
}
