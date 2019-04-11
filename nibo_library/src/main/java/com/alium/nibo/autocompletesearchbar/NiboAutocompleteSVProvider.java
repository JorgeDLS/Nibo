package com.alium.nibo.autocompletesearchbar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.places.api.net.PlacesClient;

/**
 * Created by abdulmujibaliu on 9/9/17.
 */

public interface NiboAutocompleteSVProvider {

    GoogleApiClient getGoogleApiClient();

    PlacesClient getPlacesClient();

    void onHomeButtonClicked();

    NiboPlacesAutoCompleteSearchView.SearchListener getSearchListener();

    boolean getShouldUseVoice();

}
