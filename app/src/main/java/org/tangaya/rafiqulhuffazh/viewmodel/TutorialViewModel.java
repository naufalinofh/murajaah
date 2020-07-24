package org.tangaya.rafiqulhuffazh.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableField;
import android.util.Log;

import org.tangaya.rafiqulhuffazh.view.navigator.TutorialNavigator;

// todo: view
public class TutorialViewModel extends AndroidViewModel {

    TutorialNavigator mNavigator;

    public final ObservableField<String> tutorialText = new ObservableField<>();

    public TutorialViewModel(Application context) {
        super(context);

        tutorialText.set("Press above button to record");
        Log.d("TVM", tutorialText.get());
    }

    public void onActivityCreated(TutorialNavigator navigator) {
        Log.d("TVM", "onActivityCreated called");
        mNavigator = navigator;
    }

    public void skipTutorial() {
        Log.d("TVM", "skipTutorial() invoked");
        mNavigator.skipTutorial();
    }

}