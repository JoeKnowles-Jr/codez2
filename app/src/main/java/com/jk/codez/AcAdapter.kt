package com.jk.codez

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.jk.codez.item.Item


class AcAdapter(private val mContext: Context,
                private val viewResourceId: Int,
                private val items: ArrayList<Item>) : ArrayAdapter<Item?>(mContext, viewResourceId, items.toList()) {

    private val itemsAll = items.clone() as ArrayList<Item>
    private var suggestions = ArrayList<Item>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v: View? = convertView
        if (v == null) {
            val vi = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(viewResourceId, null)
        }
        println("$position position")
        val item: Item = suggestions[position]
        val numaddress = v?.findViewById(R.id.tv_numaddress) as TextView?
        numaddress?.text = String.format("%d %s", item.number, item.street)
        val codes = v?.findViewById(R.id.tv_codes) as TextView?
        codes?.text = item.codesString
        return v!!
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    private var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return String.format("%d %s", (resultValue as Item).number, resultValue.street)
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                for (item in itemsAll) {
                    val numString = String.format("%d", item.number)
                    println("$numString numString")
                    if (
                        numString.lowercase().startsWith(constraint.toString().lowercase()) ||
                                item.street.lowercase().startsWith(constraint.toString().lowercase())
                    ) {
                        suggestions.add(item)
                        println(suggestions.toString())
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                println(filterResults.values.toString())
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val filteredList =  results?.values as ArrayList<Item>?
            println("$filteredList numString")

            if (results != null && results.count > 0) {
                clear()
                for (c: Item in filteredList ?: listOf()) {
                    println("$c c")
                    add(c)
                }
                notifyDataSetChanged()
            }
        }
    }
}