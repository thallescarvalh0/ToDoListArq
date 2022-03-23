package br.edu.ifsp.scl.sdm.pa2.todolistarq.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Parcelable
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa

class TarefaService : Service() {
    private lateinit var database: ToDoListArqDatabase

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

    private inner class WorkerThread(val intent: Intent): Thread(){
        override fun run() {
            val extraOperacao = intent.extras
            when(intent.action){
                ACTION_ATUALIZAR -> {
                    val tarefa = (extraOperacao?.get(EXTRA_ATUALIZAR) as Tarefa)
                    val retorno = atualizaTarefa(tarefa)
                    sendBroadcast(Intent(ACTION_ATUALIZAR).also {
                        it.putExtra(EXTRA_ATUALIZAR, retorno)
                    })
                }
                ACTION_INSERIR -> {
                    val tarefa = (extraOperacao?.get(EXTRA_INSERIR) as Tarefa)
                    sendBroadcast(Intent(ACTION_INSERIR).also {
                        it.putExtra(EXTRA_INSERIR, insereTarefa(tarefa))
                    })
                }
                ACTION_REMOVER -> {
                    val tarefa = (extraOperacao?.get(EXTRA_REMOVER) as Tarefa)
                    removerTarefa(tarefa)
                    sendBroadcast(Intent(ACTION_REMOVER))

                }
                else -> {
                    sendBroadcast(Intent(ACTION_BUSCAR).also {
                        it.putExtra(EXTRA_BUSCAR,  buscarTarefas() as Array<out Parcelable>)
                    })
                }
            }
            onDestroy()
        }
    }
    private lateinit var workerThread: WorkerThread

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        database = Room.databaseBuilder(
            applicationContext,
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()

        workerThread = WorkerThread(intent!!)
        workerThread.start()

        return START_STICKY

    }

    //Funções de acesso ao datasource
    fun atualizaTarefa(tarefa: Tarefa): Tarefa{
        database.getTarefaDao().atualizarTarefa(tarefa)
        return tarefa
    }

    fun insereTarefa(tarefa: Tarefa): Tarefa{
        val id = database.getTarefaDao().inserirTarefa(tarefa)
        return Tarefa(id.toInt(), tarefa.nome, tarefa.realizada)
    }

    fun buscarTarefas(): Array<Tarefa> {
        val listaTarefas = database.getTarefaDao().recuperarTarefas()
        return listaTarefas.toTypedArray()
    }

    fun removerTarefa(tarefa: Tarefa){
        database.getTarefaDao().removerTarefa(tarefa)
    }

}