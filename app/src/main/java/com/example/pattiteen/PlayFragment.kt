package com.example.pattiteen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.pattiteen.databinding.FragmentPlayBinding
import com.example.pattiteen.logic.GameViewModel
import com.example.pattiteen.logic.GameViewModelFactory

class PlayFragment : Fragment() {

    private val binding by lazy { FragmentPlayBinding.inflate(layoutInflater) }
    private val viewModel: GameViewModel by activityViewModels {
        GameViewModelFactory((activity as MainActivity).getGameConnectManager())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.peersCount.observe(viewLifecycleOwner, {
            binding.countText.text = it.toString()
        })

        viewModel.cardsSeen.observe(viewLifecycleOwner, {
            val imageRes = if(it) R.drawable.seen else R.drawable.blind
            binding.chaalSiren.setImageResource(imageRes)
            val chaalString = if(it) R.string.chaal else R.string.blind
            binding.contiuneBtn.text = context?.getString(chaalString)
        })

        viewModel.chaalAmount.observe(viewLifecycleOwner, {
            binding.chaalAmount.text = context?.getString(R.string.chaal_string, it.toString())
        })
        viewModel.potAmount.observe(viewLifecycleOwner, {
            binding.potAmount.text = context?.getString(R.string.pot_string, it.toString())
        })

        binding.chaalSiren.setOnClickListener { viewModel.onCardsSeen() }
        binding.countText.setOnClickListener { viewModel.onPeerCountClick() }
        binding.doubleBtn.setOnClickListener { viewModel.onDoubleClick() }
        binding.contiuneBtn.setOnClickListener { viewModel.onChaalClick() }
        binding.packBtn.setOnClickListener { viewModel.onPackClick() }
    }
}