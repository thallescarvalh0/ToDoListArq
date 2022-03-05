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
}