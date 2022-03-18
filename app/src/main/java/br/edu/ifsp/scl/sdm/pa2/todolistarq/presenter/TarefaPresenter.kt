package br.edu.ifsp.scl.sdm.pa2.todolistarq.presenter

import androidx.fragment.app.Fragment
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TarefaPresenter(private val tarefaView: TarefaView) {
    private val database: ToDoListArqDatabase
    init {
        database = Room.databaseBuilder(
            (tarefaView as Fragment).requireContext(),
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()
    }

    fun atualizaTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().atualizarTarefa(tarefa)
            tarefaView.retornaTarefa(tarefa)
        }
    }

    fun insereTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().inserirTarefa(tarefa)
            tarefaView.retornaTarefa(
                Tarefa(
                    tarefa.id,
                    tarefa.nome,
                    tarefa.realizada
                )
            )
        }
    }

    fun buscarTarefas() {
        GlobalScope.launch {
            val listaTarefas = database.getTarefaDao().recuperarTarefas()
            tarefaView.atualizarListaTarefas(listaTarefas.toMutableList())
        }
    }

    fun removerTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().removerTarefa(tarefa)
        }
    }

    interface TarefaView{
        fun atualizarListaTarefas(listaTarefas: MutableList<Tarefa>)
        fun retornaTarefa(tarefa: Tarefa)
    }
}