/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.fontbox.cff;

import org.apache.fontbox.ttf.*;
import com.codename1.io.Log;

/**
 *
 * @author shannah
 */
class LOG {
    static void debug(String str){
        Log.p(str, Log.DEBUG);
    }
    
    static boolean isDebugEnabled(){
        return false;
    }
    
    static void warn(String str){
        Log.p(str, Log.WARNING);
    }
}
