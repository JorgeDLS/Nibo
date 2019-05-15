package com.alium.nibo.di;

import android.content.Context;

import com.alium.nibo.domain.geocoding.GeocodeAddressUseCase;
import com.alium.nibo.repo.contracts.IGeoCodingRepository;
import com.alium.nibo.repo.location.GeoCodingRepository;
import com.alium.nibo.repo.location.LocationRepository;
import com.alium.nibo.repo.location.SuggestionsProvider;
import com.google.android.libraries.places.api.net.PlacesClient;

/**
 * Created by aliumujib on 05/05/2018.
 */

public class ProviderModule {

    private final PlacesClient placesClient;
    private final Context context;

    private SuggestionsProvider suggestionsProvider;
    private LocationRepository locationRepository;
    private IGeoCodingRepository geoCodingRepository;

    public ProviderModule(PlacesClient placesClient, Context context) {
        this.placesClient = placesClient;
        this.context = context;
    }

    public SuggestionsProvider getSuggestionsProvider() {
        if (suggestionsProvider == null) {
            suggestionsProvider = new SuggestionsProvider(placesClient, context,
                new GeocodeAddressUseCase(getGeoCodingRepository()));
        }
        return suggestionsProvider;
    }

    public LocationRepository getLocationRepository() {
        if (locationRepository == null) {
            locationRepository = new LocationRepository(context);
        }
        return locationRepository;
    }

    public IGeoCodingRepository getGeoCodingRepository() {
        if (geoCodingRepository == null) {
            geoCodingRepository = new GeoCodingRepository(
                APIModule.getInstance(
                    RetrofitModule.getInstance()).getGeocodeAPI(), context);
        }
        return geoCodingRepository;
    }

}
