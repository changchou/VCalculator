package com.zhang.vcalculator;

import android.content.Context;
import android.media.SoundPool;
import android.os.Handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Mr.Z on 2016/12/27 0027.
 */

public class SoundManager {

    public static final String TAG = SoundManager.class.getSimpleName();

    private SoundManager() {
    }

    private static SoundManager sMe;

    public synchronized static SoundManager getInstance() {
        if (sMe == null) {
            sMe = new SoundManager();
        }
        return sMe;
    }

    private static final int[] RES = {
            R.raw.one,
            R.raw.two,
            R.raw.three,
            R.raw.four,
            R.raw.five,
            R.raw.six,
            R.raw.seven,
            R.raw.eight,
            R.raw.nine,
            R.raw.zero,
            R.raw.ac,
            R.raw.del,
            R.raw.plus,
            R.raw.mul,
            R.raw.minus,
            R.raw.div,
            R.raw.equal,
            R.raw.dot
    };

    private Context context;

    private boolean playing;

    private int curStreamId;

    private SoundPool soundPool;

    private HashMap<String, Sound> soundMap;

    private Vector<String> soundQueue = new Vector<>();

    /**
     * @param context
     */
    public void initSound(Context context) {
        this.context = context;
        this.soundPool = new SoundPool(RES.length, 3, 0);
        this.playing = false;

        this.soundMap = new HashMap<>();

        addSound(context.getString(R.string.digit1), R.raw.one, 320);
        addSound(context.getString(R.string.digit2), R.raw.two, 274);
        addSound(context.getString(R.string.digit3), R.raw.three, 304);
        addSound(context.getString(R.string.digit4), R.raw.four, 215);
        addSound(context.getString(R.string.digit5), R.raw.five, 388);
        addSound(context.getString(R.string.digit6), R.raw.six, 277);
        addSound(context.getString(R.string.digit7), R.raw.seven, 447);
        addSound(context.getString(R.string.digit8), R.raw.eight, 274);
        addSound(context.getString(R.string.digit9), R.raw.nine, 451);
        addSound(context.getString(R.string.digit0), R.raw.zero, 404);
        addSound(context.getString(R.string.clear), R.raw.ac, 696);
        addSound(context.getString(R.string.del), R.raw.del, 442);
        addSound(context.getString(R.string.plus), R.raw.plus, 399);
        addSound(context.getString(R.string.mul), R.raw.mul, 399);
        addSound(context.getString(R.string.minus), R.raw.minus, 399);
        addSound(context.getString(R.string.div), R.raw.div, 399);
        addSound(context.getString(R.string.equal), R.raw.equal, 480);
        addSound(context.getString(R.string.dot), R.raw.dot, 454);

    }

    private Handler handler = new Handler();

    private Runnable playNext = new Runnable() {
        @Override
        public void run() {
            SoundManager.this.soundPool.stop(SoundManager.this.curStreamId);
            SoundManager.this.playNextSound();
        }
    };

    private void playNextSound() {
        if (soundQueue.isEmpty()) {
            return;
        }
        String str = soundQueue.remove(0);
        Sound sound = soundMap.get(str);

        if (sound != null) {
            curStreamId = soundPool.play(sound.id, 0.2F, 0.2F, 1, 0, 1.0F);
            this.playing = true;
            this.handler.postDelayed(playNext, sound.time);
        }
    }

    public void playSound(String text) {
        stopSound();
        this.soundQueue.add(text);
        playNextSound();
    }

    public void playSeqSound(String[] sounds) {
        int len = sounds.length;
        for (int i = 0; ; i++) {
            if (i >= len) {
                if (!this.playing)
                    playNextSound();
                return;
            }
            String str = sounds[i];
            this.soundQueue.add(str);
        }
    }

    public void stopSound() {
        this.handler.removeCallbacks(this.playNext);
        this.soundQueue.clear();
        this.soundPool.stop(this.curStreamId);
        this.playing = false;
    }

    public void cleanUp(){
        unloadAll();
        this.soundPool.release();
        this.soundPool = null;
        sMe = null;
    }

    public void unloadAll() {
        stopSound();
        if (this.soundMap.size() > 0) {
            Iterator<String> iterator = soundMap.keySet().iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                Sound sound = soundMap.get(s);
                this.soundPool.unload(sound.id);
            }
        }
    }

    /**
     * @param text
     * @param resId
     * @param time
     */
    private void addSound(String text, int resId, int time) {
        Sound s = new Sound(this.soundPool.load(this.context, resId, 1), time);
        this.soundMap.put(text, s);
    }

    /**
     *
     */
    private final class Sound {
        public int id;
        public int time;

        public Sound(int id, int time) {
            this.id = id;
            this.time = time;
        }
    }
}
