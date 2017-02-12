package main.java.player;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ReadImage {

	//private long frameCount;		//for debug purpose\
	private String fileName;
	private final int width = 480;
	private final int height = 270;
	private InputStream is = null;
	private BufferedImage img = null;
	private byte[] bytes; 
	File file = null;

	public void load(String imageFileName, PlaySound pSound){
		try {
			img = null;
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			bytes = new byte[(int)width*height*3];
			fileName = imageFileName;
			is = null;
			file = new File(imageFileName);
			is = new FileInputStream(file);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImg() {
		return img;
	}

	/**
	 * Reads in the bytes of raw RGB data for a frame.
	 */
	public  BufferedImage readBytes() {
		//frameCount++;
		try {
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}
			int ind = 0;
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					img.setRGB(x,y,pix);
					ind++;
				} 
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("frameCount: " + frameCount);
		return img;
	}
	
	public void ModifyRGBFileMultiple(int[] start, int[] end, String[] adImg) throws IOException{
		
		long count = 0;
		int index = 0;
		boolean isRangeChecked = false;
		RandomAccessFile ip;
		try {
			ip = new RandomAccessFile(fileName, "r");
			byte[] bytes2 = new byte[(int)width*height*3];
			FileOutputStream out = new FileOutputStream("/Users/Brain/Documents/javaworkspace/Ads_processing_git_master/dataset/Ads/outputFile.rgb");
			int numRead = 0;
			long offset = 0;
			boolean isReplaced = false;
	
			while(offset < getImageFileLength() && ((numRead=ip.read(bytes2, 0, bytes2.length)) >= 0)){
				count++;
				offset += numRead;
				ip.seek(offset);
				System.out.println(offset);
				if(index < start.length && count>=(30*start[index]) && count<=(30*end[index])){
					isRangeChecked = true;
					if(adImg[index] != null){
						if(!isReplaced){
							RandomAccessFile adFile = new RandomAccessFile(adImg[index], "r");
							int nr = 0;
							int os = 0;
							try{
								File f = new File(adImg[index]);
								while(os<f.length() && ((nr=adFile.read(bytes2, 0, bytes2.length)) >= 0)){
									os += nr;
									adFile.seek(os);
									out.write(bytes2);
								}
								adFile.close();
								isReplaced = true;
							}
							catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
						else
							continue;
					}
					else
						continue;
				}
				else{
					if(isRangeChecked){
						index++;
						isRangeChecked = false;
						isReplaced = false;
					}
					out.write(bytes2);
				}
			}
			System.out.println(getImageFileLength());
			System.out.println(count);
			ip.close();
			out.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void ModifyRGBFile(int start, int end, String adImg) throws IOException{
		
			long framesSkipStartCount = 30*start;
			long frameSkipEndCount = 30*end;
			long count = 0;
			RandomAccessFile ip;
			try {
				ip = new RandomAccessFile(fileName, "r");
				byte[] bytes2 = new byte[(int)width*height*3];
				FileOutputStream out = new FileOutputStream("/Users/Brain/Documents/javaworkspace/Ads_processing_git_master/dataset/Ads/outputFile.rgb");
				int numRead = 0;
				long offset = 0;
				boolean isReplaced = false;
		
				while(offset < getImageFileLength() && ((numRead=ip.read(bytes2, 0, bytes2.length)) >= 0)){
					count++;
					offset += numRead;
					ip.seek(offset);
					System.out.println(offset);
					if(count>=framesSkipStartCount && count<=frameSkipEndCount){
						if(adImg != null){
							if(!isReplaced){
								RandomAccessFile adFile = new RandomAccessFile(adImg, "r");
								int nr = 0;
								int os = 0;
								File f = new File(adImg);
								while(os<f.length() && ((nr=adFile.read(bytes2, 0, bytes2.length)) >= 0)){
									os += nr;
									adFile.seek(os);
									out.write(bytes2);
								}
								adFile.close();
								isReplaced = true;
							}
							else
								continue;
						}
						else
							continue;
					}
					else
						out.write(bytes2);
				}
				System.out.println(getImageFileLength());
				System.out.println(count);
				ip.close();
				out.close();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}

	public long getImageFileLength(){
		return file.length();
	}

	public void resetInputStream(String fileName){
		try {
			is = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
}