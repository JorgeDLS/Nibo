package com.alium.nibo.repo.contracts;

import com.alium.nibo.autocompletesearchbar.NiboSearchSuggestionItem;
import com.google.android.libraries.places.api.model.Place;

import java.util.Collection;

import io.reactivex.Observable;

/**
 * Created by aliumujib on 05/05/2018.
 */

public interface ISuggestionRepository {

    Observable<Collection<NiboSearchSuggestionItem>> getSuggestions(final String query);

    Observable<Place> getPlaceByID(final String placeId);

    void stop();
}
