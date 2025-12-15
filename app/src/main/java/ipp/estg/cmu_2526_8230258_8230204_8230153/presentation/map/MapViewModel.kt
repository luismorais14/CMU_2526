package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ipp.estg.cmu_2526_8230258_8230204_8230153.BuildConfig
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.RetrofitHelper
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica do ecrã do Mapa.
 *
 * Gere a localização do utilizador, a comunicação com a API de geolocalização (Geoapify)
 * para obter locais próximos (restaurantes/cafés) e mantém o estado da UI ([MapUiState]).
 */
class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    /**
     * Estado da UI do mapa, observável pelo Composable [MapScreen].
     */
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val apiKey = BuildConfig.GEOAPIFY_KEY

    /**
     * Processa os eventos de interação da UI e atualizações de localização.
     *
     * @param event O evento ocorrido (ex: localização atualizada).
     */
    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.OnLocationUpdate -> {
                fetchNearbyPlaces(event.location)
            }

            MapEvent.OnRegisterFoodLocationClick -> {
            }
        }
    }

    /**
     * Faz uma chamada à API Geoapify para obter restaurantes e cafés num raio
     * de 5 km da localização fornecida.
     *
     * @param location A coordenada (Latitude e Longitude) atual do utilizador.
     */
    private fun fetchNearbyPlaces(location: LatLng) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val filter = "circle:${location.longitude},${location.latitude},5000"
                val categories = "catering.restaurant,catering.cafe"

                val response = RetrofitHelper.geoapifyApi.getPlaces(
                    categories = categories,
                    filter = filter,
                    limit = 20,
                    apiKey = apiKey
                )

                val newMarkers = response.features.map {
                        feature ->
                    val placeName = feature.properties.name ?: "Local Desconhecido"
                    val placeDesc = feature.properties.formatted ?: "Endereço não disponível"
                    val placeContact = feature.properties.contact?.phone ?: "Contacto não disponível"

                    FoodPlace(
                        name = placeName,
                        location = LatLng(feature.properties.lat, feature.properties.lon),
                        description = placeDesc,
                        contact = placeContact
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        foodMarkers = newMarkers
                    )
                }

            } catch (e: Exception) {
                Log.e("MapViewModel", "Erro na API Geoapify: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Falha ao carregar locais: ${e.message}"
                    )
                }
            }
        }
    }
}