/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.fontbox.ttf;

import com.codename1.ui.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;




/**
 * This class provides a glyph to GeneralPath conversion.
 * 
 * This class is based on code from Apache Batik a subproject of Apache XMLGraphics.
 * see http://xmlgraphics.apache.org/batik/ for further details.
 */
public class Glyph2D 
{

    private short leftSideBearing = 0;
    private int advanceWidth = 0;
    private Point[] points;
    private GeneralPath glyphPath;

    /**
     * Constructor.
     * 
     * @param gd the glyph description
     * @param lsb leftSideBearing
     * @param advance advanceWidth
     */
    public Glyph2D(GlyphDescription gd, short lsb, int advance) 
    {
        leftSideBearing = lsb;
        advanceWidth = advance;
        describe(gd);
    }

    /**
     * Returns the advanceWidth value.
     * 
     * @return the advanceWidth
     */
    public int getAdvanceWidth() 
    {
        return advanceWidth;
    }

    /**
     * Returns the leftSideBearing value.
     * 
     * @return the leftSideBearing
     */
    public short getLeftSideBearing() 
    {
        return leftSideBearing;
    }

    /**
     * Set the points of a glyph from the GlyphDescription.
     */
    private void describe(GlyphDescription gd) 
    {
        int endPtIndex = 0;
        points = new Point[gd.getPointCount()];
        for (int i = 0; i < gd.getPointCount(); i++) 
        {
            boolean endPt = gd.getEndPtOfContours(endPtIndex) == i;
            if (endPt) 
            {
                endPtIndex++;
            }
            points[i] = new Point(
                    gd.getXCoordinate(i),
                    gd.getYCoordinate(i),
                    (gd.getFlags(i) & GlyfDescript.ON_CURVE) != 0,
                    endPt);
        }
    }
    
    /**
     * Returns the path describing the glyph.
     * 
     * @return the GeneralPath of the glyph
     */
    public GeneralPath getPath() 
    {
        if (glyphPath == null)
        {
            glyphPath = calculatePath(points);
        }
        return glyphPath;
    }
    
    
    private GeneralPath calculatePath(Point[] points) 
    { 
        GeneralPath path = new GeneralPath(); 
        int start = 0; 
        for (int p = 0, len = points.length; p < len; ++p) 
        { 
         if (points[p].endOfContour) 
            { 
          Point firstPoint = points[start]; 
          Point lastPoint = points[p]; 
          List<Point> contour = new ArrayList<Point>(); 
          for (int q = start; q <= p; ++q) 
                { 
           contour.add(points[q]); 
                } 
          if (points[start].onCurve) 
                { 
           // using start point at the contour end 
           contour.add(firstPoint); 
                } 
          else if (points[p].onCurve) 
                { 
           // first is off-curve point, trying to use one from the end 
           contour.add(0, lastPoint); 
                } 
                else 
                { 
                 // start and end are off-curve points, creating implicit one 
                 Point pmid = midValue(firstPoint, lastPoint); 
                 contour.add(0, pmid); 
                 contour.add(pmid); 
                } 
          moveTo(path, contour.get(0)); 
          for (int j = 1, clen = contour.size(); j < clen; j++) 
                { 
           Point pnow = contour.get(j); 
           if (pnow.onCurve) 
           { 
            lineTo(path, pnow); 
           } 
           else if (contour.get(j + 1).onCurve) 
           { 
            quadTo(path, pnow, contour.get(j + 1)); 
            ++j; 
           } 
           else 
           { 
            quadTo(path, pnow, midValue(pnow, contour.get(j + 1))); 
           } 
                } 
          start = p + 1; 
            } 
        } 
        return path; 
    } 
    
    private void moveTo(GeneralPath path, Point point) {
        path.moveTo(point.x, point.y);
    }
    
    private void lineTo(GeneralPath path, Point point) 
    { 
        path.lineTo(point.x, point.y); 
//        Log.v("PdfBoxAndroid", "lineTo: " + String.format("%d,%d", point.x, point.y)); 
    } 
    
    private void quadTo(GeneralPath path, Point ctrlPoint, Point point) 
    { 
        path.quadTo(ctrlPoint.x, ctrlPoint.y, point.x, point.y); 
//        Log.v("PdfBoxAndroid", "quadTo: " + String.format("%d,%d %d,%d", ctrlPoint.x, ctrlPoint.y, 
//                    point.x, point.y)); 
    }
    
    private Point midValue(Point point1, Point point2) 
    { 
        return new Point(midValue(point1.x, point2.x), midValue(point1.y, point2.y)); 
    } 
    
    

    private int midValue(int a, int b) 
    {
        return a + (b - a)/2;
    }

    /**
     * This class represents one point of a glyph.  
     *
     */
    private class Point 
    {

        public int x = 0;
        public int y = 0;
        public boolean onCurve = true;
        public boolean endOfContour = false;

        public Point(int xValue, int yValue, boolean onCurveValue, boolean endOfContourValue) 
        {
            x = xValue;
            y = yValue;
            onCurve = onCurveValue;
            endOfContour = endOfContourValue;
        }

        public Point(int xValue, int yValue) 
        {
            this(xValue, yValue, false, false);
        }
        
        public String toString() {
            return "Point{x="+x+", y="+y+", onCurve="+onCurve+", endOfContour="+endOfContour;
        }
    }

}