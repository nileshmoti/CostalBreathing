package com.example.costalbreathing

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.wearable.activity.WearableActivity
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : WearableActivity() {

    internal lateinit var countDownTimer: CountDownTimer
    internal var initialCountDown: Long = 60000
    internal var countDownInterval: Long = 1000
    var timerState = false
    lateinit var vibrate: Vibrator
    var VibratePattern = longArrayOf(0, 0, 0, 0)
    var buttonState = true
    var pauseCounterInterval: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBarPauseFunction()
        seekBarTimeFunction()
        buttonStart()
        vibrateInit()



        // Enables Always-on
        //setAmbientEnabled()
    }


    private fun buttonStart(){

        //When button is clicked
        button_start.setOnClickListener {

            // If button is pressed first time then start counter, else stop counter
            if (buttonState == true){

                vibrate.vibrate(customVibratePattern(0, 50, 25, 50), -1)


                button_start.text = "Stop"
                buttonState = false

                //Check if there is a timer that is running, if so close it then start a new one
                if (timerState){
                    countDownTimer.cancel()
                }

                timer()
                countDownTimer.start()

                // Foreground Service
                ForegroundService.startService(this, "")

            } else{

                vibrate.vibrate(customVibratePattern(0, 50, 25, 50), -1)

                button_start.text = "Start"
                buttonState = true

                if (timerState){
                    countDownTimer.cancel()
                }

                // Stop foreground service
                ForegroundService.stopService(this)

            }


        }
    }


    private fun timer(){

        //Set timer state
        timerState = true

        // Reset tick counter
        var tickCounter: Long = 0

        //Create timer instance
        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval){
            override fun onFinish() {
            }

            override fun onTick(millisUntilFinished: Long) {

                val timeLeft = millisUntilFinished / 1000
                textView_output.text = timeLeft.toString()



                tickCounter++

                if (tickCounter >= pauseCounterInterval){

                    vibrate.vibrate(customVibratePattern(0, 75, 25, 75), -1)
                    tickCounter = 0
                }


            }

        }

    }


    private fun seekBarPauseFunction(){

        seekBar_pause.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView_output.text = progress.toString() + " sec"
                pauseCounterInterval = progress.toLong()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }


        })


    }


    private fun seekBarTimeFunction(){
        seekBar_timeDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView_output.text = progress.toString() + " sec"
                initialCountDown = progress.toLong()*1000
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        
    }

    private fun vibrateInit(){
        vibrate = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        //customVibratePattern();
    }


    private fun customVibratePattern(delay: Long, initVibrateOn: Long, vibrationOff: Long, precVibrationOn: Long): LongArray {
        // 0 : Start without a delay
        // 400 : Vibrate for 400 milliseconds
        // 200 : Pause for 200 milliseconds
        // 400 : Vibrate for 400 milliseconds
        VibratePattern = longArrayOf(delay, initVibrateOn, vibrationOff, precVibrationOn)

        // Usage:
        // -1 : Do not repeat this pattern
        // pass 0 if you want to repeat this pattern from 0th index
        //vibrator.vibrate(mVibratePattern, -1)
        return VibratePattern
    }

}




