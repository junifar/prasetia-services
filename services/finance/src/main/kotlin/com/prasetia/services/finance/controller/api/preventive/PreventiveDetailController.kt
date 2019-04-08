package com.prasetia.services.finance.controller.api.preventive

import com.prasetia.services.finance.model.preventive.PreventiveSaleOrder
import com.prasetia.services.finance.model.preventive.PreventiveSaleOrderInvoice
import com.prasetia.services.finance.pojo.preventive.*
import com.prasetia.services.finance.repository.preventive.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PreventiveDetailController{

    @Autowired
    lateinit var repository: PreventiveCustomerDetailRepository

    @Autowired
    lateinit var repositoryPreventiveSaleOrder: PreventiveSaleOrderRepository

    @Autowired
    lateinit var repositoryPreventiveInvoice: PreventiveInvoiceRepository

    @Autowired
    lateinit var repositoryBudget: PreventiveBudgetRepository

    @Autowired
    lateinit var repositoryRealisasiBudget: PreventiveBudgetRealisasiRepository

    @Autowired
    lateinit var repositorySaleOrderInvoice: PreventiveSaleOrderInvoiceRepository

    fun getPreventivePOInvoiceSaleOrder(data:Iterable<PreventiveSaleOrderInvoice>, clientOrderRef: String, month:Int): Long{
        var value:Long = 0
        data.forEach {
            if ((it.client_order_ref == clientOrderRef) and (it.bulan_po == month.toLong())) value += it.nilai_invoice
        }
        return value
    }

    fun getPreventivePOInvoiceSaleOrderVal(data:Iterable<PreventiveSaleOrderInvoice>, clientOrderRef: String, month:Int): String{
        var value:String = ""
        data.forEach {
            if ((it.client_order_ref == clientOrderRef) and (it.bulan_po == month.toLong())) value += " ${it.name} "
        }
        return value
    }

    fun getPreventivePOInvoiceSaleOrderState(data:Iterable<PreventiveSaleOrderInvoice>, clientOrderRef: String, month:Int): String{
        var value:String = ""
        data.forEach {
            if ((it.client_order_ref == clientOrderRef) and (it.bulan_po == month.toLong())) value += " ${it.state} "
        }
        return value
    }



    fun getPreventiveSaleOrderInvoice(client_order_ref: String, data:Iterable<PreventiveSaleOrderInvoice>, bulan: Long): Long{
        var value:Long = 0
        data.forEach {
            item->
            if ((item.client_order_ref == client_order_ref) and (item.bulan_po == bulan)){
                value += item.nilai_invoice
            }
        }
        return value
    }

    fun getPreventiveBudgetRealisasi(name: String, data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget>, bulan: Long): Long{
        var value:Long = 0
        data.forEach {
            item->
            if ((item.name == name) and (item.bulan == bulan)){
                value += item.realisasi_budget?:0
            }
        }
        return value
    }

    fun getPreventivePOInvoice(clientOrderRef: String, data:Iterable<PreventiveSaleOrderInvoice>):MutableList<com.prasetia.services.finance.pojo.preventive.PreventiveSaleOrderInvoice>{
        val preventiveSaleOrderInvoice:MutableList<com.prasetia.services.finance.pojo.preventive.PreventiveSaleOrderInvoice> = mutableListOf()
        data.forEach {
            item->
            if(item.client_order_ref == clientOrderRef){
                var found = false

                preventiveSaleOrderInvoice.forEach {
                    if(it.client_order_ref == clientOrderRef) found = true
                }

                if(!found){
                    val i = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 1)
                    val ii = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 2)
                    val iii = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 3)
                    val iv = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 4)
                    val v = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 5)
                    val vi = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 6)
                    val vii = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 7)
                    val viii = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 8)
                    val ix = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 9)
                    val x = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 10)
                    val xi = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 11)
                    val xii = getPreventivePOInvoiceSaleOrder(data, clientOrderRef, 12)
                    val total = i + ii + iii + iv + v + vi + vii + viii + ix + x + xi + xii
                    val i_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 1)
                    val ii_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 2)
                    val iii_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 3)
                    val iv_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 4)
                    val v_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 5)
                    val vi_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 6)
                    val vii_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 7)
                    val viii_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 8)
                    val ix_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 9)
                    val x_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 10)
                    val xi_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 11)
                    val xii_val = getPreventivePOInvoiceSaleOrderVal(data, clientOrderRef, 12)

                    val i_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 1)
                    val ii_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 2)
                    val iii_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 3)
                    val iv_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 4)
                    val v_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 5)
                    val vi_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 6)
                    val vii_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 7)
                    val viii_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 8)
                    val ix_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 9)
                    val x_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 10)
                    val xi_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 11)
                    val xii_state = getPreventivePOInvoiceSaleOrderState(data, clientOrderRef, 12)

                    preventiveSaleOrderInvoice.add(com.prasetia.services.finance.pojo.preventive.PreventiveSaleOrderInvoice(item.id, item.client_order_ref, item.state, i, ii, iii,
                            iv, v, vi, vii, viii, ix, x, xi, xii, i_val, ii_val, iii_val, iv_val,
                            v_val, vi_val, vii_val, viii_val, ix_val, x_val, xi_val, xii_val, i_state, ii_state, iii_state, iv_state,
                            v_state, vi_state, vii_state, viii_state, ix_state, x_state, xi_state, xii_state, total))
                }
            }
        }
        return preventiveSaleOrderInvoice
    }

    @RequestMapping("/api/preventive_by_customer_year_area/{customer_id}/{tahun}/{area_id}")
    fun getDataByCustomerYearArea(@PathVariable("customer_id") customer_id: Int,
                                  @PathVariable("tahun") tahun: String, @PathVariable("area_id") area_id:String): MutableList<PreventiveCustomerDetailHeader> {

        val data = if (area_id != "null") repository.getDataByCustomerYearArea(customer_id, tahun, area_id.toInt()) else repository.getDataByCustomerYearArea(customer_id, tahun)
        val dataPreventiveSaleOrder = if (area_id != "null") repositoryPreventiveSaleOrder.getPreventiveSaleOrder(customer_id, tahun, area_id.toInt()) else repositoryPreventiveSaleOrder.getPreventiveSaleOrderNullArea(customer_id, tahun)
        val dataPreventiveInvoice = if (area_id != "null") repositoryPreventiveInvoice.getPreventiveInvoice(customer_id, tahun, area_id.toInt()) else repositoryPreventiveInvoice.getPreventiveInvoiceNullArea(customer_id, tahun)
        val dataPreventiveBudget = if (area_id != "null") repositoryBudget.getPreventiveBudget(customer_id, tahun, area_id.toInt()) else repositoryBudget.getPreventiveBudgetNullArea(customer_id, tahun)
        val dataPreventiveRealisasiBudget = if (area_id != "null") repositoryRealisasiBudget.getPreventiveRealisasiBudget(customer_id, tahun, area_id.toInt()) else repositoryRealisasiBudget.getPreventiveRealisasiBudgetNullArea(customer_id, tahun)
        val dataPreventiveSaleOrderInvoice = if(area_id != "null") repositorySaleOrderInvoice.getPreventiveSaleOrderInvoice(customer_id, tahun, area_id.toInt()) else repositorySaleOrderInvoice.getPreventiveSaleOrderInvoiceNullArea(customer_id, tahun)

        var headerGroup: MutableList<PreventiveCustomerDetailHeader> = mutableListOf()
        var id:Long=1
        data.forEach{
            item->
            headerGroup.add(PreventiveCustomerDetailHeader(id++, item.customer_id,
                    item.customer_name, item.area, item.area_id, item.tahun.toString(),
                    getPreventiveSaleOrder(customer_id, tahun, area_id, dataPreventiveSaleOrder, dataPreventiveSaleOrderInvoice),
                    getPreventiveInvoice(customer_id, tahun, area_id, dataPreventiveInvoice),
                    getPreventiveBudgetArea(customer_id, tahun, area_id, dataPreventiveBudget, dataPreventiveRealisasiBudget),
                    getPreventiveRealisasiBudgetArea(customer_id, tahun, area_id, dataPreventiveRealisasiBudget)))
        }
        return headerGroup
    }

    fun getPreventiveSaleOrder(customer_id: Int, tahun: String, area_id: String, data:Iterable<PreventiveSaleOrder>, dataSaleOrder:Iterable<PreventiveSaleOrderInvoice>): MutableList<com.prasetia.services.finance.pojo.preventive.PreventiveSaleOrder>{
        val preventiveSaleOrder:MutableList<com.prasetia.services.finance.pojo.preventive.PreventiveSaleOrder> = mutableListOf()
        data.forEach {
            item->
            if((item.tahun.toString() == tahun) and (item.customer_id == customer_id.toLong()) and (item.area_id.toString() == area_id)){
                var found = false
                preventiveSaleOrder.forEach {
                    itemDetail ->
                    if (itemDetail.client_order_ref == item.client_order_ref) found = true
                }
                if (!found)
                {
                    val i = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 1, data)
                    val ii = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 2, data)
                    val iii = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 3, data)
                    val iv = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 4, data)
                    val v = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 5, data)
                    val vi = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 6, data)
                    val vii = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 7, data)
                    val viii = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 8, data)
                    val ix = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 9, data)
                    val x = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 10, data)
                    val xi = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 11, data)
                    val xii = getPreventiveSaleOrderNilaiPO(customer_id, tahun, area_id, item.client_order_ref, 12, data)
                    val i_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 1)
                    val ii_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 2)
                    val iii_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 3)
                    val iv_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 4)
                    val v_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 5)
                    val vi_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 6)
                    val vii_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 7)
                    val viii_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 8)
                    val ix_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 9)
                    val x_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 10)
                    val xi_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 11)
                    val xii_inv = getPreventiveSaleOrderInvoice(item.client_order_ref, dataSaleOrder , 12)
                    val i_inv_precentage = if (i == 0.toLong()) 0f else i_inv.toFloat().div(i.toFloat())
                    val ii_inv_precentage = if (ii == 0.toLong()) 0f else ii_inv.toFloat().div(ii.toFloat())
                    val iii_inv_precentage = if (iii == 0.toLong()) 0f else iii_inv.toFloat().div(iii.toFloat())
                    val iv_inv_precentage = if (iv == 0.toLong()) 0f else iv_inv.toFloat().div(iv.toFloat())
                    val v_inv_precentage = if (v == 0.toLong()) 0f else v_inv.toFloat().div(v.toFloat())
                    val vi_inv_precentage = if (vi == 0.toLong()) 0f else vi_inv.toFloat().div(vi.toFloat())
                    val vii_inv_precentage = if (vii == 0.toLong()) 0f else vii_inv.toFloat().div(vii.toFloat())
                    val viii_inv_precentage = if (viii == 0.toLong()) 0f else viii_inv.toFloat().div(viii.toFloat())
                    val ix_inv_precentage = if (ix == 0.toLong()) 0f else ix_inv.toFloat().div(ix.toFloat())
                    val x_inv_precentage = if (x == 0.toLong()) 0f else x_inv.toFloat().div(x.toFloat())
                    val xi_inv_precentage = if (xi == 0.toLong()) 0f else xi_inv.toFloat().div(xi.toFloat())
                    val xii_inv_precentage = if (xii == 0.toLong()) 0f else xii_inv.toFloat().div(xii.toFloat())
                    val total = i + ii + iii + iv + v + vi + vii + vii + ix + x + xi + xii

                    preventiveSaleOrder.add(com.prasetia.services.finance.pojo.preventive.PreventiveSaleOrder(
                            item.id,
                            item.client_order_ref,
                            i, ii, iii,
                            iv, v, vi,
                            vii, viii, ix,
                            x, xi, xii,
                            total, i_inv, ii_inv, iii_inv,
                            iv_inv, v_inv, vi_inv,
                            vii_inv, viii_inv, ix_inv, x_inv, xi_inv, xii_inv,
                            i_inv_precentage, ii_inv_precentage, iii_inv_precentage, iv_inv_precentage,
                            v_inv_precentage, vi_inv_precentage, vii_inv_precentage, viii_inv_precentage,
                            ix_inv_precentage, x_inv_precentage, xi_inv_precentage, xii_inv_precentage,
                            getPreventivePOInvoice(item.client_order_ref, dataSaleOrder)))
                }

            }
        }
        return preventiveSaleOrder
    }

    fun getPreventiveSaleOrderNilaiPO(customer_id: Int, tahun: String, area_id: String, clientOrderRef: String, bulan: Long, data:Iterable<PreventiveSaleOrder>): Long{
        var nilai_po:Long = 0
        data.forEach {
            item->
            if((item.tahun.toString() == tahun) and
                    (item.customer_id == customer_id.toLong()) and
                    (item.area_id.toString() == area_id) and
                    (item.client_order_ref == clientOrderRef) and
                    (item.bulan == bulan)){
                if(item.nilai_po != null){
                    nilai_po += item.nilai_po
                }
            }
        }
        return nilai_po
    }

    fun getPreventiveSaleOrderNilaiInvoice(customer_id: Int, tahun: String, area_id: String, clientOrderRef: String, bulan: Long, data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveInvoice>): Long{
        var nilai_po:Long = 0
        data.forEach {
            item->
            if((item.tahun.toString() == tahun) and
                    (item.customer_id == customer_id.toLong()) and
                    (item.area_id.toString() == area_id) and
                    (item.client_order_ref == clientOrderRef) and
                    (item.bulan == bulan)){
                if(item.nilai_invoice != null){
                    nilai_po += item.nilai_invoice
                }
            }
        }
        return nilai_po
    }

    fun getPreventiveInvoice(customer_id: Int, tahun: String, area_id: String, data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveInvoice>): MutableList<PreventiveInvoice>{
        val preventiveInvoice:MutableList<PreventiveInvoice> = mutableListOf()
        data.forEach {
            item->
            if((item.tahun.toString() == tahun) and (item.customer_id == customer_id.toLong()) and (item.area_id.toString() == area_id)){
                var found = false
                preventiveInvoice.forEach {
                    itemDetail ->
                    if (itemDetail.client_order_ref == item.client_order_ref) found = true
                }
                if (!found){
                    val i = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 1, data)
                    val ii = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 2, data)
                    val iii = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 3, data)
                    val iv = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 4, data)
                    val v = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 5, data)
                    val vi = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 6, data)
                    val vii = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 7, data)
                    val viii = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 8, data)
                    val ix = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 9, data)
                    val x = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 10, data)
                    val xi = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 11, data)
                    val xii = getPreventiveSaleOrderNilaiInvoice(customer_id, tahun, area_id, item.client_order_ref, 12, data)
                    val total = i + ii + iii + iv + v + vi + vii + viii + ix + x + xi + xii
                    preventiveInvoice.add(PreventiveInvoice(item.id, item.client_order_ref,
                            i, ii, iii,
                            iv, v, vi,
                            vii, viii, ix,
                            x, xi, xii, total))
                }
            }
        }
        return preventiveInvoice
    }

    fun getPreventiveNilaiBudget(customer_id: Int, tahun: String, area_id: String, name: String, bulan: Long, data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveBudget>): Long{
        var nilai_budget:Long = 0
        data.forEach {
            item->
            if((item.tahun.toString() == tahun) and
                    (item.customer_id == customer_id.toLong()) and
                    (item.area_id.toString() == area_id) and
                    (item.name == name) and
                    (item.bulan == bulan)){
                if(item.nilai_budget != null){
                    nilai_budget += item.nilai_budget
                }
            }
        }
        return nilai_budget
    }

    fun getPreventiveBudget(customer_id: Int, tahun: String, area_id: String, sub_area: String?,
                            data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveBudget>,
                            dataRealisasiBudget: Iterable<com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget>):MutableList<PreventiveBudget>{
        val preventiveBudget:MutableList<PreventiveBudget> = mutableListOf()
        val filterValue = when(area_id){
            "null" -> data.filter { it.customer_id == customer_id.toLong() }
                    .filter { it.tahun == tahun.toLong() }
                    .filter { it.area_detail == sub_area }
            else -> data.filter { it.customer_id == customer_id.toLong() }
                    .filter { it.tahun == tahun.toLong() }
                    .filter { it.area_id == area_id.toLong() }
                    .filter { it.area_detail == sub_area }
        }

        val filterValueRealisasiBudget = when(area_id){
            "null" -> dataRealisasiBudget.filter { it.customer_id == customer_id.toLong() }
                    .filter { it.tahun == tahun.toLong() }
                    .filter { it.area_detail == sub_area }
            else -> dataRealisasiBudget.filter { it.customer_id == customer_id.toLong() }
                    .filter { it.tahun == tahun.toLong() }
                    .filter { it.area_id == area_id.toLong() }
                    .filter { it.area_detail == sub_area }
        }


        filterValue.forEach {
            item->
            if(preventiveBudget.filter { it.name == item.name }.count() == 0){
                val i = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 1, filterValue)
                val ii = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 2, filterValue)
                val iii = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 3, filterValue)
                val iv = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 4, filterValue)
                val v = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 5, filterValue)
                val vi = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 6, filterValue)
                val vii = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 7, filterValue)
                val viii = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 8, filterValue)
                val ix = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 9, filterValue)
                val x = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 10, filterValue)
                val xi = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 11, filterValue)
                val xii = getPreventiveNilaiBudget(customer_id, tahun, area_id, item.name, 12, filterValue)
                val total = i + ii + iii + iv + v + vi + vii + viii + ix + x + xi + xii
                val i_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,1)
                val ii_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,2)
                val iii_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,3)
                val iv_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,4)
                val v_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,5)
                val vi_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,6)
                val vii_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,7)
                val viii_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,8)
                val ix_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,9)
                val x_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,10)
                val xi_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,11)
                val xii_realisasi = getPreventiveBudgetRealisasi(item.name, filterValueRealisasiBudget,12)
                val i_realisasi_percentage = if (i == 0.toLong()) 0f else i_realisasi.toFloat().div(i.toFloat())
                val ii_realisasi_percentage = if (ii == 0.toLong()) 0f else ii_realisasi.toFloat().div(ii.toFloat())
                val iii_realisasi_percentage = if (iii == 0.toLong()) 0f else iii_realisasi.toFloat().div(iii.toFloat())
                val iv_realisasi_percentage = if (iv == 0.toLong()) 0f else iv_realisasi.toFloat().div(iv.toFloat())
                val v_realisasi_percentage = if (v == 0.toLong()) 0f else v_realisasi.toFloat().div(v.toFloat())
                val vi_realisasi_percentage = if (vi == 0.toLong()) 0f else vi_realisasi.toFloat().div(vi.toFloat())
                val vii_realisasi_percentage = if (vii == 0.toLong()) 0f else vii_realisasi.toFloat().div(vii.toFloat())
                val viii_realisasi_percentage = if (viii == 0.toLong()) 0f else viii_realisasi.toFloat().div(viii.toFloat())
                val ix_realisasi_percentage = if (ix == 0.toLong()) 0f else ix_realisasi.toFloat().div(ix.toFloat())
                val x_realisasi_percentage = if (x == 0.toLong()) 0f else x_realisasi.toFloat().div(x.toFloat())
                val xi_realisasi_percentage = if (xi == 0.toLong()) 0f else xi_realisasi.toFloat().div(xi.toFloat())
                val xii_realisasi_percentage = if (xii == 0.toLong()) 0f else xii_realisasi.toFloat().div(xii.toFloat())

                preventiveBudget.add(PreventiveBudget(item.id, item.name,
                        i, ii, iii, iv,
                        v, vi, vii, viii,
                        ix, x, xi, xii, total,
                        i_realisasi, ii_realisasi, iii_realisasi,
                        iv_realisasi, v_realisasi, vi_realisasi,
                        vii_realisasi, viii_realisasi, ix_realisasi,
                        x_realisasi, xi_realisasi, xii_realisasi,
                        i_realisasi_percentage, ii_realisasi_percentage, iii_realisasi_percentage,
                        iv_realisasi_percentage, v_realisasi_percentage, vi_realisasi_percentage,
                        vii_realisasi_percentage, viii_realisasi_percentage, ix_realisasi_percentage,
                        x_realisasi_percentage,xi_realisasi_percentage, xii_realisasi_percentage,
                        getPreventiveBudgetWithRealisasiBudget(customer_id, tahun, area_id, sub_area,
                                item.id, filterValueRealisasiBudget)))
            }

        }
        return preventiveBudget
    }

    fun getPreventiveBudgetWithRealisasiBudget(customer_id: Int, tahun: String, area_id: String, sub_area: String?,
                                               budget_realisasi_id: Long,
                                               data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget>): MutableList<PreventiveRealisasiBudget> {
        val preventiveRealisasiBudget:MutableList<PreventiveRealisasiBudget> = mutableListOf()
        data.forEach {
            item->
            if((item.tahun.toString() == tahun) and
                    (item.customer_id == customer_id.toLong()) and
                    (item.area_id.toString() == area_id) and (item.area_detail == sub_area) and
                    (item.id == budget_realisasi_id)){
                var found = false
                preventiveRealisasiBudget.forEach {
                    itemDetail ->
                    if(itemDetail.name == item.name) found = true
                }
                if(!found){
                    val i = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 1, data)
                    val ii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 2, data)
                    val iii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 3, data)
                    val iv = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 4, data)
                    val v = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 5, data)
                    val vi = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 6, data)
                    val vii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 7, data)
                    val viii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 8, data)
                    val ix = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 9, data)
                    val x = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 10, data)
                    val xi = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 11, data)
                    val xii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 12, data)
                    val total = i + ii + iii + iv + v + vi + vii + viii + ix + x + xi + xii

                    preventiveRealisasiBudget.add(PreventiveRealisasiBudget(item.id, item.name,
                            i, ii, iii, iv,
                            v, vi, vii, viii,
                            ix, x, xi, xii, total))
                }
            }
        }
        return preventiveRealisasiBudget
    }

    fun getPreventiveBudgetArea(customer_id: Int, tahun: String, area_id: String,
                                data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveBudget>,
                                dataRealisasiBudget: Iterable<com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget>):MutableList<PreventiveBudgetArea>{
        val preventiveBudgetArea:MutableList<PreventiveBudgetArea> = mutableListOf()
        var id:Long= 0
        data.distinctBy { it.area_detail }.forEach {
            item->
            preventiveBudgetArea.add(PreventiveBudgetArea(id++, item.area_detail?: "-", getPreventiveBudget(customer_id, tahun, area_id, item.area_detail , data, dataRealisasiBudget)))
        }

        return preventiveBudgetArea
    }

    fun getPreventiveNilaiRealisasiBudget(customer_id: Int, tahun: String, area_id: String, name: String, bulan: Long, data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget>): Long{
        var nilai_budget:Long = 0
        data.forEach {
            item->
            if((item.tahun.toString() == tahun) and
                    (item.customer_id == customer_id.toLong()) and
                    (item.area_id.toString() == area_id) and
                    (item.name == name) and
                    (item.bulan == bulan)){
                if(item.realisasi_budget != null){
                    nilai_budget += item.realisasi_budget
                }
            }
        }
        return nilai_budget
    }

    fun getPreventiveRealisasiBudget(customer_id: Int, tahun: String, area_id: String, sub_area: String?, data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget>):MutableList<PreventiveRealisasiBudget>{
        val preventiveRealisasiBudget:MutableList<PreventiveRealisasiBudget> = mutableListOf()
        val filterValue = when(area_id){
            "null" -> data.filter { it.customer_id == customer_id.toLong() }
                    .filter { it.tahun == tahun.toLong() }
                    .filter { it.area_detail == sub_area }
            else -> data.filter { it.customer_id == customer_id.toLong() }
                    .filter { it.tahun == tahun.toLong() }
                    .filter { it.area_id == area_id.toLong() }
                    .filter { it.area_detail == sub_area }
        }

        filterValue.forEach{
            item->
            if(preventiveRealisasiBudget.filter { it.name == item.name }.count() == 0){
                val i = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 1, filterValue)
                val ii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 2, filterValue)
                val iii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 3, filterValue)
                val iv = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 4, filterValue)
                val v = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 5, filterValue)
                val vi = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 6, filterValue)
                val vii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 7, filterValue)
                val viii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 8, filterValue)
                val ix = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 9, filterValue)
                val x = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 10, filterValue)
                val xi = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 11, filterValue)
                val xii = getPreventiveNilaiRealisasiBudget(customer_id, tahun, area_id, item.name, 12, filterValue)
                val total = i + ii + iii + iv + v + vi + vii + viii + ix + x + xi + xii

                preventiveRealisasiBudget.add(PreventiveRealisasiBudget(item.id, item.name,
                        i, ii, iii, iv,
                        v, vi, vii, viii,
                        ix, x, xi, xii, total))
            }
        }
        return preventiveRealisasiBudget
    }

    fun getPreventiveRealisasiBudgetArea(customer_id: Int, tahun: String, area_id: String, data:Iterable<com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget>): MutableList<PreventiveRealisasiBudgetArea>{
        val preventiveRealisasiBudgetArea:MutableList<PreventiveRealisasiBudgetArea> = mutableListOf()
        var id:Long = 0
        data.distinctBy { it.area_detail }.forEach {
            item->
            preventiveRealisasiBudgetArea.add(PreventiveRealisasiBudgetArea(id++, item.area_detail?: "-", getPreventiveRealisasiBudget(customer_id, tahun, area_id, item.area_detail, data)))
        }
        return preventiveRealisasiBudgetArea
    }
}
