package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.map

import com.google.android.gms.maps.model.LatLng

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã do Mapa.
 *
 * Estes eventos são disparados pela UI ou pelo sistema (ex: atualizações de localização)
 * e são enviados para o [MapViewModel] para processamento.
 */
sealed interface MapEvent {

    /**
     * Evento disparado quando o utilizador clica na ação de registar um local de comida.
     * (Reservado para funcionalidades futuras).
     */
    object OnRegisterFoodLocationClick : MapEvent

    /**
     * Evento disparado quando o sistema (GPS) fornece uma nova localização do utilizador.
     *
     * Este evento aciona a pesquisa de locais próximos na API.
     * @property location A nova coordenada geográfica ([LatLng]) do utilizador.
     */
    data class OnLocationUpdate(val location: LatLng) : MapEvent
}