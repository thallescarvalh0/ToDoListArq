package br.edu.ifsp.scl.sdm.pa2.todolistarq.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.R
import br.edu.ifsp.scl.sdm.pa2.todolistarq.databinding.FragmentTarefaBinding
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.ACAO_TAREFA_EXTRA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.CONSULTA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.TAREFA_EXTRA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.TAREFA_REQUEST_KEY
import br.edu.ifsp.scl.sdm.pa2.todolistarq.viewmodel.TarefaViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TarefaFragment : BaseFragment() {
    private lateinit var fragmentTarefaBinding: FragmentTarefaBinding
    val ID_INEXISTENTE = -1L
    private var tarefaExtraId: Long = ID_INEXISTENTE
    private lateinit var database: ToDoListArqDatabase
    private lateinit var tarefaFragmentController: TarefaViewModel

    private var fab: FloatingActionButton? = null

    companion object {
        val ACTION_ATUALIZAR = "ACTION_ATUALIZAR"
        val ACTION_INSERIR = "ACTION_INSERIR"

        val EXTRA_ATUALIZAR = "ATUALIZAR"
        val EXTRA_INSERIR = "INSERIR"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //tarefaFragmentController = TarefaFragmentController(this)
        tarefaFragmentController = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(TarefaViewModel::class.java)

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
                tarefaFragmentController.atualizaTarefa(
                    Tarefa(
                        tarefaExtraId.toInt(),
                        fragmentTarefaBinding.nomeTarefaEt.text.toString(),
                        if (fragmentTarefaBinding.realizadaTarefaCb.isChecked) 1 else 0)
                )
            }
            else {
                // Insere tarefa no banco
                tarefaFragmentController.insereTarefa(
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

    override fun retornaTarefa(tarefa: Tarefa) {
        setFragmentResult(TAREFA_REQUEST_KEY, Bundle().also {
            it.putParcelable(TAREFA_EXTRA, tarefa)
        })
        activity?.supportFragmentManager?.popBackStack()
    }

    private val receiveInserirTarefasBr: BroadcastReceiver by lazy {
        object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val bundle = intent?.extras
                val tarefa = bundle?.getParcelable<Tarefa>(EXTRA_INSERIR)
                retornaTarefa(tarefa!!)
            }
        }
    }

    private val receiveAtualizarTarefasBr: BroadcastReceiver by lazy {
        object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val bundle = intent?.extras
                val tarefa = bundle?.getParcelable<Tarefa>(EXTRA_ATUALIZAR)
                retornaTarefa(tarefa!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().registerReceiver(receiveInserirTarefasBr, IntentFilter(
            ACTION_INSERIR))

        requireActivity().registerReceiver(receiveAtualizarTarefasBr, IntentFilter(
            ACTION_ATUALIZAR))
    }
}