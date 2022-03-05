package br.edu.ifsp.scl.sdm.pa2.todolistarq.view

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.R
import br.edu.ifsp.scl.sdm.pa2.todolistarq.controller.ListaTarefasController
import br.edu.ifsp.scl.sdm.pa2.todolistarq.databinding.FragmentListaTarefasBinding
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.ACAO_TAREFA_EXTRA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.CONSULTA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.TAREFA_EXTRA
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.BaseFragment.Constantes.TAREFA_REQUEST_KEY
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.adapter.OnTarefaClickListener
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.adapter.TarefasAdapter

class ListaTarefasFragment: BaseFragment(), OnTarefaClickListener {
    private lateinit var fragmentListaTarefasBinding: FragmentListaTarefasBinding
    private lateinit var tarefasList: MutableList<Tarefa>
    private lateinit var tarefasAdapter: TarefasAdapter
    private lateinit var listaTarefasController: ListaTarefasController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listaTarefasController = ListaTarefasController(this)
        // Buscar tarefas no banco de dados
        tarefasList = mutableListOf()

        listaTarefasController.buscarTarefas()

        setFragmentResultListener(TAREFA_REQUEST_KEY) { chave, resultados ->
            val tarefaExtra = resultados.getParcelable<Tarefa>(TAREFA_EXTRA)
            // Adiciona ou atualiza uma tarefa da lista
            if (tarefaExtra != null) {
                var novaTarefa = true
                tarefasList.forEachIndexed { posicao, tarefa ->
                    if (tarefa.id == tarefaExtra.id) {
                        tarefasList.set(posicao, tarefaExtra)
                        novaTarefa = false
                    }
                }
                if (novaTarefa) {
                    tarefasList.add(tarefaExtra)
                }
                tarefasAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentListaTarefasBinding = FragmentListaTarefasBinding.inflate(inflater, container, false)

        tarefasAdapter = TarefasAdapter(this, tarefasList)
        val tarefasLayoutManager = LinearLayoutManager(activity)
        fragmentListaTarefasBinding.tarefasRv.adapter = tarefasAdapter
        fragmentListaTarefasBinding.tarefasRv.layoutManager = tarefasLayoutManager

        return fragmentListaTarefasBinding.root
    }

    override fun onTarefaClick(posicao: Int) {
        // Abre TarefaFragment para consulta de tarefa
        val tarefa = tarefasList[posicao]
        abreTarefaFragment(tarefa, true)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val posicao = tarefasAdapter.posicao
        return when (item.itemId) {
            R.id.editarTarefaMi -> {
                // Abre TarefaFragment para edição de tarefa
                abreTarefaFragment(tarefasList[posicao], false)
                true
            }
            R.id.removerTarefaMi -> {
                listaTarefasController.removerTarefa(tarefasList[posicao])
//                // Remove da lista de tarefas
//                tarefasList.removeAt(posicao)
//                tarefasAdapter.notifyDataSetChanged()
                true
            }
            else -> false
        }
    }

    private fun abreTarefaFragment(tarefa: Tarefa, consulta: Boolean) {
        // Preparando tarefa para enviar para o TarefaFragment
        val argumentos = Bundle().also { bundle ->
            bundle.putParcelable(TAREFA_EXTRA, tarefa)
            if (consulta) {
                bundle.putInt(ACAO_TAREFA_EXTRA, CONSULTA)
            }
        }
        val tarefaFragment = TarefaFragment()
        tarefaFragment.arguments = argumentos

        activity?.supportFragmentManager?.commit {
            setReorderingAllowed(true)
            addToBackStack("TarefaFragment")
            replace(R.id.principalFcv, tarefaFragment)
        }
    }
    fun atualizarListaTarefas(listaTarefas: MutableList<Tarefa>){
        tarefasList.clear()
        tarefasList.addAll(listaTarefas)
        tarefasAdapter.notifyDataSetChanged()

        //todo perguntar se alguma outra metodologia de desenvolvimento seria mais eficaz, sem ter esse acoplamento SOLID? CLean code?
    }

}