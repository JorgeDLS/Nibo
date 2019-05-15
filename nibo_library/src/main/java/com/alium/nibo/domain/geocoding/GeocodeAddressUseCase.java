package com.alium.nibo.domain.geocoding;

import com.alium.nibo.domain.Params;
import com.alium.nibo.domain.base.BaseUseCase;
import com.alium.nibo.repo.contracts.IGeoCodingRepository;
import com.alium.nibo.utils.NiboConstants;
import io.reactivex.Observable;

public class GeocodeAddressUseCase extends BaseUseCase {

    IGeoCodingRepository geoCodingRepository;

    public GeocodeAddressUseCase(IGeoCodingRepository geoCodingRepository) {
        this.geoCodingRepository = geoCodingRepository;
    }

    @Override
    protected Observable getObservable(Params params) {
        String address = params.getString(NiboConstants.ADDRESS_PARAM, null);
        return geoCodingRepository.getObservableAddressStringFromAddress(address);
    }
}
