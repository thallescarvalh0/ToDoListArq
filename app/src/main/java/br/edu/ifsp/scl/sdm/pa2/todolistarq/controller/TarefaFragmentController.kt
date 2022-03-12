package br.edu.ifsp.scl.sdm.pa2.todolistarq.controller

import android.os.AsyncTask
import android.provider.Settings
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.TarefaFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TarefaFragmentController(private val tarefaFragment: TarefaFragment) {
    private val database: ToDoListArqDatabase
    init {
        database = Room.databaseBuilder(
            tarefaFragment.requireContext(),
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()
    }

    fun atualizaTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().atualizarTarefa(tarefa)
            tarefaFragment.retornaTarefa(tarefa)
        }
    }

    fun insereTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().inserirTarefa(tarefa)
            tarefaFragment.retornaTarefa(
                Tarefa(
                    tarefa.id,
                    tarefa.nome,
                    tarefa.realizada
                )
            )
        }
    }
}