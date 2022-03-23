package br.edu.ifsp.scl.sdm.pa2.todolistarq.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.service.BuscarTarefaService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TarefaViewModel(application: Application): AndroidViewModel(application) {
    /*private val database: ToDoListArqDatabase // a requisição é feita agora no buscarTarefaService
    init {
           database = Room.databaseBuilder(
               application.baseContext,
               ToDoListArqDatabase::class.java,
               ToDoListArqDatabase.Constantes.DB_NAME
           ).build()
      }*/
    companion object {
        val ACTION_ATUALIZAR = "ACTION_ATUALIZAR"
        val ACTION_INSERIR = "ACTION_INSERIR"
        val ACTION_REMOVER = "ACTION_REMOVER"
        val ACTION_BUSCAR = "ACTION_BUSCAR"

        val EXTRA_ATUALIZAR = "ATUALIZAR"
        val EXTRA_INSERIR = "INSERIR"
        val EXTRA_REMOVER = "REMOVER"
        val EXTRA_BUSCAR = "BUSCAR"

    }

    private val listaTarefasMld: MutableLiveData<MutableList<Tarefa>> = MutableLiveData()
    private val tarefaMld: MutableLiveData<Tarefa> = MutableLiveData()

    //Funcoes para recuperar os observaveis
    fun recuperarListaTarefas() = listaTarefasMld
    fun recuperarTarefa() = tarefaMld

    //Funçõe de acesso ao datasource
    fun atualizaTarefa(tarefa: Tarefa){
        val tarefaServiceIntent = Intent(ACTION_ATUALIZAR,
            Uri.EMPTY,
            getApplication(), BuscarTarefaService::class.java).also {
            it.putExtra(EXTRA_ATUALIZAR, tarefa)
        }
        getApplication<Application>().startService(tarefaServiceIntent)
    }

    fun insereTarefa(tarefa: Tarefa){
        val tarefaServiceIntent = Intent(ACTION_INSERIR,
            Uri.EMPTY,
            getApplication(), BuscarTarefaService::class.java).also {
            it.putExtra(EXTRA_INSERIR, tarefa)
        }
        getApplication<Application>().startService(tarefaServiceIntent)
    }

    fun buscarTarefas() {
        val tarefaServiceIntent = Intent(ACTION_BUSCAR,
            Uri.EMPTY,
            getApplication(),
            BuscarTarefaService::class.java)
        getApplication<Application>().startService(tarefaServiceIntent)
    }

    fun removerTarefa(tarefa: Tarefa){
        val tarefaServiceIntent = Intent(ACTION_REMOVER,
            Uri.EMPTY,
            getApplication(),
            BuscarTarefaService::class.java).also {
                it.putExtra(EXTRA_REMOVER, tarefa)
        }
        getApplication<Application>().startService(tarefaServiceIntent)
    }
}