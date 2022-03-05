package br.edu.ifsp.scl.sdm.pa2.todolistarq.view

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.R
import br.edu.ifsp.scl.sdm.pa2.todolistarq.databinding.FragmentTarefaBinding
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.ACAO_TAREFA_EXTRA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.CONSULTA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.TAREFA_EXTRA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.TAREFA_REQUEST_KEY
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.nio.channels.AsynchronousByteChannel

class TarefaFragment : BaseFragment() {
    private lateinit var fragmentTarefaBinding: FragmentTarefaBinding
    private val ID_INEXISTENTE = -1L
    private var tarefaExtraId: Long = ID_INEXISTENTE
    private lateinit var database: ToDoListArqDatabase

    private var fab: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Escondendo botão de adicionar tarefa
        fab = activity?.findViewById(R.id.novaTarefaFab)
        fab?.visibility = GONE


        database = Room.databaseBuilder(
            requireContext(),
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentTarefaBinding = FragmentTarefaBinding.inflate(inflater, container, false)

        fragmentTarefaBinding.salvarTarefaBt.setOnClickListener {
            if (tarefaExtraId != ID_INEXISTENTE) {
                // Atualiza no banco
                atualizaTarefa(
                    Tarefa(
                        tarefaExtraId.toInt(),
                        fragmentTarefaBinding.nomeTarefaEt.text.toString(),
                        if (fragmentTarefaBinding.realizadaTarefaCb.isChecked) 1 else 0)
                )
            }
            else {
                // Insere tarefa no banco
                insereTarefa(
                    Tarefa(
                    nome = fragmentTarefaBinding.nomeTarefaEt.text.toString(),
                    realizada = if (fragmentTarefaBinding.realizadaTarefaCb.isChecked) 1 else 0)
                )
            }
        }

        // Verificando se trata-se de ação em uma tarefa existente
        val tarefaExtra = arguments?.getParcelable<Tarefa>(TAREFA_EXTRA)
        if (tarefaExtra != null) {
            tarefaExtraId = tarefaExtra.id.toLong() // Guarda id da tarefa editada para retornar
            with (fragmentTarefaBinding) {
                nomeTarefaEt.setText(tarefaExtra.nome)
                realizadaTarefaCb.isChecked = tarefaExtra.realizada != 0 // Zero é falso porque Sqlite não tem Boolean
            }
            val acaoTarefaExtra = arguments?.getInt(ACAO_TAREFA_EXTRA)
            if (acaoTarefaExtra == CONSULTA) {
                with (fragmentTarefaBinding) {
                    nomeTarefaEt.isEnabled = false
                    realizadaTarefaCb.isEnabled = false
                    salvarTarefaBt.visibility = GONE
                }
            }
        }

        return fragmentTarefaBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        // Mostrando botão de adicionar tarefa novamente
        fab?.visibility = View.VISIBLE
    }

    private fun retornaTarefa(tarefa: Tarefa) {
        setFragmentResult(TAREFA_REQUEST_KEY, Bundle().also {
            it.putParcelable(TAREFA_EXTRA, tarefa)
        })
        activity?.supportFragmentManager?.popBackStack()
    }

    private fun atualizaTarefa(tarefa: Tarefa){
        object  : AsyncTask<Tarefa, Unit, Unit>(){
            override fun doInBackground(vararg params: Tarefa?) {
                params[0]?.let { tarefaEditada ->
                    database.getTarefaDao().atualizarTarefa(tarefaEditada)
                }
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                retornaTarefa(tarefa)
            }
        }.execute(tarefa)
    }

    private fun insereTarefa(tarefa: Tarefa){
        object  : AsyncTask<Tarefa, Unit, Long>(){
            override fun doInBackground(vararg params: Tarefa?): Long {
                params[0]?.let { novaTarefa ->
                    return database.getTarefaDao().inserirTarefa(novaTarefa)
                }
                return ID_INEXISTENTE
            }

            override fun onPostExecute(result: Long?) {
                super.onPostExecute(result)
                result?.let { novoId ->
                    retornaTarefa(
                        Tarefa(
                            novoId.toInt(),
                            tarefa.nome,
                            tarefa.realizada
                        )
                    )
                }

            }
        }.execute(tarefa)
    }
}