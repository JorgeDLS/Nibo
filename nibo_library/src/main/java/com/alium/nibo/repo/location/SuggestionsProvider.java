package com.alium.nibo.repo.location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alium.nibo.R;
import com.alium.nibo.autocompletesearchbar.NiboSearchSuggestionItem;
import com.alium.nibo.repo.contracts.ISuggestionRepository;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdulmujibaliu on 9/8/17.
 */

public class SuggestionsProvider implements ISuggestionRepository {


    private String TAG = getClass().getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private PlacesClient placesClient;
    private Context mContext;

    public SuggestionsProvider(GoogleApiClient mGoogleApiClient,
        PlacesClient placesClient, Context mContext) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.placesClient = placesClient;
        this.mContext = mContext;
    }

    public Observable<Collection<NiboSearchSuggestionItem>> getSuggestions(final String query) {
        final List<NiboSearchSuggestionItem> placeSuggestionItems = new ArrayList<>();

        if (mGoogleApiClient == null) {
            Log.d(TAG, "Google play services cannot be null");
        }

        return new Observable<Collection<NiboSearchSuggestionItem>>() {
            @Override
            protected void subscribeActual(final Observer<? super Collection<NiboSearchSuggestionItem>> observer) {
                FindAutocompletePredictionsRequest findAutocompletePredictionsRequest =
                    FindAutocompletePredictionsRequest.builder().setQuery(query).build();
                placesClient.findAutocompletePredictions(findAutocompletePredictionsRequest).addOnSuccessListener(response -> {
                        placeSuggestionItems.clear();
                        List<AutocompletePrediction> autocompletePredictions =
                            response.getAutocompletePredictions();
                        for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
                            NiboSearchSuggestionItem placeSuggestion = new NiboSearchSuggestionItem(
                                autocompletePrediction.getFullText(null).toString(),
                                autocompletePrediction.getPlaceId(), NiboSearchSuggestionItem.TYPE_SEARCH_ITEM_SUGGESTION,
                                mContext.getResources().getDrawable(R.drawable.ic_map_marker_def)
                            );

                            placeSuggestionItems.add(placeSuggestion);
                        }
                        observer.onNext(placeSuggestionItems);
                    }).addOnFailureListener(e -> observer.onError(new Throwable(e.getMessage())));
            }
        };
    }


    public Observable<Place> getPlaceByID(final String placeId) {
        return new Observable<Place>() {
            @Override
            protected void subscribeActual(final Observer<? super Place> observer) {
                FetchPlaceRequest fetchPlaceRequest =
                    FetchPlaceRequest.builder(placeId, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                    .build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(result -> {
                    Place place = result.getPlace();
                    LatLng queriedLocation = place.getLatLng();
                    Log.v("Latitude is", "" + queriedLocation.latitude);
                    Log.v("Longitude is", "" + queriedLocation.longitude);
                    observer.onNext(place);
                });
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void stop() {
        mContext = null;
        mGoogleApiClient = null;
    }


}
