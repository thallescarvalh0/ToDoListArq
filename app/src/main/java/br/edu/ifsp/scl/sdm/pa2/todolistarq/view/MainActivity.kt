package br.edu.ifsp.scl.sdm.pa2.todolistarq.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import br.edu.ifsp.scl.sdm.pa2.todolistarq.R
import br.edu.ifsp.scl.sdm.pa2.todolistarq.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.pa2.todolistarq.service.BuscarTarefaService

class MainActivity : AppCompatActivity() {

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val lifeTimeServiceIntent: Intent by lazy{
        //Intent(this, LifeTimeStartedService::class.java)
        Intent(this, BuscarTarefaService::class.java)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.principalFcv, ListaTarefasFragment(), "ListaTarefasFragment")
        }

        activityMainBinding.novaTarefaFab.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack("Tarefa")
                replace(R.id.principalFcv, TarefaFragment(), "TarefaFragment")
            }
        }
    }
}