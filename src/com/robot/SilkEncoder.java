package com.robot;

public class SilkEncoder
{
    static{
        try{
            
            NativeUtils.loadLibraryFromJar("/native/"+System.getProperty("os.arch")+"/libencoder.so");
         //   NativeUtils.loadLibraryFromJar("/native/libffmpegrun.so");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public native static int ffmpegcmd(String[] cmd);
    
    public native static void pcm2silk(String inputPath, int sampleRate, String outputPath);
}
