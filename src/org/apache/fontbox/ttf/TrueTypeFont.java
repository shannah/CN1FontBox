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



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import pisces.Font;
import pisces.Font.Glyph;
import pisces.Graphics;
import pisces.Path;

import pisces.m.Matrix;

/**
 * A class to hold true type font information.
 * 
 * @author Ben Litchfield (ben@benlitchfield.com)
 * @version $Revision: 1.2 $
 */
public class TrueTypeFont 
{
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
    
    public pisces.Font getFont(String asName, float size){
        HeaderTable h = getHeader();
        
        return new pisces.Font(asName, new PiscesFontCollection(size));
    }
    
    /**
     * This permit to get the data of the True Type Font
     * program representing the stream used to build this 
     * object (normally from the TTFParser object).
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
        
        public PiscesFontCollection(float size){
            this.size = size;
        }

        public Font.Kind getKind() {
            return Font.Kind.Draw;
        }

        private int getGlyphId(char c ){
            int glyphId = -1;
            CMAPEncodingEntry[] maps = getCMAP().getCmaps();
            for ( int i=0; i<maps.length; i++){
                glyphId = maps[i].getGlyphId(c);
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
            int w= h.getXMax()-h.getXMin();
            return w;
        }

        public int getMaxHeight() {
            HeaderTable h = TrueTypeFont.this.getHeader();
            return h.getYMax()-h.getYMin();
        }

        public void read(InputStream in) throws IOException {
            throw new RuntimeException("Not implemented");
        }

        public Iterator<Glyph> iterator() {
            throw new RuntimeException("Not implemented yet");
        }
        
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

        public int getWidth() {
            HeaderTable h = TrueTypeFont.this.getHeader();
            float advance = TrueTypeFont.this.getHorizontalMetrics().getAdvanceWidth()[glyphId];
            
            float upem = h.getUnitsPerEm();
            
            int w = (int)(size * advance/upem);
            return w;
            
        }

        public int getHeight() {
            return (int)size;
        }

        public Glyph blit(Graphics grphcs, int i, int i1, float f) {
            throw new RuntimeException("No blitting allowed on this one... draw instead");
        }

        public Glyph draw(pisces.Graphics g, int x, int y, float f) {
            Glyph2D g2d = new Glyph2D(this.data.getDescription(),(short)100, 200);
            Path p = null;
            HeaderTable h = TrueTypeFont.this.getHeader();
            float upem = h.getUnitsPerEm();
            
            float scale = size / upem;
            Matrix transform = Matrix.getTranslateInstance(x, y+getHeight());
            transform.scale(scale, -scale);
            
            p = g2d.getPath();
            p.transform(transform);
            g.fill(p);
            return this;
            
        }
        
    }
}
