/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.fontbox.util;

/**
 *
 * @author shannah
 */
public class Number {
    
    private static final byte INT=0;
    private static final byte SHORT=1;
    private static final byte BYTE=2;
    private static final byte LONG=3;
    private static final byte FLOAT=4;
    private static final byte DOUBLE=5;
    private static final byte OBJECT=6;
    
    public Number(int i){
        intval=i;
        type = INT;
    }
    
    public Number(short i){
        shortval=i;
        type = SHORT;
    }
    
    public Number(byte i){
        byteval = i;
        type=BYTE;
    }
    
    public Number(long i){
        longval = i;
        type=LONG;
    }
    
    public Number(float i){
        floatval = i;
        type=FLOAT;
    }
    
    public Number(double i){
        doubleval = i;
        type=DOUBLE;
    }
    
    public Number(Object o){
        box = o;
        type=OBJECT;
        
    }
    
    Object box;
    
    private int intval;
    private byte byteval;
    private long longval;
    private float floatval;
    private double doubleval;
    private short shortval;
    private byte type;
    
    
    
    public byte byteValue(){
        switch ( type ){
            case BYTE: return byteval;
            case INT: return (byte)intval;
            case SHORT: return (byte)shortval;
            case FLOAT: return (byte)floatval;
            case DOUBLE: return (byte)doubleval;
            case LONG: return (byte)longval;
        }
        if ( box instanceof Double){
            return ((Double)box).byteValue();
        } /*else if ( box instanceof Byte ){
            return ((Byte)box).byteValue();
        } */else if ( box instanceof Integer){
            return ((Integer)box).byteValue();
        }  else if ( box instanceof Float ){
            return ((Float)box).byteValue();
        } /*else if ( box instanceof Short ){
            return ((Short)box).byteValue();
        } else if ( box instanceof Long ){
            return ((Long)box).byteValue();
        }*/ else {
            return 0;
        }
    }
    
    public int intValue(){
        switch ( type ){
            case BYTE: return byteval;
            case INT: return (int)intval;
            case SHORT: return (int)shortval;
            case FLOAT: return (int)floatval;
            case DOUBLE: return (int)doubleval;
            case LONG: return (int)longval;
        }
        if ( box instanceof Double){
            return ((Double)box).intValue();
        } /*else if ( box instanceof Byte ){
            return ((Byte)box).intValue();
        }*/ else if ( box instanceof Integer){
            return ((Integer)box).intValue();
        }  else if ( box instanceof Float ){
            return ((Float)box).intValue();
        }/* else if ( box instanceof Short ){
            return ((Short)box).intValue();
        } else if ( box instanceof Long ){
            return ((Long)box).intValue();
        }*/ else {
            return 0;
        }
    }
    /*
    public short shortValue(){
        switch ( type ){
            case BYTE: return byteval;
            case INT: return (short)intval;
            case SHORT: return (short)shortval;
            case FLOAT: return (short)floatval;
            case DOUBLE: return (short)doubleval;
            case LONG: return (short)longval;
        }
        if ( box instanceof Double){
            return ((Double)box).shortValue();
        } else if ( box instanceof Byte ){
            return ((Byte)box).shortValue();
        } else if ( box instanceof Integer){
            return ((Integer)box).shortValue();
        }  else if ( box instanceof Float ){
            return ((Float)box).shortValue();
        } else if ( box instanceof Short ){
            return ((Short)box).shortValue();
        } else if ( box instanceof Long ){
            return ((Long)box).shortValue();
        } else {
            return 0;
        }
    }
    */
    public long longValue(){
        
        switch ( type ){
            case BYTE: return byteval;
            case INT: return (long)intval;
            case SHORT: return (long)shortval;
            case FLOAT: return (long)floatval;
            case DOUBLE: return (long)doubleval;
            case LONG: return (long)longval;
        }
        
        if ( box instanceof Double){
            return ((Double)box).longValue();
        }/* else if ( box instanceof Byte ){
            return ((Byte)box).longValue();
        } */else if ( box instanceof Integer){
            return ((Integer)box).longValue();
        }  else if ( box instanceof Float ){
            return ((Float)box).longValue();
        } /*else if ( box instanceof Short ){
            return ((Short)box).longValue();
        } else if ( box instanceof Long ){
            return ((Long)box).longValue();
        } */else {
            return 0;
        }
    }
    
    public float floatValue(){
        
        switch ( type ){
            case BYTE: return byteval;
            case INT: return (float)intval;
            case SHORT: return (float)shortval;
            case FLOAT: return (float)floatval;
            case DOUBLE: return (float)doubleval;
            case LONG: return (float)longval;
        }
        if ( box instanceof Double){
            return ((Double)box).floatValue();
        } /*else if ( box instanceof Byte ){
            return ((Byte)box).floatValue();
        } */else if ( box instanceof Integer){
            return ((Integer)box).floatValue();
        }  else if ( box instanceof Float ){
            return ((Float)box).floatValue();
        } /*else if ( box instanceof Short ){
            return ((Short)box).floatValue();
        } else if ( box instanceof Long ){
            return ((Long)box).floatValue();
        }*/ else {
            return 0;
        }
    }
    
    public double doubleValue(){
        switch ( type ){
            case BYTE: return byteval;
            case INT: return (double)intval;
            case SHORT: return (double)shortval;
            case FLOAT: return (double)floatval;
            case DOUBLE: return (double)doubleval;
            case LONG: return (double)longval;
        }
        if ( box instanceof Double){
            return ((Double)box).doubleValue();
        }/* else if ( box instanceof Byte ){
            return ((Byte)box).doubleValue();
        } */else if ( box instanceof Integer){
            return ((Integer)box).doubleValue();
        }  else if ( box instanceof Float ){
            return ((Float)box).doubleValue();
        } /*else if ( box instanceof Short ){
            return ((Short)box).doubleValue();
        } */else if ( box instanceof Long ){
            return ((Long)box).doubleValue();
        } else {
            return 0;
        }
    }
    
}
