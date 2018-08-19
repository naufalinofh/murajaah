package org.tangaya.rafiqulhuffazh.view.ui;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.tangaya.rafiqulhuffazh.R;
import org.tangaya.rafiqulhuffazh.data.model.EvaluationOld;
import org.tangaya.rafiqulhuffazh.data.service.MyAudioPlayer;
import org.tangaya.rafiqulhuffazh.data.service.MyAudioRecorder;
import org.tangaya.rafiqulhuffazh.databinding.ActivityDevspaceBinding;
import org.tangaya.rafiqulhuffazh.util.AudioFileHelper;
import org.tangaya.rafiqulhuffazh.view.navigator.DevspaceNavigator;
import org.tangaya.rafiqulhuffazh.viewmodel.DevspaceViewModel;

import java.util.ArrayList;

import timber.log.Timber;

public class DevspaceActivity extends Activity implements LifecycleOwner, DevspaceNavigator {

    public DevspaceViewModel mViewModel;
    private ActivityDevspaceBinding binding;
    private LifecycleRegistry mLifecycleRegistry;

    private MyAudioRecorder mRecorder = MyAudioRecorder.getInstance();
    MyAudioPlayer mPlayer = new MyAudioPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Development Space");

        mViewModel = DevspaceViewModel.getIntance(this.getApplication());
        mViewModel.onActivityCreated(this);

        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);

        final Observer<String> serverStatusObserver = new Observer<String>() {

            @Override
            public void onChanged(@Nullable String serverStatus) {
                Timber.d("server status has been changed ==> " + serverStatus);
                mViewModel.serverStatus.set(serverStatus);
            }
        };
        mViewModel.getServerListener().getStatus().observe(this, serverStatusObserver);

        final Observer<Integer> numWorkerObserver = new Observer<Integer>() {

            @Override
            public void onChanged(@Nullable Integer numAvailWorkers) {
                Timber.d("num worker has been changed ==> " + numAvailWorkers);
                mViewModel.numAvailableWorkers.set(numAvailWorkers);
                if (numAvailWorkers>0) {
                    mViewModel.dequeueRecognitionTasks();
                }
            }
        };
        mViewModel.getServerListener().getNumWorkersAvailable().observe(this, numWorkerObserver);

        final Observer<ArrayList<EvaluationOld>> evalsObserver = new Observer<ArrayList<EvaluationOld>>() {
            @Override
            public void onChanged(@Nullable ArrayList<EvaluationOld> evaluations) {
                Timber.d("eval set has changed");

            }
        };

        mViewModel.getEvalsMutableLiveData().observe(this, evalsObserver);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_devspace);
        binding.setViewmodel(mViewModel);
     }

    @Override
    public void onStart() {
        super.onStart();
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    public void gotoScoreboard() {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    @Override
    public void onStartRecording(int surah, int ayah) {
        mRecorder.setOutputFile(AudioFileHelper.getUserRecordingFilePath(surah, ayah));
        mRecorder.prepare();
        mRecorder.start();
        Timber.d("onStartRecording");
    }

    @Override
    public void onStopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        Timber.d("onStopRecording");
    }

    @Override
    public void onPlayRecording(int surah, int ayah) {
        mPlayer.play(MyAudioPlayer.Source.RECORDING, surah, ayah);
    }

    @Override
    public void onPlayTestFile(int surah, int ayah) {
        mPlayer.play(MyAudioPlayer.Source.QARI1, surah, ayah);
    }
}
