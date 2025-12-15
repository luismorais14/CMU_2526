package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.map

import com.google.android.gms.maps.model.LatLng

/**
 * Modelo de dados que representa um local relacionado com alimentação
 * (ex: restaurante, café) obtido da API de geolocalização.
 *
 * @property name O nome do local.
 * @property location As coordenadas geográficas ([LatLng]) do local.
 * @property description O endereço formatado ou descrição breve do local.
 * @property contact O número de telefone ou outra forma de contacto.
 */
data class FoodPlace(
    val name: String,
    val location: LatLng,
    val description: String,
    val contact: String
)

/**
 * Representa o estado da Interface de Utilizador (UI) para o ecrã do Mapa.
 *
 * Este estado é gerido pelo [MapViewModel] e contém as informações necessárias
 * para exibir o mapa e os locais de interesse.
 *
 * @property isLoading Indica se os locais próximos estão a ser carregados da API.
 * @property foodMarkers Lista de [FoodPlace] a serem exibidos como marcadores no mapa.
 * @property errorMessage Mensagem de erro a ser exibida na UI (nula se não houver erro).
 */
data class MapUiState(
    val isLoading: Boolean = false,
    val foodMarkers: List<FoodPlace> = emptyList(),
    val errorMessage: String? = null
) {
}