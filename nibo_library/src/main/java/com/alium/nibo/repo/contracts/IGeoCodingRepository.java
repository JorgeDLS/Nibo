package com.alium.nibo.repo.contracts;

import android.location.Address;
import io.reactivex.Observable;
import java.util.List;

/**
 * Created by aliumujib on 03/05/2018.
 */

public interface IGeoCodingRepository {

    Observable<List<Address>> getObservableAddressStringFromAddress(final String address);

    Observable<String> getObservableAddressStringFromLatLng(final double latitude, final double longitude, String apiKey);

}
