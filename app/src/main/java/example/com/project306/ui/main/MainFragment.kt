package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import example.com.project306.databinding.MainFragmentBinding
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = MainFragmentBinding.inflate(inflater, container, false)
        subscribeUi(binding)
        return binding.root
    }

    private fun subscribeUi(binding: MainFragmentBinding?) {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.getDisplayName().observe(viewLifecycleOwner, Observer { displayName ->
            if (displayName != null) {
                binding?.displayName = displayName
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_button.setOnClickListener {
            with(viewModel) {
                setDisplayName("Mark Fonte")
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
