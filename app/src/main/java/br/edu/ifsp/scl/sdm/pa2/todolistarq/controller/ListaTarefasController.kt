package br.edu.ifsp.scl.sdm.pa2.todolistarq.controller

import android.os.AsyncTask
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.ListaTarefasFragment

class ListaTarefasController(private val listaTarefasFragment: ListaTarefasFragment) {
    private val database: ToDoListArqDatabase
    init {
        database = Room.databaseBuilder(
            listaTarefasFragment.requireContext(),
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()
    }

    fun buscarTarefas() {
        object: AsyncTask<Unit, Unit, List<Tarefa>>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg p0: Unit?): List<Tarefa> {
                return database.getTarefaDao().recuperarTarefas()
            }

            override fun onPostExecute(result: List<Tarefa>?) {
                super.onPostExecute(result)
                val listaTarefas = mutableListOf<Tarefa>()
                result?.forEach{ tarefa ->
                    listaTarefas.add(tarefa)
                }
                listaTarefasFragment.atualizarListaTarefas(listaTarefas)
            }
        }.execute()
    }

    fun removerTarefa(tarefa: Tarefa){
        val listaTarefas = mutableListOf<Tarefa>()
        object: AsyncTask<Tarefa, Unit, List<Tarefa>>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Tarefa?): List<Tarefa>? {

                params?.forEach { tarefaParams ->
                    if (tarefaParams != null) {
                        if (tarefaParams.id == tarefa.id){
                            database.getTarefaDao().removerTarefa(tarefa)
                        }
                    }
                }
                return database.getTarefaDao().recuperarTarefas()
            }

            override fun onPostExecute(result: List<Tarefa>?) {
                super.onPostExecute(result)
                result?.forEach{ tarefaParams ->
                    if (tarefaParams.id != tarefa.id){
                        listaTarefas.add(tarefaParams)
                    }
                }
                listaTarefasFragment.atualizarListaTarefas(listaTarefas)
            }
        }.execute(tarefa)
    }
}