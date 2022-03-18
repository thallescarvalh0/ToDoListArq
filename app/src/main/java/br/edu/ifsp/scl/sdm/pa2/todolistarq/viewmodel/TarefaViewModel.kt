package br.edu.ifsp.scl.sdm.pa2.todolistarq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TarefaViewModel(application: Application): AndroidViewModel(application) {
    private val database: ToDoListArqDatabase
    init {
        database = Room.databaseBuilder(
            application.baseContext,
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()
    }
    private val listaTarefasMld: MutableLiveData<MutableList<Tarefa>> = MutableLiveData()
    private val tarefaMld: MutableLiveData<Tarefa> = MutableLiveData()

    //Funcoes para recuperar os observaveis
    fun recuperarListaTarefas() = listaTarefasMld
    fun recuperarTarefa() = tarefaMld

    //Funçõe de acesso ao datasource
    fun atualizaTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().atualizarTarefa(tarefa)
            tarefaMld.postValue(tarefa)
            //tarefaView.retornaTarefa(tarefa)
        }
    }

    fun insereTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().inserirTarefa(tarefa)
            tarefaMld.postValue(
                Tarefa(
                tarefa.id,
                tarefa.nome,
                tarefa.realizada
            ))
            /*tarefaView.retornaTarefa(
                Tarefa(
                    tarefa.id,
                    tarefa.nome,
                    tarefa.realizada
                )
            )*/
        }
    }

    fun buscarTarefas() {
        GlobalScope.launch {
            val listaTarefas = database.getTarefaDao().recuperarTarefas()
            listaTarefasMld.postValue(listaTarefas.toMutableList())
            //tarefaView.atualizarListaTarefas(listaTarefas.toMutableList())
        }
    }

    fun removerTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            database.getTarefaDao().removerTarefa(tarefa)
        }
    }
}