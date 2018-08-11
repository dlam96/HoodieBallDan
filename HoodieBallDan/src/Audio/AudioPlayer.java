package Audio;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;

public class AudioPlayer {
	
	private Clip clip;
	
	public AudioPlayer(String s, double vol) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(
				getClass().getResourceAsStream(
						s
						)
				);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),
					16,
					baseFormat.getChannels(),
					baseFormat.getChannels() * 2,
					baseFormat.getSampleRate(),
					false
					);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
			setVol(vol, clip);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	private static void setVol(double vol, Clip clip) {
		FloatControl gain = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		float dB = (float) (Math.log(vol) / Math.log(10) * 20);
		gain.setValue(dB);
	}
	
	public void play() {
		if(clip == null) return;
		stop();
		clip.setFramePosition(0);
		clip.start();
	}
	
	 public void loop(){
	        clip.loop(Clip.LOOP_CONTINUOUSLY);
	    }
	 
	public void stop() {
		if(clip.isRunning() == true) clip.stop();
	}
	
	public void close() {
		stop();
		clip.close();
	}
}
