package com.alium.nibo.repo.contracts;

import android.location.Location;

import com.google.android.libraries.places.api.model.Place;

import io.reactivex.Observable;

/**
 * Created by abdulmujibaliu on 9/3/17.
 */

public interface ILocationRepository {

    Observable<Location> getLocationObservable();

}
