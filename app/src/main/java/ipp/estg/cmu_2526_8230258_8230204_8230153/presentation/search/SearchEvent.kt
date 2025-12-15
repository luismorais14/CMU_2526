package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.search

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã de Pesquisa.
 *
 * Estes eventos são capturados pela UI (SearchScreen) e enviados para o [SearchViewModel]
 * para processamento, seguindo o padrão MVI (Model-View-Intent) ou fluxo unidirecional.
 */
sealed interface SearchEvent {

    /**
     * Evento disparado sempre que o utilizador altera o texto na barra de pesquisa.
     *
     * @property query O novo texto contido na barra de pesquisa.
     */
    data class OnQueryChanged(val query: String) : SearchEvent

    /**
     * Evento disparado quando o utilizador confirma explicitamente a pesquisa
     * (por exemplo, ao pressionar o botão de "Pesquisar" ou "Enter" no teclado virtual).
     */
    object OnSearch : SearchEvent

    /**
     * Evento disparado quando o utilizador clica num item específico da lista de resultados
     * com a intenção de o adicionar à refeição atual.
     *
     * @property food O objeto [FoodItem] correspondente ao alimento selecionado.
     */
    data class OnFoodClicked(val food: FoodItem) : SearchEvent
}