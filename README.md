# Codename One FontBox Port

This is a port of the [FontBox](http://sourceforge.net/projects/fontbox/) library for use with [Codename One](http://www.codenameone.com).

This library adds the ability to load and render TTF fonts as scalable vectors.  This includes a `TTFFont` class that extends the core
Codename One `Font` class so that it can be used pretty much any place a "normal" font would be used in Codename One.  The difference is
that TTFFonts are rendered using the CN1 Shapes API.  While this would be slower than using regular fonts (which are rendered
natively), this provides certain abilities that you don't have with normal fonts, such choosing to "stroke" or "fill" (or both - with different colors) text, scaling the width
or height of a font, or potentially providing your own 3D transformations to the fonts. 

## Features

1. Support for TTF Fonts
2. Library additionally can support CFF, AFM. and PFB fonts but these haven't been specifically integrated into Codename One.
3. Obtain font metrics, and glyph shapes as vector paths.
4. Stroking support (e.g. outlines of fonts)
5. Fonts can be used with AffineTransforms (scale, shear, rotate, translation) to draw text in any size and orientation.


## Compatibility

This should work on any platform that supports the CN1 shapes API.  This currently includes:

1. iOS
2. Android
3. UWP
4. Javascript
5. JavaSE (i.e. the CN1 simulator)

## License

Apache License 2.0

## Dependencies

1. None

## Installation Instructions

Assuming you have a Codename One application project started in Netbeans:

1. Copy [CN1FontBox.cn1lib](https://github.com/shannah/CN1FontBox/raw/master/dist/CN1FontBox.cn1lib) into your project's `lib` directory.  Then right click on your project's icon in the project explorer, and select "Refresh Libs" from the contextual menu.

## Usage Examples


### Loading TTF File

**From InputStream:**

~~~
TTFFont font = TTFFont.createFont("MyFont", inputStream);
~~~

**From Resources:**

~~~
TTFFont font = TTFFont.createFont("MyFont", "/MyFont.ttf");
~~~

**From Storage/URL:**

~~~
TTFFont font = TTFFont.createFontToStorage("MyFont", 
    "font_MyFont.ttf", 
    "http://example.com/MyFont.ttf"
);
~~~

**From File System/URL:**

~~~
TTFFont font = TTFFont.createFontToFileSystem("MyFont", 
    FileSystemStorage.getInstance().getAppHomePath()+"/fonts/MyFont.ttf", 
    "http://example.com/MyFont.ttf"
);
~~~

**From Cache:**

~~~
TTFFont font = TTFFont.getFont("MyFont", 12);
~~~

### Setting Font for Style

~~~
myLabel.getAllStyles().setFont(font);
~~~

### Getting Particular Size Font

~~~
font = font.deriveFont(24); // get size 24 font.
~~~

### Horizontal and Vertical Scaling

~~~
font = font.deriveScaled(0.5f, 1.5f);  
    // scaled 50% horizontal, and 150% vertical
    
font = font.deriveHorizontalScaled(0.5f); // scaled 50% horizontally

font = font.deriveVerticalScaled(0.5f); // scaled 50% vertically
~~~

### Stroking and Filling

~~~
font = font.deriveStroked(Stroke(1f, Stroke.CAP_BUTT, Stroke.JOIN_MITER, 1f), #ff0000);
    // Stroke with red 1px outline 

font = font.deriveStroked(Stroke(1f, Stroke.CAP_BUTT, Stroke.JOIN_MITER, 1f), null);
    // Stroked - stroke color determined by graphics context's current color.. e.g. defers to Style's foreground color
    
font = font.deriveStroked(null, 0x0);
    // Not stroked
    
font = font.deriveFilled(true, null);
    // Filled - fill color determined by graphics context's current color.. e.g. defers to Style's foreground color
    
font = font.deriveFilled(true, 0x00ff00);
    // Filled with green
    
font = font.deriveFilled(false, null);
    // Not filled
~~~

### Antialias

~~~
font = font.deriveAntialias(true);
    // Should be rendered antialiased
    
font = font.deriveAntialias(false);
    // should be rendered without antialias.
~~~


### Drawing Directly on Graphics Context

~~~
font.drawString(g, "Hello world", x, y);

// Or ...
g.setFont(font);
g.drawString("Hello world", x, y);
~~~

### Appending to existing GeneralPath

~~~
font.draw(path, "Hello world", x, y, 1f /*opacity*/);
~~~
