package ipp.estg.cmu_2526_8230258_8230204_8230153.presentation.diary

import java.time.LocalDate

/**
 * Define a hierarquia de eventos (intenções) que podem ocorrer no ecrã do Diário Alimentar.
 *
 * Estes eventos são disparados pela UI e enviados para o [DiaryViewModel] para processamento
 * e atualização do estado do diário.
 */
sealed interface DiaryEvent {

    /**
     * Evento disparado quando o utilizador seleciona uma nova data no seletor.
     *
     * Nota: O ViewModel será responsável por implementar as verificações de limites de data
     * (ex: não permitir datas futuras ou demasiado antigas).
     *
     * @property date A nova data ([LocalDate]) selecionada.
     */
    data class DateSelected(val date : LocalDate) : DiaryEvent

    /**
     * Evento disparado para adicionar uma nova refeição vazia (ou usar uma existente) a um dia.
     *
     * @property mealType O [MealType] da nova refeição a adicionar (ex: LUNCH).
     */
    data class MealAdded(val mealType : MealType) : DiaryEvent

    /**
     * Evento disparado para remover uma refeição completa.
     *
     * @property mealId O ID da refeição a ser removida.
     */
    data class MealDeleted(val mealId : String) : DiaryEvent

    /**
     * Evento disparado para adicionar um novo alimento a uma refeição existente.
     *
     * Nota: A obtenção dos detalhes nutricionais do [foodName] é responsabilidade
     * do ViewModel e dos repositórios.
     *
     * @property mealId O ID da refeição onde o alimento será adicionado.
     * @property foodName O nome do alimento a ser adicionado.
     */
    data class FoodAdded(val mealId : String, val foodName : String) : DiaryEvent

    /**
     * Evento disparado para remover um alimento específico de uma refeição.
     *
     * @property mealId O ID da refeição que contém o alimento.
     * @property foodId O ID do alimento a ser removido.
     */
    data class FoodDeleted(val mealId : String, val foodId : String) : DiaryEvent

    /**
     * Evento disparado para solicitar um novo carregamento dos dados diários.
     * (Pode ser usado para forçar um refresh).
     */
    object LoadDailyData : DiaryEvent
}