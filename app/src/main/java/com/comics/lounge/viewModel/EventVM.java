package com.comics.lounge.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comics.lounge.modals.Event;
import com.comics.lounge.modals.EventNew;

public class EventVM extends ViewModel {
    private final MutableLiveData<EventNew> event = new MutableLiveData<>();

    public void isChecked(EventNew eventNew) {
        event.setValue(eventNew);
    }

    public LiveData<EventNew> getSelected() {
        return event;
    }
}
