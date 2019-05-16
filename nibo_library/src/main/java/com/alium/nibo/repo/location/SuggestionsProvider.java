package com.alium.nibo.repo.location;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import com.alium.nibo.R;
import com.alium.nibo.autocompletesearchbar.NiboSearchSuggestionItem;
import com.alium.nibo.domain.Params;
import com.alium.nibo.domain.geocoding.GeocodeAddressUseCase;
import com.alium.nibo.repo.contracts.ISuggestionRepository;
import com.alium.nibo.utils.NiboConstants;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import io.reactivex.observers.DisposableObserver;
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
    private PlacesClient placesClient;
    private GeocodeAddressUseCase geocodeAddressUseCase;
    private Context mContext;

    public SuggestionsProvider(PlacesClient placesClient, Context mContext,
        GeocodeAddressUseCase geocodeAddressUseCase) {
        this.placesClient = placesClient;
        this.mContext = mContext;
        this.geocodeAddressUseCase = geocodeAddressUseCase;
    }

    public Observable<Collection<NiboSearchSuggestionItem>> getSuggestions(final String query) {
        final List<NiboSearchSuggestionItem> placeSuggestionItems = new ArrayList<>();
        return new Observable<Collection<NiboSearchSuggestionItem>>() {
            @Override
            protected void subscribeActual(final Observer<? super Collection<NiboSearchSuggestionItem>> observer) {
                FindAutocompletePredictionsRequest findAutocompletePredictionsRequest =
                    FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .build();
                placesClient.findAutocompletePredictions(findAutocompletePredictionsRequest).addOnSuccessListener(response -> {
                        placeSuggestionItems.clear();
                        List<AutocompletePrediction> autocompletePredictions =
                            response.getAutocompletePredictions();
                        if (autocompletePredictions.isEmpty()) {
                            Params params = Params.create();
                            params.putData(NiboConstants.ADDRESS_PARAM, query);
                            geocodeAddressUseCase.execute(new DisposableObserver<List<Address>>() {
                                @Override public void onNext(List<Address> addresses) {
                                    if (addresses != null && !addresses.isEmpty()) {
                                        for (Address address : addresses) {
                                            String result = null;
                                            StringBuilder sb = new StringBuilder();
                                            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                                sb.append(address.getAddressLine(i));
                                                if (i != address.getMaxAddressLineIndex()) {
                                                    sb.append(", ");
                                                }
                                            }
                                            result = sb.toString();
                                            Log.d(TAG, "geocodeAddressUseCase: onNext: " + result );
                                            NiboSearchSuggestionItem placeSuggestion =
                                                new NiboSearchSuggestionItem(
                                                    result,
                                                    result,
                                                    NiboSearchSuggestionItem.TYPE_SEARCH_ITEM_SUGGESTION,
                                                    mContext.getResources()
                                                        .getDrawable(R.drawable.ic_map_marker_def),
                                                    new LatLng(address.getLatitude(), address.getLongitude())
                                                );
                                            placeSuggestionItems.add(placeSuggestion);
                                        }
                                        observer.onNext(placeSuggestionItems);
                                    } else {
                                      observer.onComplete();
                                    }

                                }

                                @Override public void onError(Throwable e) {
                                    observer.onError(new Throwable(e.getMessage()));
                                }

                                @Override public void onComplete() {

                                }
                            }, params);
                        } else {
                            for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
                                NiboSearchSuggestionItem placeSuggestion =
                                    new NiboSearchSuggestionItem(
                                        autocompletePrediction.getFullText(null).toString(),
                                        autocompletePrediction.getPlaceId(),
                                        NiboSearchSuggestionItem.TYPE_SEARCH_ITEM_SUGGESTION,
                                        mContext.getResources()
                                            .getDrawable(R.drawable.ic_map_marker_def)
                                    );

                                placeSuggestionItems.add(placeSuggestion);
                            }
                        observer.onNext(placeSuggestionItems);
                        }
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
    }
}
