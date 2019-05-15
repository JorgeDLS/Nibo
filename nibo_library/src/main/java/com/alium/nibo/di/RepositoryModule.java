package com.alium.nibo.di;

import android.content.Context;
import com.alium.nibo.repo.contracts.IDirectionsRepository;
import com.alium.nibo.repo.contracts.IGeoCodingRepository;
import com.alium.nibo.repo.directions.DirectionsRepository;
import com.alium.nibo.repo.location.GeoCodingRepository;

/**
 * Created by aliumujib on 03/05/2018.
 */

public class RepositoryModule {

    private static RepositoryModule repositoryModule;
    private final Context context;

    public static RepositoryModule getInstance(APIModule apiModule, Context context) {
        if (repositoryModule == null) {
            repositoryModule = new RepositoryModule(apiModule, context);
        }
        return repositoryModule;
    }

    private IDirectionsRepository directionsRepository;
    private IGeoCodingRepository geoCodingRepository;

    APIModule apiModule;

    private RepositoryModule(APIModule apiModule, Context context) {
        this.apiModule = apiModule;
        this.context = context;
    }

    public IDirectionsRepository getDirectionsRepository() {
        if (directionsRepository == null) {
            directionsRepository = new DirectionsRepository(apiModule.getDirectionsAPI());
        }
        return directionsRepository;
    }

    public IGeoCodingRepository getGeoCodingRepository() {
        if (geoCodingRepository == null) {
            geoCodingRepository = new GeoCodingRepository(apiModule.getGeocodeAPI(), context);
        }
        return geoCodingRepository;
    }



}
