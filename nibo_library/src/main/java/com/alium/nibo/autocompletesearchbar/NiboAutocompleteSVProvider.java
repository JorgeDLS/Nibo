package com.alium.nibo.autocompletesearchbar;

import com.google.android.libraries.places.api.net.PlacesClient;

/**
 * Created by abdulmujibaliu on 9/9/17.
 */

public interface NiboAutocompleteSVProvider {

    PlacesClient getPlacesClient();

    void onHomeButtonClicked();

    NiboPlacesAutoCompleteSearchView.SearchListener getSearchListener();

    boolean getShouldUseVoice();

}
