package tipcalculator.gohool.musicbox;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;
    private ImageView imageView;
    private SeekBar seekBar;
    private TextView lefttime;
    private TextView righttime;
    private Button prevBtn;
    private Button playBtn;
    private Button nextBtn;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUi();

        //setmax : mediaPlayer의 길이(int)값을 받아서 그 길이 값을 세팅함
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               // 시크바 View / 변경된 값 / 사용자에 의한 변경인지(True), 코드에 의한 변경인지(False)
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                //현재위치
                int currentPos = mediaPlayer.getCurrentPosition();
                //길이
                int duration = mediaPlayer.getDuration();

                lefttime.setText(dateFormat.format(new Date(currentPos)));
                righttime.setText((dateFormat.format(new Date(duration - currentPos))));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setUpUi(){

        //mediaPlayer 메소드 사용법  간 - 단
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.music);


        imageView = (ImageView) findViewById(R.id.imageView1);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        lefttime = (TextView) findViewById(R.id.lefttime);
        righttime = (TextView) findViewById(R.id.righttime);
        prevBtn = (Button) findViewById(R.id.prevbtn);
        playBtn = (Button) findViewById(R.id.playbtn);
        nextBtn = (Button) findViewById(R.id.nextbtn);

        prevBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prevbtn:
                musicReset();
                break;
            case R.id.playbtn:
                //음악이 실행중이라면 버튼을 눌렀을떄, 정지. 아니면 실행
                if(mediaPlayer.isPlaying()){
                    musicPause();
                } else {
                    musicStart();
                }

                break;
            case R.id.nextbtn:
                musicNext();
                break;
        }
    }
    //mediaplayer 작동 컨트롤
    public void musicPause(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            //버튼 icon을 버튼 클릭시 이벤트를 받아서 바꿔줌.
            playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }
    public void musicStart(){
        if(mediaPlayer != null){
            mediaPlayer.start();
            playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
            updateThread();
        }
    }
    public void musicNext(){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void musicReset(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
    }

    //스레드 : 프로세스내에서 순차적으로 실행되는 실행 흐름
    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                //runOnUiThread : 현재 스레드가 UI 스레드라면 UI 자원을 사용하는 행동에 대해서는 즉시 실행
                //runable 인터페이스. run만 쓰는 경우.
                try {
                    while(mediaPlayer != null && mediaPlayer.isPlaying()){

                        //1초
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                // update text
                                lefttime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getCurrentPosition()))));

                                righttime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));
                            }
                    });
                }} catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}