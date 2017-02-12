package main.java.player;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySound implements LineListener {
	private static final int SECONDS_IN_HOUR = 60 * 60;
	private static final int SECONDS_IN_MINUTE = 60;

	/**
	 * this flag indicates whether the playback completes or not.
	 */
	private boolean playCompleted;

	/**
	 * this flag indicates whether the playback is stopped or not.
	 */
	private boolean isStopped;

	private boolean isPaused;

	private Clip audioClip;

	private AudioFormat format = null;
	
	private String audioFilePath;
	
	private long byteLength;
	
	private float sampleRate;
	
	private long totalSamples;

	/**
	 * Load audio file before playing back
	 * 
	 * @param audioFilePath
	 *            Path of the audio file.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public void load(String audioFilePath)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		format = null;
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(audioFilePath));
		format = audioStream.getFormat();
		this.audioFilePath = audioFilePath;
		DataLine.Info info = new DataLine.Info(Clip.class, format);

		audioClip = null;
		audioClip = (Clip) AudioSystem.getLine(info);

		audioClip.addLineListener(this);

		audioClip.open(audioStream);
		RandomAccessFile ip = new RandomAccessFile(audioFilePath, "r");
		byteLength = ip.length();
		ip.close();
		totalSamples = audioStream.getFrameLength();
		sampleRate = getSampleRate();
	}
	
	public void modifyWavFileMultiple(int[] start, int[] end, String[] adWav) throws IOException{
		try {
			RandomAccessFile is = new RandomAccessFile(audioFilePath, "r");
			FileOutputStream of = new FileOutputStream("/Users/Brain/Documents/javaworkspace/Ads_processing_git_master/dataset/Ads/outputFile.wav");
			byte[] bytes = new byte[(int) ((byteLength*sampleRate)/(totalSamples*30))];
			System.out.println(byteLength);
			System.out.println(sampleRate);
			System.out.println(totalSamples);
			System.out.println("---------------------------------");
			long offset = 0;
			long numRead = 0;
			int count = 0;
			int index = 0;
			boolean isRangeChecked = false;
			boolean isReplaced = false;
			while(offset<byteLength && (numRead = is.read(bytes, 0, bytes.length))>=0){
				count++;
				offset += numRead;
				is.seek(offset);
				System.out.println(offset);
				if(index<start.length && count>=(30*start[index]) && count<=(30*end[index])){
					isRangeChecked = true;
					if(adWav[index] != null){
						if(!isReplaced){
							RandomAccessFile adFile = new RandomAccessFile(adWav[index], "r");
							int nr = 0;
							int os = 0;
							try{
								File f = new File(adWav[index]);
								while(os<f.length() && ((nr=adFile.read(bytes, 0, bytes.length)) >= 0)){
									os += nr;
									adFile.seek(os);
									of.write(bytes);
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
				else
					if(isRangeChecked){
						index++;
						isRangeChecked = false;
						isReplaced = false;
					}
					of.write(bytes);
			}
			System.out.println(count);
			is.close();
			of.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public void modifyWavFile(int start, int end, String adWav) throws IOException{
		try {
			RandomAccessFile is = new RandomAccessFile(audioFilePath, "r");
			FileOutputStream of = new FileOutputStream("/Users/Brain/Documents/javaworkspace/Ads_processing_git_master/dataset/Ads/outputFile.wav");
			byte[] bytes = new byte[(int) ((byteLength*sampleRate)/totalSamples)];
			long offset = 0;
			long numRead = 0;
			int count = 0;
			boolean isReplaced = false;
			while(offset<byteLength && (numRead = is.read(bytes, 0, bytes.length))>0){
				count++;
				offset += numRead;
				is.seek(offset);
				System.out.println(offset);
				if(count>start && count<=end){
					if(adWav != null){
						if(!isReplaced){
							RandomAccessFile adFile = new RandomAccessFile(adWav, "r");
							int nr = 0;
							int os = 0;
							try{
								File f = new File(adWav);
								while(os<f.length() && ((nr=adFile.read(bytes, 0, bytes.length)) >= 0)){
									os += nr;
									adFile.seek(os);
									of.write(bytes);
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
				else
					of.write(bytes);
			}
			System.out.println(count);
			is.close();
			of.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public long getClipSecondLength() {
		return audioClip.getMicrosecondLength() / 1000000;
	}

	public String getClipLengthString() {
		String length = "";
		long hour = 0;
		long minute = 0;
		long seconds = audioClip.getMicrosecondLength() / 1000000;

		System.out.println(seconds);

		if (seconds >= SECONDS_IN_HOUR) {
			hour = seconds / SECONDS_IN_HOUR;
			length = String.format("%02d:", hour);
		} else {
			length += "00:";
		}

		minute = seconds - hour * SECONDS_IN_HOUR;
		if (minute >= SECONDS_IN_MINUTE) {
			minute = minute / SECONDS_IN_MINUTE;
			length += String.format("%02d:", minute);

		} else {
			minute = 0;
			length += "00:";
		}

		long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;

		length += String.format("%02d", second);

		return length;
	}

	/**
	 * Play a given audio file.
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	void play() throws IOException {

		audioClip.start();

		playCompleted = false;
		isStopped = false;

		while (!playCompleted) {
			// wait for the playback completes
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				if (isStopped) {
					audioClip.stop();
					break;
				}
				if (isPaused) {
					audioClip.stop();
				} else {
					//System.out.println("!!!!");
					audioClip.start();
				}
			}
		}

		audioClip.close();

	}

	/**
	 * Stop playing back.
	 */
	public void stop() {
		isStopped = true;
	}

	public void pause() {
		isPaused = true;
	}

	public void resume() {
		isPaused = false;
	}

	/**
	 * Listens to the audio line events to know when the playback completes.
	 */
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.STOP) {
			System.out.println("STOP EVENT");
			if (isStopped || !isPaused) {
				playCompleted = true;
			}
		}
	}

	public Clip getAudioClip() {
		return audioClip;
	}

	public long getPosition() {
		return audioClip.getLongFramePosition();
	}

	public float getSampleRate() {
		return format.getFrameRate();
	}

}
