package com.robot;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

public class Util
{

    public static String downloadmusic(String path, String text)
    {
        try
        {
            String info = Util.Curl("http://search.kuwo.cn/r.s?client=kt&all=" + text.replaceAll("[ \\t]+", "+") + "&pn=0&rn=10&uid=221260053&ver=kwplayer_ar_99.99.99.99&vipver=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1");

            JSONObject json = new JSONObject(info);
            JSONArray songs_list = json.getJSONArray("abslist");
            

            JSONObject data = songs_list.getJSONObject(0);

            String song_id = data.getString("MUSICRID");

            if(issongcached(path,song_id)){
                return song_id;
            }
            //String album_name =data.getJSONObject("album").getString("name");
            Util.Curl("http://antiserver.kuwo.cn/anti.s?type=convert_url&rid=" + song_id + "&format=mp3&response=url");

            String url=Util.getkuwourl(text);
            InputStream is=new URL((url)).openStream();
            ByteArrayOutputStream buffer=new ByteArrayOutputStream();
            int b=-1;
            while ((b = is.read()) != -1)
                buffer.write(b);
            return savefile(path,song_id, buffer.toByteArray());
        }
        catch (MalformedURLException e)
        {
            System.out.println(e.toString());
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
        catch (JSONException e)
        {
            System.out.println(e.toString());
        }
		return null;
    }

    public static boolean issongcached(String path,String song_id)
    {
      return  new File( path+song_id+".silk").exists();
 
    }

    private static String getkuwourl(String text)
    {
        String info = Util.Curl("http://search.kuwo.cn/r.s?client=kt&all=" + text.replaceAll("[ \\t]+", "+") + "&pn=0&rn=10&uid=221260053&ver=kwplayer_ar_99.99.99.99&vipver=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1");
        try
        {
            JSONObject json = new JSONObject(info);
            JSONArray songs_list = json.getJSONArray("abslist");


            JSONObject data = songs_list.getJSONObject(0);

            String song_id = data.getString("MUSICRID");

            //String album_name =data.getJSONObject("album").getString("name");
            return Util.Curl("http://antiserver.kuwo.cn/anti.s?type=convert_url&rid=" + song_id + "&format=mp3&response=url");


        }
        catch (JSONException e)
        {
            return null;
        }

    }




    public static String getkugouurl(String song)
    {

        String info = Util.curl_with_referer("http://songsearch.kugou.com/song_search_v2?keyword=" + song .replaceAll(" ", "+") + "&page=0&pagesize=10&userid=-1&clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0", "http://www.kugou.com");
        try
        {
            JSONObject json = new JSONObject(info);



            String File_hash = json.getJSONObject("data").getJSONArray("lists").getJSONObject(0).getString("FileHash");
            JSONObject data = new JSONObject(Util.curl_with_referer("http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + File_hash, "http://www.kugou.com")).getJSONObject("data");
            //String audio_name  = data.getString("audio_name");
            //String album_name = data.getString("album_name");

            return data.getString("play_url");

        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

	public static String getpath()
	{
		// TODO: Implement this method
		return new File("").getAbsolutePath();
	}

	public static String downloadtts(String path, String text)
	{
		try
		{
			InputStream is=new URL("https://tts.baidu.com/text2audio?tex=" + text.replaceAll(" ", "+") + "&cuid=baike&rate=192&ctp=1&pdt=301&vol=9&lan=ZH&per=0").openStream();
			ByteArrayOutputStream buffer=new ByteArrayOutputStream();
			int b=-1;
			while ((b = is.read()) != -1)
				buffer.write(b);
			return savefile(path,Util.getRandomString(6), buffer.toByteArray());
		}
		catch (MalformedURLException e)
		{
			System.out.println(e.toString());
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}
		return null;
	}

	private static String savefile(String path,String filename, byte[] data) throws FileNotFoundException, IOException
	{
		
		File file =new File(path + filename + ".mp3");
        OutputStream out =new FileOutputStream(file);
        out.write(data);
        out.close();
		return filename;
	}

	public static String getRandomString(int length)
    {
		String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random=new Random();
		StringBuffer sb=new StringBuffer();
		for (int i=0;i < length;i++)
        {
			int number=random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	public static String curl_with_referer(String url, String referer)
	{  
		try
		{  
			URL lll = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Referer", referer);
            connection.setRequestProperty("Cookie", "kg_mid=15244717272755554455555");
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}

	public static String get_redirected_url(String url)
    {
		String location =null;
		try
        {  
            URL serverUrl = new URL(url);  
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();  
            connection.setRequestMethod("GET");  
            // 必须设置false，否则会自动redirect到Location的地址  
            connection.setInstanceFollowRedirects(false);
            connection.addRequestProperty("Accept-Charset", "UTF-8;");  
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
            connection.connect();  
            location = connection.getHeaderField("Location");
			connection.disconnect();

        }
        catch (Exception e)
        {  
            e.printStackTrace();  
        }  
		return location;
	}

	public static String post_with_data(String url, String data)
	{  
		try
		{
			URL lll = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Accept", "*/*");
			connection.setDoOutput(true);
			connection.setRequestProperty("Referer", "https://music.163.com/m/song?id=16431842");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
			connection.setRequestProperty("Origin", "http://music.163.com");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
			connection.connect();// 连接会话  
			// 获取输入流  
			PrintWriter writer = new PrintWriter(connection.getOutputStream());
			writer.print(data);                                    
			writer.flush();
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}

	public static String Curl(String url)
	{
		try
		{
			InputStream is=new URL(url).openStream();
			ByteArrayOutputStream buffer=new ByteArrayOutputStream();
			int b=-1;
			while ((b = is.read()) != -1)
				buffer.write(b);
			return new String(buffer.toByteArray());
		}
		catch (MalformedURLException e)
		{
			System.out.println(e.toString());
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}
		return null;
	}
	public static String ridiculous_uin(String qquin)
	{
		String uin = String.valueOf(qquin);
		return uin.replace("0", "⓪")
	    	.replace("1", "①")
	     	.replace("2", "②")
			.replace("3", "③")
			.replace("4", "④")
			.replace("5", "⑤")
			.replace("6", "⑥")
			.replace("7", "⑦")
			.replace("8", "⑧")
			.replace("9", "⑨");
	}
}
