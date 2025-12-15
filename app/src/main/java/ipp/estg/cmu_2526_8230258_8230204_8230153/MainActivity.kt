package ipp.estg.cmu_2526_8230258_8230204_8230153

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.AppDatabase
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.local.UserPreferences
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.FirebaseAuthSource
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.FirestoreSource
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.repository.AuthRepository
import ipp.estg.cmu_2526_8230258_8230204_8230153.navigation.AppNavHost
import ipp.estg.cmu_2526_8230258_8230204_8230153.ui.theme.CMU_2526_8230258_8230204_8230153Theme

/**
 * O ponto de entrada principal da aplicação.
 *
 * Esta atividade é responsável por inicializar as dependências principais, incluindo a base de dados local,
 * as preferências do utilizador e as fontes de dados remotas. Configura o [AuthRepository] e inicia
 * o grafo de navegação principal utilizando o [AppNavHost].
 */
class MainActivity : ComponentActivity() {

    /**
     * Chamado quando a atividade está a iniciar.
     *
     * Este método inicializa a camada de dados da aplicação (Base de dados Room, DataStore, Firebase)
     * e configura o conteúdo da UI utilizando Jetpack Compose.
     *
     * @param savedInstanceState Se a atividade estiver a ser reinicializada após ter sido encerrada,
     * este Bundle contém os dados que forneceu mais recentemente em onSaveInstanceState.
     * Caso contrário, é nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(applicationContext)
        val userPreferences = UserPreferences(applicationContext)
        val authSource = FirebaseAuthSource()
        val firestoreSource = FirestoreSource()

        val authRepository = AuthRepository(
            firestoreSource = firestoreSource,
            firebaseAuthSource = authSource,
            userPreferences = userPreferences,
            mealDao = database.mealDao()
        )

        setContent {
            CMU_2526_8230258_8230204_8230153Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        authRepository = authRepository
                    )
                }
            }
        }
    }
}