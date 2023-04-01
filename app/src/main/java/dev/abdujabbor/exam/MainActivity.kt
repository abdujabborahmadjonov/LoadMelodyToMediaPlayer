package dev.abdujabbor.exam

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import dev.abdujabbor.exam.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var mediaPlayer: MediaPlayer
    lateinit var animation: Animation
    companion object {
        var songPosition: Int = 0
        var isPlaying: Boolean = false

        @SuppressLint("StaticFieldLeak")
        var repeat: Boolean = false
    }
    val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //ringtonManager

        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val ringtoneTitle = RingtoneManager.getRingtone(this, defaultRingtoneUri).getTitle(this)
        binding.songName.text = ringtoneTitle
        mediaPlayer = MediaPlayer.create(this, defaultRingtoneUri)
        mediaPlayer.start()
        animation = AnimationUtils.loadAnimation(this, R.anim.rotation)
        binding.image.animation = animation
        if (mediaPlayer.currentPosition / 1000 % 60 < 10) binding.seekBarStartPA.text =
            "${mediaPlayer.currentPosition / 1000 / 60}:0${mediaPlayer.currentPosition / 1000 % 60}"
        else binding.seekBarStartPA.text =
            "${mediaPlayer.currentPosition / 1000 / 60}:${mediaPlayer.currentPosition / 1000 % 60}"

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                binding.seekBarPA.progress = mediaPlayer.currentPosition
                handler.postDelayed(this, 100)
            }
        }, 100)
        binding.playPauseBtn.setOnClickListener {
            if (!isPlaying) playMusic()
            else pauseMusic()
        }
        var minut =""
        var second =""
        val duration = mediaPlayer.duration
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                TimeUnit.MINUTES.toSeconds(minutes)
        minut = if (minutes.toString().length==1){
            "0$minutes"
        }else{
            minutes.toString()
        }
        second = if (seconds.toString().length==1){
            "0$seconds"
        }else{
            seconds.toString()
        }
        binding.duration.text = "$minut:$second"
        binding.seekBarPA.max = mediaPlayer.duration
        handler.postDelayed(object : Runnable {
            override fun run() {
                binding.seekBarPA.progress = mediaPlayer.currentPosition
                if (mediaPlayer.currentPosition / 1000 % 60 < 10) binding.seekBarStartPA.text =
                    "${mediaPlayer.currentPosition / 1000 / 60}:0${mediaPlayer.currentPosition / 1000 % 60}"
                else binding.seekBarStartPA.text =
                    "${mediaPlayer.currentPosition / 1000 / 60}:${mediaPlayer.currentPosition / 1000 % 60}"

                handler.postDelayed(this, 1000)
            }
        }, 1000)
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                    if (mediaPlayer.currentPosition / 1000 % 60 < 10) binding.seekBarStartPA.text =
                        "${mediaPlayer.currentPosition / 1000 / 60}:0${mediaPlayer.currentPosition / 1000 % 60}"
                    else binding.seekBarStartPA.text =
                        "${mediaPlayer.currentPosition / 1000 / 60}:${mediaPlayer.currentPosition / 1000 % 60}"

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Not used
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Not used
            }
        })

        seekVolume()
    }
    fun seekVolume() {
        binding.seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val audioManager =
                    this@MainActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume: Int =
                    progress * maxVolume / 100 // Scale the progress to the volume range
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    private fun playMusic() {
        mediaPlayer.start()
        binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
        binding.image.startAnimation(animation)
        isPlaying = true
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
        binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        binding.image.clearAnimation()
        isPlaying = false
    }


}