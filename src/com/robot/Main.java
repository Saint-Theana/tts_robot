package com.robot;
import java.util.*;
import com.Tick_Tock.ANDROIDQQ.sdk.*;
import java.util.regex.*;
import org.json.*;
import com.Tick_Tock.ANDROIDQQ.sdk.Message.*;
import com.Tick_Tock.ANDROIDQQ.sdk.MessageBuilder.*;
import com.Tick_Tock.ANDROIDQQ.sdk.Target.*;
import java.io.*;
import com.Tick_Tock.ANDROIDQQ.sdk.Event.*;

public class Main implements Plugin
{
    
    
    public static void main(String j[]){
        SilkEncoder.ffmpegcmd(j);
        SilkEncoder.pcm2silk("/sdcard/pcm16k.pcm",24000,"/sdcard/1.silk");
        
        
        
    }
	private API api;

	@Override
	public String version()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public String author()
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public String name()
	{
		return "tts机器人";
	}

	@Override
	public void onLoad(API p1)
	{
		this.api = p1;
      
	}

	@Override public void onMessageHandler(Event message)
	{
		
	}


	@Override public void onMessageHandler(QQMessage qqmessage)
	{
        
		if(qqmessage.toString().matches("说[ ]+.*")){
			String filename = this.tts(qqmessage.toString().replaceAll("说[ ]+",""));
			if(filename!=null){
				MessageBuilder builder = new MessageBuilder();
				builder.addVoice(filename);
				this.api.sendMessage(builder,qqmessage.getTarget());
			}
		}else if(qqmessage.toString().matches("听[ ]+.*")){
            String filename = this.music(qqmessage.toString().replaceAll("听[ ]+",""));
            if(filename!=null){
                MessageBuilder builder = new MessageBuilder();
                builder.addVoice(filename);
                this.api.sendMessage(builder,qqmessage.getTarget());
			}
        }
	}

    private String music(String text)
    {
       // System.out.println(System.getProperty("os.arch"));
        String cachepath = this.api.getPluginPath()+"cache/";
        if(!new File(cachepath).exists()){
            new File(cachepath).mkdir();
        }
        String songid = Util.downloadmusic(cachepath,text);
        if(songid==null||songid.isEmpty()){
            return null;
        }
        if(Util.issongcached(cachepath,songid)){
            return cachepath+songid+".silk";
        }
        SilkEncoder.ffmpegcmd(("ffmpeg -i "+cachepath+songid+".mp3 -f s16le -ar 24000 -ac 1 -acodec pcm_s16le "+cachepath+songid+".pcm -y").split("[ \\t]+"));
        SilkEncoder.pcm2silk(cachepath+songid+".pcm",24000,cachepath+songid+".silk");
        return cachepath+songid+".silk";
    }

	private String tts(String text)
	{
        String cachepath = this.api.getPluginPath()+"cache/";
        if(!new File(cachepath).exists()){
            new File(cachepath).mkdir();
        }
		String mp3filename = Util.downloadtts(cachepath,text);
		String pcmfilename  =Util.getRandomString(6);
        SilkEncoder.ffmpegcmd(("ffmpeg -i "+cachepath+mp3filename+".mp3 -f s16le -ar 24000 -ac 1 -acodec pcm_s16le "+cachepath+pcmfilename+".pcm -y").split("[ \\t]+"));
        SilkEncoder.pcm2silk(cachepath+pcmfilename+".pcm",24000,cachepath+pcmfilename+".silk");
        return cachepath+pcmfilename+".silk";
	}
}

