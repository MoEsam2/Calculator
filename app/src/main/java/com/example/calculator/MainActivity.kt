package com.example.calculator

import android.os.Bundle
import android.util.LruCache
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var isRecyclerViewExpanded = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var calculatorViewModel: CalculatorViewModel
    private val cache = LruCache<String, String>(10 * 1024 * 1024)
    private lateinit var adapter: ResultAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        calculatorViewModel = ViewModelProvider(this).get(CalculatorViewModel::class.java)

        setRecyclerview()
        addCallBack()

        binding.yourName.apply {
            isFocusableInTouchMode = true
            isFocusable = true
        }

        calculatorViewModel.inputExpression.observe(this, Observer {
            it?.let {
                binding.textCalculation.text = it
            }
        })

        calculatorViewModel.displayText.observe(this, Observer {
            it?.let {
                updateDisplay(it)
            }
        })

    }

    private fun setRecyclerview() {
        adapter = ResultAdapter()
        binding.recyclerView.adapter = adapter

    }

    private fun addCallBack() {
        binding.apply {
            buttonClear.setOnClickListener {
                calculatorViewModel.clearInput()
            }
            buttonAdd.setOnClickListener {
                calculatorViewModel.appendInput("+")

            }
            buttonSub.setOnClickListener {
                calculatorViewModel.appendInput("-")

            }
            buttonMul.setOnClickListener {
                calculatorViewModel.appendInput("*")

            }
            buttonDiv.setOnClickListener {
                calculatorViewModel.appendInput("/")

            }
            buttonPoint.setOnClickListener {
                calculatorViewModel.appendInput(".")
            }
            buttonEqual.setOnClickListener {
                calculatorViewModel.evaluateExpression()
                val result = "${binding.textCalculation.text} = ${binding.textResult.text}"
                cache.put("Result", result)
                adapter.setData(result)

            }
            headerView.setOnClickListener {
                changeRecyclerViewHeight()
            }
        }
    }


    fun onClickNumber(v: View) {
        val digit = (v as Button).text.toString()
        calculatorViewModel.appendInput(digit)
    }


    private fun updateDisplay(expression: String) {
        binding.textResult.text = expression

    }

    private fun changeRecyclerViewHeight() {
        val newHeight =
            if (isRecyclerViewExpanded) 0 else resources.getDimensionPixelSize(R.dimen.recycler_view_height)
        val layoutParams = binding.recyclerView.layoutParams
        layoutParams.height = newHeight
        binding.recyclerView.layoutParams = layoutParams
        isRecyclerViewExpanded = !isRecyclerViewExpanded
    }


}